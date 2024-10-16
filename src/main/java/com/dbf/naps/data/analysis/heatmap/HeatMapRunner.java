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
import java.util.AbstractMap;
import java.util.List;
import java.util.Map.Entry;
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

public abstract class HeatMapRunner extends DataQueryRunner<HeatMapOptions> {
	
	//TODO: make these configurable
	private static final int cellWidth = 50;
	private static final int cellHeight = 50;
	private static final int halfCellWidth = cellWidth / 2;
	private static final int halfCellHeight = cellHeight / 2;
	
	private static final int labelPadding = 10;
	private static final int outsidePadding = 5;
	
	private static final Font font = new Font("Calibri", Font.BOLD, 20);
	
	private static final Color[] COLOUR_BLIND_GRADIENT = new Color[] {
			Color.decode("#e4ff7a"), //Light Greenish Yellow
			Color.decode("#ffe81a"), //Bright Yellow
			Color.decode("#ffbd00"), //Deep Yellow
			Color.decode("#ffa000"), //Orange
			Color.decode("#fc7f00")  //Darker Orange
	};

	private static final Color[] BASIC_GRADIENT = new Color[] {
			Color.decode("#1d4877"), //Dark Blue
			Color.decode("#1b8a5a"), //Green
			Color.decode("#fbb021"), //Golden Yellow
			Color.decode("#f68838"), //Orange
			Color.decode("#ee3e32")  //Red
	};
	
	private static final Color[] TWO_COLOUR_GRADIENT = new Color[] {
			Color.decode("#0000FF"), //Blue
			Color.decode("#FF0000"), //Red
	};
	
	private static final Logger log = LoggerFactory.getLogger(HeatMapRunner.class);
	
