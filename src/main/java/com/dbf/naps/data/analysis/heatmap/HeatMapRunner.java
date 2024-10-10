package com.dbf.naps.data.analysis.heatmap;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.analysis.AggregationField;
import com.dbf.naps.data.analysis.DataQueryRecord;
import com.dbf.naps.data.db.DBRunner;
import com.dbf.naps.data.db.mappers.DataMapper;

import eu.hansolo.fx.charts.ChartType;
import eu.hansolo.fx.charts.MatrixPane;
import eu.hansolo.fx.charts.data.MatrixChartItem;
import eu.hansolo.fx.charts.series.MatrixItemSeries;
import eu.hansolo.fx.heatmap.ColorMapping;

public abstract class HeatMapRunner extends DBRunner<HeatMapOptions> {
	
	private static final Logger log = LoggerFactory.getLogger(HeatMapRunner.class);
	
	private File dataFile;
	private Integer specificYear;
	private String specificPollutant;
	private Integer specificSite;
		
	public HeatMapRunner(int threadId, HeatMapOptions config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory);
		this.dataFile = dataFile;
		this.specificYear = specificYear;
		this.specificPollutant = specificPollutant;
		this.specificSite = specificSite;
	}
	
	@Override
	public void run() {
		
		try {
			log.info(getThreadId() + ":: Starting Heat Map generation for file " + dataFile + ".");
			generateHeatMap();
			log.info(getThreadId() + ":: Completed Heat Map generation for file " + dataFile + ".");
		 } catch (Throwable t) {
			 log.error(getThreadId() + ":: ERROR Heat Map generation for file " + dataFile + ".", t);
			return;
		 }
	}

	public void generateHeatMap() {		
		if(dataFile.isDirectory()) {
			throw new IllegalArgumentException("File path is a directory: " + dataFile);
		} else if (dataFile.isFile()) {
			if(getConfig().isOverwriteFiles()) {
				log.warn(getThreadId() + ":: Deleting existing file " + dataFile + ".");
				dataFile.delete();
			} else {
				throw new IllegalArgumentException("Cannot output to path \"" + dataFile +"\". The file already exists and the overwrite flag is set to false.");
			}
		}
		
		List<DataQueryRecord> records;
		try(SqlSession session = getSqlSessionFactory().openSession(true)) {
			records = session.getMapper(getDataMapper()).getQueryData(
				getConfig().getDimensions(),
				getConfig().getAggregateFunction(),
				specificYear != null ? List.of(specificYear) : IntStream.range(getConfig().getYearStart(), getConfig().getYearEnd() + 1).boxed().toList(),
				specificPollutant != null ? List.of(specificPollutant) : getConfig().getPollutants(),
				specificSite != null ? List.of(specificSite) : getConfig().getSites());
		}
		
		if(records == null || records.isEmpty()) {
			log.info(getThreadId() + ":: No data records found for " + dataFile + ". Skipping file.");
			return;
		}
		
		log.info("Found " + records.size() + " data record(s).");
	
		log.info("Rendering heatmap for " + dataFile + "...");
		renderHeatMap(records);
		log.info("Rendering complete for " + dataFile + ".");
	}
	
	private void renderHeatMap(List<DataQueryRecord> records) {
		
		//Determine the bounds of the X & Y dimension
		AxisDimensions<?> xDimensions = determineAxisDimensions(getConfig().getDimensions().get(0), records, true);
		AxisDimensions<?> yDimensions = determineAxisDimensions(getConfig().getDimensions().get(1), records, false);
		
		//Determine the bounds of the data values
		BigDecimal minValue = records.get(0).getValue();
		BigDecimal maxValue = minValue;
		
		//Convert the data to a format that the rendering library can use
		List<MatrixChartItem> matrixData = new ArrayList<MatrixChartItem>(records.size());
		for (DataQueryRecord record: records) {
			int x = xDimensions.isDistinct() ? xDimensions.getDistinctEntries().get(record.getX()) : (int) record.getX();
			int y = yDimensions.isDistinct() ? yDimensions.getDistinctEntries().get(record.getY()) : (int) record.getY();
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
		matrixHeatMap.renderToImage(dataFile.getAbsolutePath(), (int)(xDimensions.getCount()*scaleFactor), (int)(yDimensions.getCount()*scaleFactor));
	}

	
	private <T> AxisDimensions<?> determineAxisDimensions(AggregationField field, List<DataQueryRecord> records, boolean xField) {
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
			int minYear = (int) (xField? records.get(0).getX() : records.get(0).getY());
			int maxYear = minYear;
			int distinctYearCount = 0;
			for (DataQueryRecord record: records) {
				int year = (int) (xField? record.getX() : record.getY());
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
				Integer value = (Integer)(xField? record.getX() : record.getY());
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
				String value = (xField? record.getX() : record.getY()).toString();
				if(!distinctStrings.containsKey(value)) {
					distinctStrings.put(value, distinctStringCount++);
				}
			}
			return new AxisDimensions<String>(distinctStrings, String.class);
		}
	}
	
	
	protected abstract Class<? extends DataMapper> getDataMapper();
	
	protected abstract String getDataset();
}
