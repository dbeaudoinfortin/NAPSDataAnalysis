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

import com.dbf.naps.data.exporter.ExtractorOptions;
import com.dbf.naps.data.globals.DayOfWeekMapping;
import com.dbf.naps.data.globals.MonthMapping;
import com.dbf.naps.data.globals.ProvTerr;
import com.dbf.naps.data.globals.ProvinceTerritoryMapping;
import com.dbf.naps.data.globals.SiteType;
import com.dbf.naps.data.globals.Urbanization;

public abstract class DataQueryOptions extends ExtractorOptions {

	private static final Logger log = LoggerFactory.getLogger(DataQueryOptions.class);

	private AggregateFunction aggregateFunction = AggregateFunction.AVG;
	private final List<AggregationField> fields = new ArrayList<AggregationField>();
	
	private final Set<Integer>  months = new HashSet<Integer>();
	private final Set<Integer>  daysOfMonth = new HashSet<Integer>();
	private final Set<Integer>  daysOfWeek = new HashSet<Integer>();
	private final Set<ProvTerr> provTerr = new HashSet<ProvTerr>();
	private final Set<Urbanization> urbanization = new HashSet<Urbanization>();
	private final Set<SiteType> siteType = new HashSet<SiteType>();
	
	private String siteName;
	private String cityName;

	private Integer		minSampleCount;
	private BigDecimal	valueUpperBound;
	private BigDecimal	valueLowerBound;
	private Double		resultUpperBound;
	private Double		resultLowerBound;
	
	private String title;
	
	static {
		getOptions().addOption("a","aggregateFunction", true, "Data aggregation function (" + AggregateFunction.ALL_VALUES + ").");
		getOptions().addOption("g1","group1", true, "Data field for level 1 grouping.");
		getOptions().addOption("g2","group2", true, "Data field for optional level 2 grouping.");
		getOptions().addOption("m","months", true, "Comma-separated list of months of the year, starting at 1 for January.");
		getOptions().addOption("d","days", true, "Comma-separated list of days of the month.");
		getOptions().addOption("dow","daysOfWeek", true, "Comma-separated list of days of the week, starting at 1 for Sunday.");
		getOptions().addOption("pt","provTerr", true, "Comma-separated list of 2-digit province & territory codes (" + ProvTerr.ALL_VALUES + ").");
		getOptions().addOption("u","urbanization", true, "NAPS site urbanization classification (" + Urbanization.ALL_VALUES + ").");
		getOptions().addOption("st","siteType", true, "NAPS site type classification (" + SiteType.ALL_VALUES + ").");
		getOptions().addOption("sn","siteName", true, "NAPS site (station) name, partial match.");
		getOptions().addOption("cn","cityName", true, "City name, partial match.");
		getOptions().addOption("ct","title", true, "Chart title. Will be automatically generated if not defined.");
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
		loadDaysOfWeek(cmd);
		loadProvTerr(cmd);
		loadUrbanization(cmd);
		loadSiteType(cmd);
		loadSiteName(cmd);
		loadCityName(cmd);
		loadTitle(cmd);
		
		loadValueLowerBound(cmd); //Check me first!
		loadValueUpperBound(cmd);
		loadResultLowerBound(cmd); //Check me first!
		loadResultUpperBound(cmd);
		
		loadMinSampleCount(cmd); //Check me last
	}
	
