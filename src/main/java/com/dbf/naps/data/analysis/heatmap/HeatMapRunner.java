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
		//TODO: add support for upper and lower colour bounds
		
		log.info("Analysis complete for " + dataFile + ".");

		log.info("Rendering heatmap graphics for " + dataFile + "...");
		renderHeatMap(dataFile, records, xDimension, yDimension, minValue, maxValue);
		log.info("Rendering complete for " + dataFile + ".");
		
		if (getConfig().isGenerateCSV()) {
			File csvFile = new File(dataFile.getParent(), dataFile.getName().replace("png", "csv"));
			this.checkFile(csvFile);
			super.writeToFile(records, csvFile);
		}
	}
	
	private static void renderHeatMap(File dataFile, List<DataQueryRecord> records, Axis<?> xDimension, Axis<?> yDimension, BigDecimal minValue, BigDecimal maxValue) throws IOException{		
		//Render all of the X & Y labels first so we can determine the maximum size
		final Entry<Integer, Integer> xLabelSize = getMaxLabelSize(xDimension);
		int xLabelHeight = xLabelSize.getKey();
		final int yLabelWidth = getMaxLabelSize(yDimension).getKey();
		
		//Only rotate the x-axis labels when they are too big
		final boolean rotateXLabels = (xLabelHeight-labelPadding) > cellWidth;
		if(!rotateXLabels) {
			xLabelHeight = xLabelSize.getValue();
		}
		
		
		//TODO: render a title
		//TODO: render a legend
		
        final int imageWidth  = (outsidePadding*2) + yLabelWidth + labelPadding + (xDimension.getCount()  * cellWidth);
        final int imageHeight = (outsidePadding*2) + xLabelHeight + labelPadding + (yDimension.getCount()  * cellHeight);
        
        BufferedImage heatmapImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = heatmapImage.createGraphics();
        
        //Set a decent font
        g2d.setFont(font);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
		//Make the background all white
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, imageWidth, imageHeight);

		 try {
			//Will need to determine the width of each label individually
	    	FontMetrics fontMetrics = g2d.getFontMetrics();
	    		
        	//Add all of the x labels, drawn vertically
    		g2d.setColor(Color.BLACK);
    		final int labelStartPosY = outsidePadding + xLabelHeight;
    		
    		//TODO: Render a gentle border between rectangles
    		for (Entry<String, Integer> entry : xDimension.getLabelIndices().entrySet()) {
    			final int labelStartPosX = outsidePadding + yLabelWidth + labelPadding + (entry.getValue() * cellWidth) + halfCellWidth;
    			if(rotateXLabels) {
    				AffineTransform transform = null;
	    			//Store the current transform
	    			transform = g2d.getTransform();
	    			g2d.translate(labelStartPosX, labelStartPosY); //TODO: need to centre this in the x dimension
	    			g2d.rotate(-Math.PI / 2); // Rotate 90 degrees counter-clockwise
	    			
	    			// Draw the x axis label
	    			g2d.drawString(entry.getKey(), 0, 0);
    	
	    			//Restore the old transform
	    			g2d.setTransform(transform);
    			} else {
    				final int labelWidth = fontMetrics.stringWidth(entry.getKey());
    				g2d.drawString(entry.getKey(), labelStartPosX - (labelWidth/2), labelStartPosY);
    			}
    		}
    		
    		//Add all of the y labels, drawn horizontally
    		for (Entry<String, Integer> entry : yDimension.getLabelIndices().entrySet()) {
    			final int labelWidth = fontMetrics.stringWidth(entry.getKey());
    			//Align right
    			g2d.drawString(entry.getKey(), outsidePadding + (yLabelWidth - labelWidth), outsidePadding + xLabelHeight + labelPadding + (entry.getValue() * cellHeight) + halfCellHeight);
    		} //TODO: need to centre this in the y dimension
    		
    		final int xMatrixStart =  outsidePadding + yLabelWidth + labelPadding;
    		final int yMatrixStart =  outsidePadding + xLabelHeight + labelPadding;
    		
    		final double fMinValue   = minValue.doubleValue();
    		final double fMaxValue   = maxValue.doubleValue();
    		final double fValueRange = fMaxValue - fMinValue;
    		
			//Draw the heat map itself
    		for (DataQueryRecord record: records) {
    			final int x = xDimension.getIndex(record.getField_0());
    			final int y = yDimension.getIndex(record.getField_1());
				g2d.setColor(getColourForValue(fValueRange == 0 ? 1.0 : (record.getValue().doubleValue()-fMinValue)/fValueRange));
				g2d.fillRect(xMatrixStart + (x * cellWidth), yMatrixStart+ (y * cellHeight), cellWidth, cellHeight);	
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
	
	//TODO: Max the colours configurable (would help colour-blind people)
	private static Color getColourForValue(double value) {
        float hue = (float) ((1.0 - value) * 240 / 360);  // Blue to red hue
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }
	
	private <T> Axis<?> determineAxisDimensions(List<DataQueryRecord> records, int index) {
		switch (getConfig().getFields().get(index)) {
		case DAY:
			return new IntegerAxis(1, 31);
			
		case DAY_OF_YEAR:
			return new IntegerAxis(1, 366);
			
		case HOUR:
			return new IntegerAxis(1, 24);
			
		case WEEK_OF_YEAR:
			return new IntegerAxis(1, 53);
			
		case DAY_OF_WEEK:
			IntegerAxis dowAxis =  new IntegerAxis();
			dowAxis.addEntry(1, "Sunday");
			dowAxis.addEntry(2, "Monday");
			dowAxis.addEntry(3, "Tuesday");
			dowAxis.addEntry(4, "Wednesday");
			dowAxis.addEntry(5, "Thursday");
			dowAxis.addEntry(6, "Friday");
			dowAxis.addEntry(7, "Saturday");
			return dowAxis;
			
		case MONTH:
			IntegerAxis mAxis =  new IntegerAxis();
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
		case NAPS_ID:
			IntegerAxis iAxis = new IntegerAxis();
			for (DataQueryRecord record: records) {
				int year = (int) (index==0 ? record.getField_0() : record.getField_1());	
				iAxis.addEntry(year, "" + year);
			}
			return iAxis;
		case POLLUTANT:
		case PROVINCE_TERRITORY:
		case URBANIZATION:
		default:
			StringAxis sAxis = new StringAxis();
			for (DataQueryRecord record: records) {
				String value = (index==0 ? record.getField_0() : record.getField_1()).toString();	
				sAxis.addEntry(value, value);
			}
			return sAxis;
		}
	}
	
	protected abstract String getDataset();
}
