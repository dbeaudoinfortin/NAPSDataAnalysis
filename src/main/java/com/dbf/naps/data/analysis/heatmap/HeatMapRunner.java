package com.dbf.naps.data.analysis.heatmap;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.analysis.AggregationField;
import com.dbf.naps.data.analysis.DataQueryRecord;
import com.dbf.naps.data.analysis.DataQueryRunner;
import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.MatrixPane;
import eu.hansolo.fx.charts.data.MatrixChartItem;
import eu.hansolo.fx.charts.series.MatrixItemSeries;
import eu.hansolo.fx.heatmap.ColorMapping;

public abstract class HeatMapRunner extends DataQueryRunner<HeatMapOptions> {
	
	private static final Logger log = LoggerFactory.getLogger(HeatMapRunner.class);
	
	public HeatMapRunner(int threadId, HeatMapOptions config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory, dataFile, specificYear, specificPollutant, specificSite);
	}
	
	@Override
	public void writeToFile(List<DataQueryRecord> records, File dataFile) throws IOException {
		log.info("Rendering heatmap for " + dataFile + "...");
		
		//Determine the bounds of the X & Y dimension
		AxisDimensions<?> xDimensions = determineAxisDimensions(records, 0);
		AxisDimensions<?> yDimensions = determineAxisDimensions(records, 1);
		
		//Determine the bounds of the data values
		BigDecimal minValue = records.get(0).getValue();
		BigDecimal maxValue = minValue;
		
		//Convert the data to a format that the rendering library can use
		List<MatrixChartItem> matrixData = new ArrayList<MatrixChartItem>(records.size());
		for (DataQueryRecord record: records) {
			int x = xDimensions.isDistinct() ? xDimensions.getDistinctEntries().get(record.getField_1()) : (int) record.getField_1();
			int y = yDimensions.isDistinct() ? yDimensions.getDistinctEntries().get(record.getField_2()) : (int) record.getField_2();
			matrixData.add(new MatrixChartItem(x-1, y-1, record.getValue().doubleValue()));
			if(record.getValue().compareTo(minValue) < 0) minValue = record.getValue();
			if(record.getValue().compareTo(maxValue) > 0) maxValue = record.getValue();
		}

		//Create the heat map
		MatrixItemSeries<MatrixChartItem> matrixItemSeries = new MatrixItemSeries<MatrixChartItem>(matrixData, ChartType.MATRIX_HEATMAP);
		MatrixPane<MatrixChartItem> matrixHeatMap = new MatrixPane<MatrixChartItem>(matrixItemSeries);
		matrixHeatMap.setColorMapping(ColorMapping.INFRARED_3);
		matrixHeatMap.getMatrix().setSpacerSizeFactor(0.005);
		matrixHeatMap.getMatrix().setUseSpacer(true);
		matrixHeatMap.getMatrix().setColsAndRows(xDimensions.getCount(), yDimensions.getCount());

		double scaleFactor = 4096.0 / (xDimensions.getCount() > yDimensions.getCount() ? xDimensions.getCount() : yDimensions.getCount());
		matrixHeatMap.renderToImage(getDataFile().getAbsolutePath(), (int)(xDimensions.getCount()*scaleFactor), (int)(yDimensions.getCount()*scaleFactor));
		
		log.info("Rendering complete for " + dataFile + ".");
		
		if (getConfig().isIncludeCSV()) {
			File csvFile = new File(dataFile.getParent(), dataFile.getName().replace("png", "csv"));
			super.writeToFile(records, csvFile);
		}
	}

	
	private <T> AxisDimensions<?> determineAxisDimensions(List<DataQueryRecord> records, int index) {
		AggregationField field = getConfig().getFields().get(index);
		
		switch (field) {
		case DAY:
			return new AxisDimensions<Integer>(1, 31, 31, Integer.class);
		case DAY_OF_WEEK:
			return new AxisDimensions<Integer>(1, 7, 7, Integer.class);
		case DAY_OF_YEAR:
			return new AxisDimensions<Integer>(1, 366, 366, Integer.class);
		case HOUR:
			return new AxisDimensions<Integer>(1, 24, 24, Integer.class);
		case MONTH:
			return new AxisDimensions<Integer>(1, 12, 12, Integer.class);
		case WEEK_OF_YEAR:
			return new AxisDimensions<Integer>(1, 53, 53, Integer.class);
		case YEAR: 
			Map<Integer, Integer> distinctYears = new HashMap<Integer, Integer>(records.size());
			int minYear = (int) (index==1 ? records.get(0).getField_1() : records.get(0).getField_2());
			int maxYear = minYear;
			int distinctYearCount = 0;
			for (DataQueryRecord record: records) {
				int year = (int) (index==1 ? record.getField_1() : record.getField_2());
				if(year < minYear) minYear = year;
				if(year > maxYear) maxYear = year;
				if(!distinctYears.containsKey(year)) {
					distinctYears.put(year, distinctYearCount++);
				}
			}
			return new AxisDimensions<Integer>(minYear, maxYear, distinctYears, Integer.class);
		
		case NAPS_ID:
			Map<Integer, Integer> distinctIDs = new HashMap<Integer, Integer>(records.size());
			int distinctIDCount = 0;
			for (DataQueryRecord record: records) {
				Integer value = (Integer)(index==1 ? record.getField_1() : record.getField_2());
				if(!distinctIDs.containsKey(value)) {
					distinctIDs.put(value, distinctIDCount++);
				}
			}
			return new AxisDimensions<Integer>(distinctIDs, Integer.class);
			
		case POLLUTANT:
		case PROVINCE_TERRITORY:
		case URBANIZATION:
		default:
			Map<String, Integer> distinctStrings = new HashMap<String, Integer>(records.size());
			int distinctStringCount = 0;
			for (DataQueryRecord record: records) {
				String value = (index==1 ? record.getField_1() : record.getField_2()).toString();
				if(!distinctStrings.containsKey(value)) {
					distinctStrings.put(value, distinctStringCount++);
				}
			}
			return new AxisDimensions<String>(distinctStrings, String.class);
		}
	}
	
	protected abstract String getDataset();
}
