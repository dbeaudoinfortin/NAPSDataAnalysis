package com.dbf.naps.data.analysis.heatmap;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.analysis.DataQueryRecord;
import com.dbf.naps.data.analysis.DataQueryRunner;
import com.dbf.naps.data.analysis.heatmap.axis.Axis;
import com.dbf.naps.data.analysis.heatmap.axis.IntegerAxis;
import com.dbf.naps.data.analysis.heatmap.axis.StringAxis;
import com.dbf.naps.data.globals.DayOfWeekMapping;
import com.dbf.naps.data.globals.MonthMapping;
import com.dbf.naps.data.globals.SiteType;
import com.dbf.naps.data.globals.SiteTypeMapping;
import com.dbf.naps.data.globals.Urbanization;
import com.dbf.naps.data.globals.UrbanizationMapping;

public abstract class HeatMapRunner extends DataQueryRunner<HeatMapOptions> {
	
	//TODO: make these configurable
	private static final int cellWidth  = 50;
	private static final int cellHeight = 50;
	private static final int halfCellWidth  = cellWidth  / 2;
	private static final int halfCellHeight = cellHeight / 2;
	
	private static final int labelPadding   = 10;
	private static final int chartTitlePadding = labelPadding*4;
	private static final int outsidePadding = 5;
	
	//Legend
	private static final int    legendPadding = chartTitlePadding;
	private static final String legendFormat = "0.####";
	
	private static final Font basicFont = new Font("Calibri", Font.BOLD, 20);
	private static final Font titleFont = new Font("Calibri", Font.BOLD, 36);
	
	private static final Logger log = LoggerFactory.getLogger(HeatMapRunner.class);
	
