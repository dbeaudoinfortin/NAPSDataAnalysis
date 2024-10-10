package com.dbf.naps.data.analysis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbf.naps.data.exporter.ExporterOptions;
import com.dbf.naps.data.globals.ProvTerr;
import com.dbf.naps.data.globals.ProvinceTerritoryMapping;

public abstract class DataQueryOptions extends ExporterOptions {

	private static final Logger log = LoggerFactory.getLogger(DataQueryOptions.class);

	private AggregateFunction aggregateFunction = AggregateFunction.AVG;
	private final List<AggregationField> dimensions = new ArrayList<AggregationField>();
	
	private final Set<Integer> months = new HashSet<Integer>();
	private final Set<Integer> days = new HashSet<Integer>();
	private final Set<ProvTerr> provTerr = new HashSet<ProvTerr>();
	
	private String siteName;
	private String cityName;
	
	private BigDecimal	valueUpperBound;
	private BigDecimal	valueLowerBound;
	private Double		resultUpperBound;
	private Double		resultLowerBound;
	
	static {
		getOptions().addOption("a","aggregateFunction", true, "Data aggregation function.");
		getOptions().addRequiredOption("g1","group1", true, "Data field for level 1 grouping.");
		getOptions().addOption("g2","group2", true, "Data field for optional level 2 grouping.");
		getOptions().addOption("m","months", true, "Comma-seperated list of months of the year, starting at 1 for January.");
		getOptions().addOption("d","days", true, "Comma-seperated list of days of the month.");
		getOptions().addOption("pt","provTerr", true, "Comma-seperated list of 2-digit province & territory codes.");
		getOptions().addOption("sn","siteName", true, "NAPS site (station) name, partial match.");
		getOptions().addOption("cn","cityName", true, "City name, partial match.");
		getOptions().addOption("scm","minSampleCount", true, "Minimum sample count (number of samples or data points) in order to be included in the result set.");
		getOptions().addOption("vub","valueUpperBound", true, "Upper bound (inclusive) of pre-aggregated raw values to include. "
				+ "Values greater than this threshold will be filtered out before aggregation.");
		getOptions().addOption("vlb","valueLowerBound", true, "Lower bound (inclusive) of pre-aggregated raw values to include. "
				+ "Values less than this threshold will be filtered out before aggregation.");
		getOptions().addOption("rub","resultUpperBound", true, "Upper bound (inclusive) of post-aggregated results to include. "
				+ "Results greater than this threshold will be filtered out of the result set after aggregation.");
		getOptions().addOption("rlb","resultLowerBound", true, "Lower bound (inclusive) of post-aggregated results to include. "
				+ "Results less than this threshold will be filtered out of the result set after aggregation.");
	}

	public DataQueryOptions(String[] args) throws IllegalArgumentException {
		super(args);
		loadFromArgs(args);
	}
	
	private void loadFromArgs(String[] args) throws IllegalArgumentException {
		CommandLine cmd = null;
		try {
			cmd = (new DefaultParser()).parse(getOptions(), args);
		}
		catch(ParseException e) {
			throw new IllegalArgumentException(e);
		}
		loadAggregationField(cmd, 1, isAggregationMandatory());
		loadAggregationField(cmd, 2, isAggregationMandatory());
		loadAggregateFunction(cmd);
		loadMonths(cmd);
		loadDays(cmd);
		loadProvTerr(cmd);
		loadSiteName(cmd);
		loadCityName(cmd);
		
		loadValueLowerBound(cmd); //Check me first!
		loadValueUpperBound(cmd);
		loadResultLowerBound(cmd); //Check me first!
		loadResultUpperBound(cmd);
	}
	
	private void loadValueLowerBound(CommandLine cmd) {
		if(cmd.hasOption("valueLowerBound")) {
			valueLowerBound = new BigDecimal(cmd.getOptionValue("valueLowerBound"));
			if (valueLowerBound.compareTo(new BigDecimal(0)) < 0) {
				throw new IllegalArgumentException("Invalid pre-aggregated raw value lower bound: " + valueLowerBound);
			}
			log.info("Using pre-aggregated raw value lower bound: " + valueLowerBound);
		} else {
			log.info("No lower bound set for the pre-aggregated raw values.");
		}
	}