	public HeatMapRunner(int threadId, HeatMapOptions config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory, dataFile, specificYear, specificPollutant, specificSite);
	}
	
	@Override
	public void writeToFile(List<DataQueryRecord> records, File dataFile) throws IOException {
		
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
		final double dMinValue = getConfig().getDataLowerBound() == null ? minValue.doubleValue() : getConfig().getDataLowerBound().doubleValue();
		final double dMaxValue = getConfig().getDataUpperBound() == null ? maxValue.doubleValue() : getConfig().getDataUpperBound().doubleValue();
		log.info("Analysis complete for " + dataFile + ".");

		log.info("Rendering heatmap graphics for " + dataFile + "...");
		renderHeatMap(dataFile, records, xDimension, yDimension, dMinValue, dMaxValue);
		log.info("Rendering complete for " + dataFile + ".");
		
		if (getConfig().isGenerateCSV()) {
			File csvFile = new File(dataFile.getParent(), dataFile.getName().replace("png", "csv"));
			this.checkFile(csvFile);
			super.writeToFile(records, csvFile);
		}
	}
	
	private static void renderHeatMap(File dataFile, List<DataQueryRecord> records, Axis<?> xAxis, Axis<?> yAxis, double minValue, double maxValue) throws IOException{		
		//Render all of the X & Y labels first so we can determine the maximum size
		final Entry<Integer, Integer> xAxisLabelSizes = getMaxLabelSize(xAxis);
		final int yAxisLabelWidth  = getMaxLabelSize(yAxis).getKey();
		int xAxisLabelHeight = xAxisLabelSizes.getKey(); //Assume rotated by default
		
		//Only rotate the x-axis labels when they are too big
		final boolean rotateXLabels = (xAxisLabelHeight - labelPadding) > cellWidth;
		if(!rotateXLabels) {
			xAxisLabelHeight = xAxisLabelSizes.getValue();
		}
		
		//Since the font height is the same for all basic text, we can use the axis labels
		final int basicFontHeight = xAxisLabelSizes.getValue();
		
		//X positional values
		final int yAxisTitleStartPosX = outsidePadding + basicFontHeight;
		final int yAxisLabelStartPosX = yAxisTitleStartPosX + (labelPadding*2);
		final int xMatrixStart = yAxisLabelStartPosX + yAxisLabelWidth + labelPadding;
		final int matixWidth = (xAxis.getCount()  * cellWidth);
		final int xAxisLabelStartPosX = xMatrixStart;
		
		//Y positional values
		final int xAxisTitleStartPosY = outsidePadding + basicFontHeight; //Label Positions are bottom left!!
		final int xAxisLabelStartPosY = xAxisTitleStartPosY + (labelPadding*2) + xAxisLabelHeight;
		final int yMatrixStart = xAxisLabelStartPosY + labelPadding;
		final int matixHeight = (yAxis.getCount()  * cellHeight);
		final int yAxisLabelStartPosY = yMatrixStart;
		
		
		//TODO: render a legend
		
		//Outside padding + Y Axis Title + label padding + Y Axis Labels + label padding + chart width + label padding + legend width + outside padding
        final int imageWidth = xMatrixStart + matixWidth + outsidePadding;
        
        //Outside padding + X Axis Title + label padding + X Axis Labels + label padding + chart height + outside padding
        final int imageHeight = yMatrixStart + matixHeight + outsidePadding;
        
        BufferedImage heatmapImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = heatmapImage.createGraphics();
        
		 try {
			//Set a decent font
	        g2d.setFont(font);
	        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	        
			//Make the background all white
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, imageWidth, imageHeight);
				
			//Will need to determine the width of each label individually
	    	FontMetrics fontMetrics = g2d.getFontMetrics();
	    		
        	//Add all of the x labels, drawn vertically
    		g2d.setColor(Color.BLACK);
    		
    		//Render the X-axis title
    		//TODO: Wrap the title if it's too long
    		final int xAxisTitleWidth = fontMetrics.stringWidth(xAxis.getTitle());
    		g2d.drawString(xAxis.getTitle(), xMatrixStart + (matixWidth/2) - (xAxisTitleWidth/2), xAxisTitleStartPosY);
    		
    		//Render the Y-axis title
    		//TODO: Wrap the title if it's too long
    		final int yAxisTitleWidth = fontMetrics.stringWidth(yAxis.getTitle());
    		AffineTransform transform = g2d.getTransform();
    		g2d.translate(yAxisTitleStartPosX, yMatrixStart + (matixHeight/2) - (yAxisTitleWidth/2));
			g2d.rotate(-Math.PI / 2); // Rotate 90 degrees counter-clockwise
    		g2d.drawString(yAxis.getTitle(), 0, 0);
    		g2d.setTransform(transform);
    		
    		//TODO: Render a gentle border between rectangles? As an option?
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
    			g2d.drawString(entry.getKey(), yAxisLabelStartPosX + (yAxisLabelWidth - labelWidth), yAxisLabelStartPosY + (basicFontHeight/3) + (entry.getValue() * cellHeight) + halfCellHeight);
    		}
    		
    		
    		final double valueRange = maxValue - minValue;
    		
			//Draw the heat map itself
    		for (DataQueryRecord record: records) {
    			final int x = xAxis.getIndex(record.getField_0());
    			final int y = yAxis.getIndex(record.getField_1());
    			
    			//Determine the colour for this square of the map
				//g2d.setColor(getColourForValueStops(valueRange == 0 ? 1.0 : (record.getValue().doubleValue()-minValue) / valueRange, TWO_COLOUR_GRADIENT));
				g2d.setColor(getColourForValueSmooth(valueRange == 0 ? 1.0 : (record.getValue().doubleValue()-minValue) / valueRange));
				g2d.fillRect(xMatrixStart + (x * cellWidth), yMatrixStart + (y * cellHeight), cellWidth, cellHeight);	
			}
    		
    		ImageIO.write(heatmapImage, "png", dataFile);
        } finally {
        	g2d.dispose();
        }
	}
	
	private static Entry<Integer, Integer> getMaxLabelSize(Axis<?> axis) {
		//Create a temporary image to get Graphics2D context for measuring
        BufferedImage tinyImage = new BufferedImage(1, 1, BufferedImage.BITMASK);
        Graphics2D g2d = tinyImage.createGraphics();
        int maxLabelLength = 0;
        int labelHeight = 0;
        try {
        	 g2d.setFont(font);
        	 g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
             FontMetrics fontMetrics = g2d.getFontMetrics();
             
             //Assume it never changes
             labelHeight = fontMetrics.getHeight();
             
     		//Note: the max label size is not necessarily the one with the most characters.
     		for (String label : axis.getEntryLabels().values()) {
     			final int labelWidth = fontMetrics.stringWidth(label);
     			if (labelWidth > maxLabelLength) {
     				maxLabelLength = labelWidth;
     			}
     		}
        } finally {
        	g2d.dispose();
        }
        return new AbstractMap.SimpleEntry<Integer, Integer>(maxLabelLength, labelHeight);
	}
	
	private static Color getColourForValueSmooth(double value) {
        float hue = (float) ((1.0 - value) * 240 / 360);  // Blue to red hue
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }
	
	public static Color getColourForValueStops(double value, Color[] stops) {
        //Determine between which colours we sit
        double scaledPosition = value * (stops.length - 1);
        int stopIndex = (int) Math.floor(scaledPosition);
        if (stopIndex == stops.length -1) {
        	return stops[stopIndex];
        }
        Color colour1 = stops[stopIndex];
        Color colour2 = stops[stopIndex + 1];

        //Linearly interpolate between the two stops
        double stopFraction = scaledPosition - stopIndex;
        int r = (int) (colour1.getRed() * (1 - stopFraction) + colour2.getRed() * stopFraction);
        int g = (int) (colour1.getGreen() * (1 - stopFraction) + colour2.getGreen() * stopFraction);
        int b = (int) (colour1.getBlue() * (1 - stopFraction) + colour2.getBlue() * stopFraction);
        return new Color(r, g, b);
    }
	
	private <T> Axis<?> determineAxisDimensions(List<DataQueryRecord> records, int index) {
		switch (getConfig().getFields().get(index)) {
		case DAY:
			return new IntegerAxis("Day of the Month", 1, 31);
			
		case DAY_OF_YEAR:
			return new IntegerAxis("Day of the Year", 1, 366);
			
		case HOUR:
			return new IntegerAxis("Hour", 1, 24);
			
		case WEEK_OF_YEAR:
			return new IntegerAxis("Week of the Year", 1, 53);
			
		case DAY_OF_WEEK:
			IntegerAxis dowAxis =  new IntegerAxis("Day of the Week");
			dowAxis.addEntry(1, "Sunday");
			dowAxis.addEntry(2, "Monday");
			dowAxis.addEntry(3, "Tuesday");
			dowAxis.addEntry(4, "Wednesday");
			dowAxis.addEntry(5, "Thursday");
			dowAxis.addEntry(6, "Friday");
			dowAxis.addEntry(7, "Saturday");
			return dowAxis;
			
		case MONTH:
			IntegerAxis mAxis =  new IntegerAxis("Month");
			mAxis.addEntry(1, "January");
			mAxis.addEntry(2, "February");
			mAxis.addEntry(3, "March");
			mAxis.addEntry(4, "April");
			mAxis.addEntry(5, "May");
			mAxis.addEntry(6, "June");
			mAxis.addEntry(7, "July");
			mAxis.addEntry(8, "August");
			mAxis.addEntry(9, "September");
			mAxis.addEntry(10, "October");
			mAxis.addEntry(11, "November");
			mAxis.addEntry(12, "December");
			return mAxis;
			
		case YEAR:
			return new IntegerAxis("Year", sortAxisEntries(records, index));
		case NAPS_ID:
			return new IntegerAxis("NAPS Site ID", sortAxisEntries(records, index));
		case POLLUTANT:
			return new StringAxis("Pollutant", sortAxisEntries(records, index));
		case PROVINCE_TERRITORY:
			return new StringAxis("Province/Territory", sortAxisEntries(records, index));
		case URBANIZATION:
			StringAxis sAxis =  new StringAxis("Urbanization");
			sAxis.addEntry("LU", "Large Urban");
			sAxis.addEntry("MU", "Medium Urban");
			sAxis.addEntry("SU", "Small Urban");
			sAxis.addEntry("NU", "Rural");
			return sAxis;
		case SITE_TYPE:
			StringAxis stAxis =  new StringAxis("Site Type");
			stAxis.addEntry("PE", "General Population");
			stAxis.addEntry("RB", "Regional Backgrounds");
			stAxis.addEntry("T",  "Transportation");
			stAxis.addEntry("PS", "Point source");
			return stAxis;
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
