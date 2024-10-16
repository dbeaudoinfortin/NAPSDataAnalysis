package com.dbf.naps.data.globals;

import java.math.BigDecimal;
import java.time.Year;

public class Constants {

	public static final String URL_SITES_FULL = "https://data-donnees.az.ec.gc.ca/api/file?path=/air%2Fmonitor%2Fnational-air-pollution-surveillance-naps-program%2FProgramInformation-InformationProgramme%2FStationsNAPS-StationsSNPA.csv";
	
	public static final String URL_CONTINUOUS_BASE = "https://data-donnees.az.ec.gc.ca/api/file?path=/air%2Fmonitor%2Fnational-air-pollution-surveillance-naps-program%2FData-Donnees%2F";
	public static final String URL_CONTINUOUS_SUFFIX = "%2FContinuousData-DonneesContinu%2FHourlyData-DonneesHoraires%2F";
	
	public static final String URL_INTEGRATED_BASE = "https://data-donnees.az.ec.gc.ca/api/file?path=";
	public static final String URL_INTEGRATED_LISTING_BASE = "https://data-donnees.az.ec.gc.ca/api/path_contents?path=/air%2Fmonitor%2Fnational-air-pollution-surveillance-naps-program%2FData-Donnees%2F";
	public static final String URL_INTEGRATED_LISTING_SUFFIX = "%2FIntegratedData-DonneesPonctuelles%2F";
	
	public static final String FILE_PATH_CONTINUOUS = "ContinuousData";
	public static final String FILE_PATH_INTEGRATED = "IntegratedData";
	
	public static final int DATASET_YEAR_START = 1974;
	public static final int DATASET_YEAR_END   = Year.now().getValue();
	
	public static final BigDecimal bigDecimal1000 = new BigDecimal(1000);
	
}