	private void loadMinSampleCount(CommandLine cmd) {
		if(cmd.hasOption("minSampleCount")) {
			if(aggregateFunction.equals(AggregateFunction.NONE)) {
				throw new IllegalArgumentException("Minimum sample count cannot be used when the aggregate function is set to 'NONE'.");
			}
			if(aggregateFunction.equals(AggregateFunction.COUNT) && (resultLowerBound != null)) {
				throw new IllegalArgumentException("Minimum sample count is redundant since the aggregate function is set to 'COUNT' and the post-aggregated result lower bound is set.");
			}
			
			minSampleCount = Integer.parseInt(cmd.getOptionValue("minSampleCount"));
			if (minSampleCount < 2) {
				throw new IllegalArgumentException("Invalid minimum sample count: " + minSampleCount + ". Must be at least 2.");
			}
			log.info("Using a minimum sample count of " + minSampleCount + ".");
		} else {
			log.info("Not using minimum sample count.");
		}
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
			if (valueLowerBound != null && valueLowerBound.compareTo(valueUpperBound) >= 0) {
				throw new IllegalArgumentException("Invalid pre-aggregated raw value upper bound: " + valueUpperBound + ". The upper bound must be greater than the lower bound.");
			}
			log.info("Using pre-aggregated raw value upper bound: " + valueUpperBound);
		} else {
			log.info("No upper bound set for the pre-aggregated raw values.");
		}
	}

	private void loadResultLowerBound(CommandLine cmd) {
		if(cmd.hasOption("resultLowerBound")) {
			if(aggregateFunction.equals(AggregateFunction.NONE)) {
				throw new IllegalArgumentException("Post-aggregated result lower bound cannot be used when the aggregate function is set to 'NONE'.");
			}
			
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
			if(aggregateFunction.equals(AggregateFunction.NONE)) {
				throw new IllegalArgumentException("Post-aggregated result upper bound cannot be used when the aggregate function is set to 'NONE'.");
			}
			
			resultUpperBound = Double.parseDouble(cmd.getOptionValue("resultUpperBound"));
			if (resultLowerBound != null && resultLowerBound >= resultUpperBound) {
				throw new IllegalArgumentException("Invalid post-aggregated result upper bound: " + resultUpperBound + ". The upper bound must be greater than the lower bound.");
			}
			log.info("Using post-aggregated result upper bound: " + resultUpperBound);
		} else {
			log.info("No upper bound set for the post-aggregated results.");
		}
	}
	
	private void loadTitle(CommandLine cmd) {
		if(cmd.hasOption("title")) {
			title = cmd.getOptionValue("title");
			title = title.trim();
			log.info("Using the custom title \"" + title + "\".");
		} else {
			log.info("Using an auto-generated title.");
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
						throw new IllegalArgumentException("Invalid province/territory code: " + provRaw + ". Possible values are: " + ProvTerr.ALL_VALUES);
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
	
	private void loadSiteType(CommandLine cmd) {
		if(cmd.hasOption("siteType")) {
			for(String siteTypeRaw : cmd.getOptionValue("siteType").split(",")) {
				String siteTypeTrimmed = siteTypeRaw.trim().toUpperCase();
				if (siteTypeTrimmed.isEmpty()) continue;
				
				SiteType siteTypeEnum;
				try {
					siteTypeEnum = SiteType.valueOf(siteTypeTrimmed);
				} catch (Exception e) {
					throw new IllegalArgumentException("Invalid NAPS site type code: " + siteTypeRaw + ". Possible values are: " + SiteType.ALL_VALUES);
				}
				siteType.add(siteTypeEnum);
			}
			if(siteType.isEmpty()) 
				throw new IllegalArgumentException("Must specify at least one NAPS site type code.");
			
			log.info("Using only the following NAPS site type codes: " + siteType);
		} else {
			log.info("Using all NAPS site type codes.");
		}
	}
	
	private void loadUrbanization(CommandLine cmd) {
		if(cmd.hasOption("urbanization")) {
			for(String urbanizationRaw : cmd.getOptionValue("urbanization").split(",")) {
				String urbanizationTrimmed = urbanizationRaw.trim().toUpperCase();
				if (urbanizationTrimmed.isEmpty()) continue;
				
				Urbanization urbanizationEnum;
				try {
					urbanizationEnum = Urbanization.valueOf(urbanizationTrimmed);
				} catch (Exception e) {
					throw new IllegalArgumentException("Invalid NAPS site urbanization code: " + urbanizationRaw + ". Possible values are: " + Urbanization.ALL_VALUES);
				}
				urbanization.add(urbanizationEnum);
			}
			if(urbanization.isEmpty()) 
				throw new IllegalArgumentException("Must specify at least one NAPS site urbanization code.");
			
			log.info("Using only the following NAPS site urbanization codes: " + urbanization);
		} else {
			log.info("Using all NAPS site urbanization codes.");
		}
	}
	
	private void loadMonths(CommandLine cmd) {
		if(cmd.hasOption("months")) {
			for(String month : cmd.getOptionValue("months").split(",")) {
				month = month.trim();
				if (month.isEmpty()) continue;
				
				//Try doing a lookup to convert the long form into the integer
				Integer monthInt = MonthMapping.getMonth(month);
				if (null == monthInt) {
					//Fallback to integer parsing
					try {
						monthInt =  Integer.parseInt(month);
					} catch (Exception e){
						throw new IllegalArgumentException("Invalid month: " + month);
					}
					
					if (monthInt < 1 || monthInt > 12) {
						throw new IllegalArgumentException("Invalid month: " + month + ". Must be between 1 and 12 (inclusive).");
					}
				}
				months.add(monthInt);
			}
			if(months.isEmpty()) 
				throw new IllegalArgumentException("Must specify at least one month.");
			
			log.info("Using only the following months: " + months);
		} else {
			log.info("Using all months.");
		}
	}
	
	private void loadDays(CommandLine cmd) {
		if(cmd.hasOption("days")) {
			for(String day : cmd.getOptionValue("days").split(",")) {
				day = day.trim();
				if (day.isEmpty()) continue;
				
				int dayInt;
				try {
					dayInt = Integer.parseInt(day); 
				} catch (Exception e){
					throw new IllegalArgumentException("Invalid day: " + day);
				}
				if (dayInt < 1 || dayInt > 31) {
					throw new IllegalArgumentException("Invalid day: " + day + ". Must be between 1 and 31 (inclusive).");
				}
				daysOfMonth.add(dayInt);
			}
			if(daysOfMonth.isEmpty()) 
				throw new IllegalArgumentException("Must specify at least one day of the month.");
			
			log.info("Using only the following days of the month: " + daysOfMonth);
		} else {
			log.info("Using all days.");
		}
	}
	
	private void loadDaysOfWeek(CommandLine cmd) {
		if(cmd.hasOption("daysOfWeek")) {
			for(String day : cmd.getOptionValue("daysOfWeek").split(",")) {
				day = day.trim();
				if (day.isEmpty()) continue;
				
				//Try doing a lookup to convert the long form into the integer
				Integer dayInt = DayOfWeekMapping.getDayOfWeek(day);
				if(null == dayInt) {
					//Fallback to integer parsing
					try {
						dayInt = Integer.parseInt(day); 
					} catch (Exception e){
						throw new IllegalArgumentException("Invalid day of the week: " + day);
					}
					if (dayInt < 1 || dayInt > 7) {
						throw new IllegalArgumentException("Invalid day of the week: " + day + ". Must be between 1 and 7 (inclusive).");
					}
				}
				daysOfWeek.add(dayInt);
			}
			if(daysOfWeek.isEmpty()) 
				throw new IllegalArgumentException("Must specify at least one day of the week.");
			
			log.info("Using only the following days of the week: " + daysOfWeek);
		} else {
			log.info("Using all days of the week.");
		}
	}
	
	public void loadAggregationField(CommandLine cmd, int dimIndex, boolean mandatory) {
		final String field =  "group" + dimIndex;
		AggregationField aggregationField = null;
		
		if(cmd.hasOption(field)) {
			
			//Need to check for and prevent gaps in the fields.
			//For example, -g1 and -g3 are specified but -g2 is not
			if((dimIndex > 1) && fields.size() < (dimIndex-1)) {
				throw new IllegalArgumentException("Missing data field for group " + (dimIndex-1) + ". Use the -g" + dimIndex + " argument.");
			}
				
			String rawValue = cmd.getOptionValue(field);
			if("DAY_OF_MONTH".equals(rawValue.toUpperCase())) rawValue = "DAY"; //Allow both forms
			if("PROVINCE".equals(rawValue.toUpperCase())) rawValue = "PROVINCE_TERRITORY";
			
			try {
				aggregationField = AggregationField.valueOf(rawValue.toUpperCase()); 
			} catch(Exception e) {
				String allValues = AggregationField.ALL_VALUES;
				if(!allowAggregationFieldHour()) allValues = allValues.replace(", HOUR", ""); //Remove hour for integrated
				throw new IllegalArgumentException("Invalid data field for group " + dimIndex + ": " + rawValue + ". Possible values are: " + allValues);
			}
			if(aggregationField.equals(AggregationField.HOUR) && !allowAggregationFieldHour()) {
				log.info("Cannot use 'HOUR' as a data field for group " + dimIndex + ".");
			}
			
			fields.add(dimIndex-1, aggregationField);
			log.info("Using data field " + aggregationField + " for group " + dimIndex + ".");
		} else if(mandatory) {
			throw new IllegalArgumentException("Missing data field for group " + dimIndex + ". Use the -g" + dimIndex + " argument.");
		} else {
			log.info("No aggregation field for group " + dimIndex + ".");
		}
	}
	
	private void loadAggregateFunction(CommandLine cmd) {
		if(cmd.hasOption("aggregateFunction")) {
			String rawValue = cmd.getOptionValue("aggregateFunction");
			try {
				aggregateFunction = AggregateFunction.valueOf(rawValue.toUpperCase()); 
			} catch(Exception e) {
				throw new IllegalArgumentException("Invalid aggregation function option: " + rawValue + ". Possible values are: " + AggregateFunction.ALL_VALUES);
			}
			
			if(aggregateFunction.equals(AggregateFunction.NONE)) {
				if(!allowAggregateFunctionNone())
					throw new IllegalArgumentException("Aggregate function cannot be set to 'NONE' for this report.");
				if(fields.size() > 0)
					throw new IllegalArgumentException("Aggregate function cannot be set to 'NONE' when grouping is used. Remove the -g[1-5] arguments.");
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
	
	public List<AggregationField> getFields() {
		return fields;
	}

	public Set<Integer> getMonths() {
		return months;
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
	
	public Integer getMinSampleCount() {
		return minSampleCount;
	}

	public Set<Integer> getDaysOfMonth() {
		return daysOfMonth;
	}

	public Set<Integer> getDaysOfWeek() {
		return daysOfWeek;
	}
	
	public String getTitle() {
		return title;
	}

	public Set<Urbanization> getUrbanization() {
		return urbanization;
	}

	public Set<SiteType> getSiteType() {
		return siteType;
	}
}