	private void loadValueUpperBound(CommandLine cmd) {
		if(cmd.hasOption("valueUpperBound")) {
			valueUpperBound = new BigDecimal(cmd.getOptionValue("valueUpperBound"));
			if (valueLowerBound.compareTo(valueUpperBound) >= 0) {
				throw new IllegalArgumentException("Invalid pre-aggregated raw value upper bound: " + valueUpperBound);
			}
			log.info("Using pre-aggregated raw value upper bound: " + valueUpperBound);
		} else {
			log.info("No upper bound set for the pre-aggregated raw values.");
		}
	}

	private void loadResultLowerBound(CommandLine cmd) {
		if(cmd.hasOption("resultLowerBound")) {
			resultLowerBound = Double.parseDouble(cmd.getOptionValue("resultLowerBound"));
			if (resultLowerBound < 0) {
				throw new IllegalArgumentException("Invalid post-aggregated result lower bound: " + resultLowerBound);
			}
			log.info("Using post-aggregated result lower bound: " + resultLowerBound);
		} else {
			log.info("No lower bound set for the post-aggregated results.");
		}
	}

	private void loadResultUpperBound(CommandLine cmd) {
		if(cmd.hasOption("resultUpperBound")) {
			resultUpperBound = Double.parseDouble(cmd.getOptionValue("resultUpperBound"));
			if (resultLowerBound >= resultUpperBound) {
				throw new IllegalArgumentException("Invalid post-aggregated result upper bound: " + resultUpperBound);
			}
			log.info("Using post-aggregated result upper bound: " + resultUpperBound);
		} else {
			log.info("No upper bound set for the post-aggregated results.");
		}
	}
	
	private void loadSiteName(CommandLine cmd) {
		if(cmd.hasOption("siteName")) {
			String site = cmd.getOptionValue("siteName");
			site = site.trim();
			if(site.isEmpty()) throw new IllegalArgumentException("Must specify a site name.");
			siteName = site.toUpperCase();
			log.info("Using only the sites that fully or partially match the name '" + site + "'.");
		} else {
			log.info("Using all sites names.");
		}
	}
	
	private void loadCityName(CommandLine cmd) {
		if(cmd.hasOption("cityName")) {
			String city = cmd.getOptionValue("cityName");
			city = city.trim();
			if(city.isEmpty()) throw new IllegalArgumentException("Must specify a city name.");
			cityName = city.toUpperCase();
			log.info("Using only the cities that fully or partially match the name '" + city + "'.");
		} else {
			log.info("Using all cities.");
		}
	}
	
	private void loadProvTerr(CommandLine cmd) {
		if(cmd.hasOption("provTerr")) {
			for(String provRaw : cmd.getOptionValue("provTerr").split(",")) {
				String provTrimmed = provRaw.trim().toUpperCase();
				if (provTrimmed.isEmpty()) continue;
				
				//Try doing a lookup to convert the long form into the short code
				ProvTerr provTerrEnum = ProvinceTerritoryMapping.getProvTerr(provTrimmed);
				if(null == provTerrEnum) {
					try {
						provTerrEnum = ProvTerr.valueOf(provTrimmed);
					} catch (Exception e) {
						throw new IllegalArgumentException("Invalid province/territory code: " + provRaw);
					}
				}
				provTerr.add(provTerrEnum);
			}
			if(provTerr.isEmpty()) 
				throw new IllegalArgumentException("Must specify at least one province/territory.");
			
			log.info("Using only the following provinces/territories: " + provTerr);
		} else {
			log.info("Using all provinces/territories.");
		}
	}
	
