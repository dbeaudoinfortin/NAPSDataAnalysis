package com.dbf.naps.data.analysis.heatmap;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.heatmaps.HeatMap;
import com.dbf.heatmaps.HeatMapGradient;
import com.dbf.heatmaps.axis.Axis;
import com.dbf.heatmaps.axis.IntegerAxis;
import com.dbf.heatmaps.axis.StringAxis;
import com.dbf.heatmaps.data.DataRecord;
import com.dbf.naps.data.analysis.DataAnalysisRecord;
import com.dbf.naps.data.analysis.DataAnalysisRunner;
import com.dbf.naps.data.globals.DayOfWeekMapping;
import com.dbf.naps.data.globals.MonthMapping;
import com.dbf.naps.data.globals.SiteType;
import com.dbf.naps.data.globals.SiteTypeMapping;
import com.dbf.naps.data.globals.Urbanization;
import com.dbf.naps.data.globals.UrbanizationMapping;

public abstract class HeatMapRunner extends DataAnalysisRunner<HeatMapOptions> {
	
	private static final Logger log = LoggerFactory.getLogger(HeatMapRunner.class);
	
	public HeatMapRunner(int threadId, HeatMapOptions config, SqlSessionFactory sqlSessionFactory, File dataFile, Integer specificYear, String specificPollutant, Integer specificSite) {
		super(threadId, config, sqlSessionFactory, dataFile, specificYear, specificPollutant, specificSite);
	}
	
	@Override
	public void writeToFile(List<DataAnalysisRecord> records, String queryUnits, String title, File dataFile) throws IOException {
		
		log.info("Analyzing heat map data for " + dataFile + "...");
		//Determine the bounds of the X & Y dimension
		Axis<?> xAxis = determineAxis(records, 0);
		Axis<?> yAxis = determineAxis(records, 1);
		log.info("Analysis complete for " + dataFile + ".");
		
		log.info("Rendering heat map graphics for " + dataFile + "...");
		String shortTitle = getReportTitle(queryUnits, false);
		 HeatMap.builder()
			.withTitle(shortTitle)
			.withXAxis(xAxis)
			.withYAxis(yAxis)
			.withOptions(com.dbf.heatmaps.HeatMapOptions.builder()
					.withColourScaleLowerBound(getConfig().getColourLowerBound())
					.withColourScaleUpperBound(getConfig().getColourUpperBound())
					.withShowGridlines(getConfig().isGridLines())
					.withShowGridValues(getConfig().isGridValues())
					.withLegendTextFormat(getConfig().getDigits() < 1 ? "0" : StringUtils.rightPad("0.", getConfig().getDigits()+2,'#'))
					.withAxisLabelFont(new Font("Calibri", Font.PLAIN, (int) (20*getConfig().getFontScale())))
					.withLegendLabelFont(new Font("Calibri", Font.PLAIN, (int) (20*getConfig().getFontScale())))
					.withAxisTitleFont(new Font("Calibri", Font.BOLD, (int) (20*getConfig().getFontScale())))
					.withHeatMapTitleFont(new Font("Calibri", Font.BOLD, (int) (36*getConfig().getFontScale())))
					.withGridValuesFont(new Font("Calibri", Font.PLAIN, (int) (20*getConfig().getFontScale())))
					.withGradient(HeatMapGradient.getCannedGradient(getConfig().getColourGradient()-1)) //Command options starts at one
					.build())
			.build()
			.render(dataFile, new ArrayList<DataRecord>(records));
		log.info("Rendering complete for " + dataFile + ".");
		
		if (getConfig().isGenerateCSV()) {
			File csvFile = new File(dataFile.getParent(), dataFile.getName().replace(".png", ".csv"));
			this.checkFile(csvFile);
			super.writeToCSVFile(records, queryUnits, title, csvFile);
		}
		
		if (getConfig().isGenerateJSON()) {
			File jsonFile = new File(dataFile.getParent(), dataFile.getName().replace(".png", ".json"));
			this.checkFile(jsonFile);
			super.writeToJSONFile(records, queryUnits, title, jsonFile, false);
		}
	}
	
	private <T> Axis<?> determineAxis(List<DataAnalysisRecord> records, int index) {
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
			IntegerAxis dowAxis = new IntegerAxis(prettyName);
			for(int day = 1; day < 8; day++) {
				dowAxis.addEntry(day, DayOfWeekMapping.getDayOfWeek(day));
			}
			return dowAxis;
			
		case MONTH:
			IntegerAxis mAxis = new IntegerAxis(prettyName);
			for(int month = 1; month < 13; month++) {
				mAxis.addEntry(month, MonthMapping.getMonth(month));
			}
			return mAxis;
			
		case URBANIZATION:
			StringAxis sAxis = new StringAxis(prettyName);
			Stream.of(Urbanization.values())
				.forEach(entry-> sAxis.addEntry(entry.toString(), UrbanizationMapping.getUrbanization(entry)));
			return sAxis;
			
		case SITE_TYPE:
			StringAxis stAxis = new StringAxis(prettyName);
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
	private <T> Set<T> sortAxisEntries(List<DataAnalysisRecord> records, int index) {
		Set<T> sortedEntries = new TreeSet<T>(); //Need to order the entries
		records.stream().forEach(r->sortedEntries.add((T) (index == 0 ? r.getField_0() : r.getField_1())));
		return sortedEntries;
	}
	
	protected abstract String getDataset();
}