	public HeatMapRunner(int threadId, HeatMapOptions config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory, dataFile, specificYear, specificPollutant, specificSite);
	}
	
	@Override
	public void writeToFile(List<DataQueryRecord> records, String queryUnits, String title, File dataFile) throws IOException {
		
		log.info("Analyzing heatmap data for " + dataFile + "...");
		//Determine the bounds of the X & Y dimension
		Axis<?> xDimension = determineAxisDimensions(records, 0);
		Axis<?> yDimension = determineAxisDimensions(records, 1);
		
		//Determine the bounds of the data values
		BigDecimal minValue = records.get(0).getValue();
		BigDecimal maxValue = minValue;
		for (DataQueryRecord record: records) {
			if(record.getValue().compareTo(minValue) < 0) minValue = record.getValue();
			if(record.getValue().compareTo(maxValue) > 0) maxValue = record.getValue();
		}
		final double dMinValue = getConfig().getColourLowerBound() == null ? minValue.doubleValue() : getConfig().getColourLowerBound().doubleValue();
		final double dMaxValue = getConfig().getColourUpperBound() == null ? maxValue.doubleValue() : getConfig().getColourUpperBound().doubleValue();
		log.info("Analysis complete for " + dataFile + ".");

		log.info("Rendering heatmap graphics for " + dataFile + "...");
		renderHeatMap(dataFile, records, xDimension, yDimension, dMinValue, dMaxValue, getConfig().getColourLowerBound()!=null, getConfig().getColourUpperBound()!=null, title, getConfig().getColourGradient());
		log.info("Rendering complete for " + dataFile + ".");
		
		if (getConfig().isGenerateCSV()) {
			File csvFile = new File(dataFile.getParent(), dataFile.getName().replace("png", "csv"));
			this.checkFile(csvFile);
			super.writeToFile(records, queryUnits, title, csvFile);
		}
	}
	
	private static void renderHeatMap(File dataFile, List<DataQueryRecord> records, Axis<?> xAxis, Axis<?> yAxis, double minValue, double maxValue, boolean minClamped, boolean maxClamped, String title, int colourGradient) throws IOException{		
		//Render all of the X & Y labels first so we can determine the maximum size
		final Entry<Integer, Integer> xAxisLabelMaxSize = getMaxStringSize(xAxis.getEntryLabels().values());
		final int yAxisLabelMaxWidth  = getMaxStringSize(yAxis.getEntryLabels().values()).getKey();
		int xAxisLabelHeight = xAxisLabelMaxSize.getKey(); //Assume rotated by default
		
		//Only rotate the x-axis labels when they are too big
		final boolean rotateXLabels = (xAxisLabelHeight - labelPadding) > cellWidth;
		if(!rotateXLabels) {
			xAxisLabelHeight = xAxisLabelMaxSize.getValue();
		}
		
		//Since the font height is the same for all basic text, we can use the axis labels
		final int basicFontHeight = xAxisLabelMaxSize.getValue();
		
		//Calculate the legend values
		final int    legendBoxes = yAxis.getCount() > 5 ? yAxis.getCount() : 5; //Must be at least 5
		final double valueRange  = maxValue - minValue;
		final double legendSteps = valueRange > 0 ? valueRange / (legendBoxes-1) : 0;
		final List<Double> legendvalues = new ArrayList<Double>(legendBoxes);
		legendvalues.add(minValue);
			for(int i = 1; i < legendBoxes -1; i++) {
				legendvalues.add(minValue + (i*legendSteps));
			}
		legendvalues.add(maxValue);
		
		//Calculate the legend labels
		final DecimalFormat legendDF = new DecimalFormat(legendFormat); //Not thread safe, don't make static
		final List<String> legendLabels = legendvalues.stream().map(v->legendDF.format(v)).collect(Collectors.toList());
		//We need to indicate in the legend if the values are being capped/bounded/clamped
		if(minClamped) legendLabels.set(0, "<= " + legendLabels.get(0));
		if(maxClamped) legendLabels.set(legendvalues.size()-1, ">= " + legendLabels.get(legendvalues.size()-1));
		
		//Calculate legend sizes
		final int legendHeight = cellHeight * legendBoxes;
		final int legendLabelMaxWidth = getMaxStringSize(legendLabels).getKey();
		final int legendWidth = cellWidth + labelPadding + legendLabelMaxWidth;
		
		//Calculate the X positional values, first
		final int yAxisTitleStartPosX = outsidePadding + basicFontHeight;
		final int yAxisLabelStartPosX = yAxisTitleStartPosX + (labelPadding*2);
		final int matrixStartPosX = yAxisLabelStartPosX + yAxisLabelMaxWidth + labelPadding;
		final int matrixWidth = (xAxis.getCount()  * cellWidth);
		final int matrixCentreX =  matrixStartPosX + (matrixWidth/2);
		final int xAxisLabelStartPosX = matrixStartPosX;
		final int legendStartPosX = matrixStartPosX + matrixWidth + legendPadding;
		final int legendLabelStartPosX = legendStartPosX + cellWidth + labelPadding;
		
		//Calculate the overall image width
		//Outside padding + Y Axis Title + label padding + Y Axis Labels + label padding + chart width + legend padding + legend width + outside padding
        final int imageWidth   = legendStartPosX + legendWidth + outsidePadding;
        final int imageCenterY = imageWidth/2;
        
        //Now that we know the image width we can figure out if we need to wrap the text of the big chart title
        final int chartTitleMaxWidth = imageWidth - (outsidePadding*2);
        List<Entry<String, Entry<Integer, Integer>>> titleLines = null;
        if(!title.isEmpty()) titleLines = getTitleSized(title, chartTitleMaxWidth); //Empty chart titles are supported
        final int chartTitleLineHeight = title.isEmpty() ? 0 : titleLines.get(0).getValue().getValue();
        final int chartTitleHeight = title.isEmpty() ? 0 : titleLines.size() * chartTitleLineHeight;
        
		//Now that we know the big chart title height, we can calculate the Y positional values
        final int chartTitleStartPosY = outsidePadding;
		final int xAxisTitleStartPosY = chartTitleStartPosY + chartTitleHeight + chartTitlePadding + basicFontHeight; //Label positions are bottom left!!
		final int xAxisLabelStartPosY = xAxisTitleStartPosY + (labelPadding*2) + xAxisLabelHeight;
		final int matrixStartPosY = xAxisLabelStartPosY + labelPadding;
		final int matrixHeight = (yAxis.getCount()  * cellHeight);
		final int matrixCentreY =  matrixStartPosY + (matrixHeight/2);
		final int yAxisLabelStartPosY = matrixStartPosY;
		final int legendStartPosY = (matrixHeight>=legendHeight) ? (matrixCentreY - (legendHeight/2)) : matrixStartPosY; //Legend is centred with the Matrix only if the matrix is big enough
		final int legendLabelStartPosY = legendStartPosY + basicFontHeight; //Label positions are bottom left!!
		
        //Finally, we can figure out the overall image height
        //Outside padding + big title + title padding + X Axis Title + label padding + X Axis Labels + label padding + chart height + outside padding
        final int imageHeight = matrixStartPosY + Math.max(matrixHeight, legendHeight) + outsidePadding;
        
        BufferedImage heatmapImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = heatmapImage.createGraphics();
        
		 try {
			//Make the background all white
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, imageWidth, imageHeight);
				
			//Render the text smoothly
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			
			//Render the chart title
			if(!title.isEmpty()) {
				//Set the title font
				g2d.setFont(titleFont);
				g2d.setColor(Color.BLACK);
				for (int i = 0; i < titleLines.size(); i++) {
					Entry<String, Entry<Integer, Integer>> line = titleLines.get(i);
					// Label positions are bottom left so we need to add 1 to the line number
					final int linePosY = chartTitleStartPosY + ((i + 1) * chartTitleLineHeight);
					//Centre each line horizontally
					g2d.drawString(line.getKey(), imageCenterY - (line.getValue().getKey()/2), linePosY);
				}
			}
			
			//Reset to a decent basic font
	        g2d.setFont(basicFont);
	
			//Will will need to determine the width of each label individually using fontMetrics
	    	FontMetrics fontMetrics = g2d.getFontMetrics();
    		
    		//Render the legend labels
    		//The number of legend boxes may be greater than the number of labels
	    	g2d.setColor(Color.BLACK);
    		g2d.drawString(legendLabels.get(legendLabels.size()-1), legendLabelStartPosX, legendLabelStartPosY); //First
    		g2d.drawString(legendLabels.get(0), legendLabelStartPosX, legendLabelStartPosY + (cellHeight * (legendBoxes-1))); //Last
    		if(valueRange > 0 ) {
    			//Only render the rest of the labels if there is a range to the colours
    			for(int i = 1; i < legendBoxes-1; i++) {
    				g2d.drawString(legendLabels.get(legendBoxes-i-1), legendLabelStartPosX, legendLabelStartPosY + (cellHeight * (i)));
    			}
    		}
    		
    		//Render the legend boxes, starting with the bottom (minimum) first
    		for(int i = 0; i < legendBoxes; i++) {
    			if(i == 0) {
    				g2d.setColor(HeatMapGradient.getColour(1.0, colourGradient));
    			} else if (i == legendBoxes -1) {
    				g2d.setColor(HeatMapGradient.getColour(0.0, colourGradient));
    			} else {
    				g2d.setColor(HeatMapGradient.getColour((1-(legendvalues.get(i)-minValue)/valueRange), colourGradient));
    			}
				g2d.fillRect(legendStartPosX, legendStartPosY + (i * cellHeight), cellWidth, cellHeight);	
    		}
    		
    		//Render the X-axis title
    		//TODO: Wrap the title if it's too long
    		final int xAxisTitleWidth = fontMetrics.stringWidth(xAxis.getTitle());
    		g2d.setColor(Color.BLACK);
    		g2d.drawString(xAxis.getTitle(), matrixCentreX - (xAxisTitleWidth/2), xAxisTitleStartPosY);
    		
    		//Render the Y-axis title
    		//TODO: Wrap the title if it's too long
    		final int yAxisTitleWidth = fontMetrics.stringWidth(yAxis.getTitle());
    		AffineTransform transform = g2d.getTransform();
    		g2d.translate(yAxisTitleStartPosX, matrixCentreY + (yAxisTitleWidth/2));
			g2d.rotate(-Math.PI / 2); // Rotate 90 degrees counter-clockwise
    		g2d.drawString(yAxis.getTitle(), 0, 0);
    		g2d.setTransform(transform);
    		
    		//Add all of the x labels, drawn vertically or horizontally
    		for (Entry<String, Integer> entry : xAxis.getLabelIndices().entrySet()) {
    			if(rotateXLabels) {
	    			//Store the current transform
	    			transform = g2d.getTransform();
	    			g2d.translate(xAxisLabelStartPosX + (entry.getValue() * cellWidth) + halfCellWidth, xAxisLabelStartPosY); //TODO: need to centre this in the x dimension
	    			g2d.rotate(-Math.PI / 2); // Rotate 90 degrees counter-clockwise
	    			
	    			// Draw the x axis label
	    			g2d.drawString(entry.getKey(), 0, 0);
    	
	    			//Restore the old transform
	    			g2d.setTransform(transform);
    			} else {
    				final int labelWidth = fontMetrics.stringWidth(entry.getKey());
    				g2d.drawString(entry.getKey(), xAxisLabelStartPosX - (labelWidth/2) + (entry.getValue() * cellWidth) + halfCellWidth, xAxisLabelStartPosY);
    			}
    		}
    		
    		//Add all of the y labels, drawn horizontally
    		for (Entry<String, Integer> entry : yAxis.getLabelIndices().entrySet()) {
    			final int labelWidth = fontMetrics.stringWidth(entry.getKey());
    			//Align right
    			g2d.drawString(entry.getKey(), yAxisLabelStartPosX + (yAxisLabelMaxWidth - labelWidth), yAxisLabelStartPosY + (basicFontHeight/3) + (entry.getValue() * cellHeight) + halfCellHeight);
    		}
    		
			//Draw the heat map itself
    		for (DataQueryRecord record: records) {
    			final int x = xAxis.getIndex(record.getField_0());
    			final int y = yAxis.getIndex(record.getField_1());
    			
    			//Determine the colour for this square of the map
				final double val = minClamped || maxClamped ? Math.max(Math.min(record.getValue().doubleValue(), maxValue), minValue) : record.getValue().doubleValue();
				g2d.setColor(HeatMapGradient.getColour((valueRange == 0 ? 1.0 : (val-minValue) / valueRange), colourGradient));
				g2d.fillRect(matrixStartPosX + (x * cellWidth), matrixStartPosY + (y * cellHeight), cellWidth, cellHeight);	
			}
    		
    		ImageIO.write(heatmapImage, "png", dataFile);
        } finally {
        	g2d.dispose();
        }
	}
	
	private static Entry<Integer, Integer> getMaxStringSize(Collection<String> strings) {
		//Create a temporary image to get Graphics2D context for measuring
        BufferedImage tinyImage = new BufferedImage(1, 1, BufferedImage.BITMASK);
        Graphics2D g2d = tinyImage.createGraphics();
        int maxLabelLength = 0;
        int labelHeight = 0;
        try {
        	 g2d.setFont(basicFont);
        	 g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
             FontMetrics fontMetrics = g2d.getFontMetrics();
             
             //Assume it never changes
             labelHeight = fontMetrics.getHeight();
             
     		//Note: the max label size is not necessarily the one with the most characters.
             maxLabelLength = getLongestStringLength(strings, fontMetrics );
        } finally {
        	g2d.dispose();
        }
        return new AbstractMap.SimpleEntry<Integer, Integer>(maxLabelLength, labelHeight);
	}
	
	private static int getLongestStringLength(Collection<String> strings, FontMetrics fontMetrics) {
		return strings.stream().map(s->fontMetrics.stringWidth(s)).max(Integer::compareTo).orElse(0);
	}
	
	private static List<Entry<String, Entry<Integer, Integer>>> getTitleSized(String title, int maxWidth) {
		//Create a temporary image to get Graphics2D context for measuring
        BufferedImage tinyImage = new BufferedImage(1, 1, BufferedImage.BITMASK);
        Graphics2D g2d = tinyImage.createGraphics();
        List<Entry<String, Entry<Integer, Integer>>> titleLines = new ArrayList<Entry<String, Entry<Integer, Integer>>>();
        try {
        	 g2d.setFont(titleFont);
        	 g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
             FontMetrics fontMetrics = g2d.getFontMetrics();
             
             int fontHeight = fontMetrics.getHeight();
             
             //Handle the case where the title is too long to fit on one line
             String[] words = title.split(" ");
             StringBuilder currentLine = new StringBuilder();
             String currentLineString;
             int lineWidth;
             
             for (String word : words) {
            	 //always add the first word to the line. Otherwise, we'd get stuck in a loop if the first word is too long
            	 if(currentLine.length() == 0) {
            		 currentLine.append(word);
            		 continue;
            	 }
            	 
                 //Check the width of the current line with the next word
            	 int previousCharLen = currentLine.length();
            	 currentLine.append(" ");
            	 currentLine.append(word);
                
            	 currentLineString = currentLine.toString();
                 lineWidth = fontMetrics.stringWidth(currentLineString);
                 if (lineWidth < maxWidth) continue; //More room is left
                 
                 if (lineWidth == maxWidth) {
                	 titleLines.add(new AbstractMap.SimpleEntry<String, Entry<Integer, Integer>>(currentLineString,  new AbstractMap.SimpleEntry<Integer, Integer>(lineWidth, fontHeight)));
                	 currentLine.setLength(0);
                	 continue;
                 }
                 
                 //We have exceeded our maximum. We need to roll back the string builder.
                 currentLine.setLength(previousCharLen);
                 currentLineString = currentLine.toString();
                 lineWidth = fontMetrics.stringWidth(currentLineString);
                 titleLines.add(new AbstractMap.SimpleEntry<String, Entry<Integer, Integer>>(currentLineString,  new AbstractMap.SimpleEntry<Integer, Integer>(lineWidth, fontHeight)));
                 
                 //Reset the string builder for the next line
                 currentLine.setLength(0);
                 currentLine.append(word);
             }

             // Add the last line
             if (currentLine.length() > 0) {
            	 currentLineString = currentLine.toString();
                 lineWidth = fontMetrics.stringWidth(currentLineString);
                 titleLines.add(new AbstractMap.SimpleEntry<String, Entry<Integer, Integer>>(currentLineString, new AbstractMap.SimpleEntry<Integer, Integer>(lineWidth, fontHeight)));
             }
        } finally {
        	g2d.dispose();
        }
        return titleLines;
	}

	private <T> Axis<?> determineAxisDimensions(List<DataQueryRecord> records, int index) {
		String prettyName = getConfig().getFields().get(index).getPrettyName();
		
		switch (getConfig().getFields().get(index)) {
		case DAY:
			return new IntegerAxis(prettyName, 1, 31);
			
		case DAY_OF_YEAR:
			return new IntegerAxis(prettyName, 1, 366);
			
		case HOUR:
			return new IntegerAxis(prettyName, 1, 24);
			
		case WEEK_OF_YEAR:
			return new IntegerAxis(prettyName, 1, 53);
			
		case DAY_OF_WEEK:
			IntegerAxis dowAxis =  new IntegerAxis(prettyName);
			for(int day = 1; day < 8; day++) {
				dowAxis.addEntry(day, DayOfWeekMapping.getDayOfWeek(day));
			}
			return dowAxis;
			
		case MONTH:
			IntegerAxis mAxis =  new IntegerAxis(prettyName);
			for(int month = 1; month < 13; month++) {
				mAxis.addEntry(month, MonthMapping.getMonth(month));
			}
			return mAxis;
			
		case URBANIZATION:
			StringAxis sAxis =  new StringAxis(prettyName);
			Stream.of(Urbanization.values())
				.forEach(entry-> sAxis.addEntry(entry.toString(), UrbanizationMapping.getUrbanization(entry)));
			return sAxis;
			
		case SITE_TYPE:
			StringAxis stAxis =  new StringAxis(prettyName);
			Stream.of(SiteType.values())
				.forEach(entry-> stAxis.addEntry(entry.toString(), SiteTypeMapping.getSiteType(entry)));
			return stAxis;
			
		case YEAR:
		case NAPS_ID:
			return new IntegerAxis(prettyName, sortAxisEntries(records, index));
		case POLLUTANT:
		case PROVINCE_TERRITORY:
			return new StringAxis(prettyName, sortAxisEntries(records, index));
		default:
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> Set<T> sortAxisEntries(List<DataQueryRecord> records, int index) {
		Set<T> sortedEntries = new TreeSet<T>(); //Need to order the entries
		records.stream().forEach(r->sortedEntries.add((T) (index == 0 ? r.getField_0() : r.getField_1())));
		return sortedEntries;
	}
	
	protected abstract String getDataset();
}