	private void loadMonths(CommandLine cmd) {
		if(cmd.hasOption("months")) {
			for(String month : cmd.getOptionValue("months").split(",")) {
				month = month.trim();
				if (month.isEmpty()) continue;
				months.add(Integer.parseInt(month));
			}
			if(months.isEmpty()) 
				throw new IllegalArgumentException("Must specify at least one month.");
			
			log.info("Using only the following months: " + months);
		} else {
			log.info("Using all months.");
		}
	}
	
	private void loadDays(CommandLine cmd) {
		if(cmd.hasOption("day")) {
			for(String day : cmd.getOptionValue("days").split(",")) {
				day = day.trim();
				if (day.isEmpty()) continue;
				days.add(Integer.parseInt(day));
			}
			if(days.isEmpty()) 
				throw new IllegalArgumentException("Must specify at least one day of the month.");
			
			log.info("Using only the following days of the month: " + days);
		} else {
			log.info("Using all days.");
		}
	}
	
	public void loadAggregationField(CommandLine cmd, int dimIndex, boolean mandatory) {
		final String field =  "group" + dimIndex;
		AggregationField aggregationField = null;
		
		if(cmd.hasOption(field)) {
			String rawValue = cmd.getOptionValue(field);
			try {
				aggregationField = AggregationField.valueOf(rawValue.toUpperCase()); 
			} catch(Exception e) {
				throw new IllegalArgumentException("Invalid data field for group " + dimIndex + " " + rawValue);
			}
			if(aggregationField.equals(AggregationField.HOUR) && !allowAggregationFieldHour()) {
				log.info("Cannot use 'HOUR' as a data field for group " + dimIndex + ".");
			}
			
			dimensions.add(dimIndex-1, aggregationField);
			log.info("Using data field " + aggregationField + " for group " + dimIndex + ".");
		} else if(mandatory) {
			throw new IllegalArgumentException("Missing data field for group " + dimIndex + ". Use the -g" + dimIndex + " argument.");
		} else {
			dimensions.add(null);
		}
	}
	
	private void loadAggregateFunction(CommandLine cmd) {
		if(cmd.hasOption("aggregateFunction")) {
			String rawValue = cmd.getOptionValue("aggregateFunction");
			try {
				aggregateFunction = AggregateFunction.valueOf(rawValue.toUpperCase()); 
			} catch(Exception e) {
				throw new IllegalArgumentException("Invalid aggregation function option: " + rawValue);
			}
			
			if(aggregateFunction.equals(AggregateFunction.NONE)) {
				if(!allowAggregateFunctionNone())
					throw new IllegalArgumentException("Aggregate function cannot be set to 'NONE' for this report.");
				if(dimensions.size() > 0)
					throw new IllegalArgumentException("Aggregate function cannot be set to 'NONE' when grouping is used. Remove the -g[1-5] arguments.");
			} else if(dimensions.size() <1) {
				throw new IllegalArgumentException("Aggregate functions require the use of at least one grouping. Use the -g[1-5] arguments to specify a grouping.");
			}

			log.info("Using aggregate function " + aggregateFunction);
		} else {
			log.info("Using default aggregate function: " + aggregateFunction);
		}
	}
	
	public abstract boolean isAggregationMandatory();
	
	public abstract boolean allowAggregateFunctionNone();
	
	public abstract boolean allowAggregationFieldHour();

	public AggregateFunction getAggregateFunction() {
		return aggregateFunction;
	}

	public void setAggregateFunction(AggregateFunction aggregateFunction) {
		this.aggregateFunction = aggregateFunction;
	}
	
	public List<AggregationField> getDimensions() {
		return dimensions;
	}

	public Set<Integer> getMonths() {
		return months;
	}

	public Set<Integer> getDays() {
		return days;
	}

	public Set<ProvTerr> getProvTerr() {
		return provTerr;
	}

	public String getSiteName() {
		return siteName;
	}

	public String getCityName() {
		return cityName;
	}

	public BigDecimal getValueUpperBound() {
		return valueUpperBound;
	}

	public BigDecimal getValueLowerBound() {
		return valueLowerBound;
	}

	public Double getResultUpperBound() {
		return resultUpperBound;
	}

	public Double getResultLowerBound() {
		return resultLowerBound;
	}
}
