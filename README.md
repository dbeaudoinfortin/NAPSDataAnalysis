# Overview
Canada National Air Pollution Surveillance Program data downloader, extractor and schema importer. 

This project will eventually contain a collection of tools to assist in the analysis of Canadian air quality data. The data is provided by the National Air Pollution Surveillance (NAPS) program, which is part of Environment and Climate Change Canada. You can view the original data here: https://data-donnees.az.ec.gc.ca/data/air/monitor/national-air-pollution-surveillance-naps-program/

All usage is for non-commercial research purposes. I am not affiliated with the Government of Canada.

# Sites

## NAPSSitesDownloader

A Java tool that downloads a single file containing all of the sites (sampling stations) for the NAPS program. This is the simplest tool in the toolbox and is only included for sake of completeness. The file is downloaded from https://data-donnees.az.ec.gc.ca/api/file?path=/air%2Fmonitor%2Fnational-air-pollution-surveillance-naps-program%2FProgramInformation-InformationProgramme%2FStationsNAPS-StationsSNPA.csv to the specified directory.

You can invoke this tool by running the class com.dbf.naps.data.download.sites.NAPSSitesDownloader. Note that the threadCount argument is meaningless since there is only one file to download.

**Command line usage:**
```
 -o,  --overwriteFiles       Replace the existing file.
 -p,  --downloadPath <arg>   Local path for downloaded files.
 -t,  --threadCount <arg>    Maximum number of parallel threads.
```

## NAPSSitesLoader

A Java tool that loads all of the sites (sampling stations) for the NAPS program (downloaded by the NAPSContinuousDataDownloader) from the provided directory into a PostgreSQL database, as specified. The database schema is automatically created when the tool runs. Once all the data is loaded, there should be 791 rows of data (as of May 2024) in the sites table of your database. 

You can invoke this tool by running the class com.dbf.naps.data.loader.sites.NAPSSitesLoader. Note that the threadCount argument is meaningless since there is only one file to process.

**Command line usage:**
```
 -p,   --dataPath <arg>       Local path for the raw data file previously downloaded.
 -dbh, --dbHost <arg>         Hostname for the PostgreSQL database. Default: localhost
 -dbt, --dbPort <arg>         Port for the PostgreSQL database. Default: 5432
 -dbn, --dbName <arg>         Database name for the PostgreSQL database. Default: naps
 -dbu, --dbUser <arg>         Database user name for the PostgreSQL database. Default: postgres
 -dbp, --dbPass <arg>         Database password for the PostgreSQL database. Default: password
 -t,   --threadCount <arg>    Maximum number of parallel threads.
```

# Continuous Data

## NAPSContinuousDataDownloader

A Java tool that will download all of the hourly continuous data for the provided years into the provided directory. All file names are unique and all files are downloaded into a single directory. Files are downloaded from https://data-donnees.az.ec.gc.ca/data/air/monitor/national-air-pollution-surveillance-naps-program/Data-Donnees/.

You can invoke this tool by running the class com.dbf.naps.data.download.continuous.NAPSContinuousDataDownloader.

**Command line usage:**
```
 -o,  --overwriteFiles       Replace existing files.
 -p,  --downloadPath <arg>   Local path for downloaded files.
 -t,  --threadCount <arg>    Maximum number of parallel threads.
 -ye, --yearEnd <arg>        End year (inclusive).
 -ys, --yearStart <arg>      Start year (inclusive).
```

## NAPSContinuousDataLoader

A Java tool that loads all of the raw continuous data (downloaded by the NAPSContinuousDataDownloader) from the provided directory into a PostgreSQL database, as specified. The database schema is automatically created when the tool runs. This tool automatically cleans-up and fixes data inconsistencies as it finds them. Once all the data is loaded, there should be about 275 million rows of data (as of May 2024) in the continuous_data table of your database. Note that for the PM2.5 pollutant, each variation of the analysis method is given its own pollutant id since the data may not be directly comparable.

You can invoke this tool by running the class com.dbf.naps.data.loader.continuous.NAPSContinuousDataLoader.

**Command line usage:**
```
 -p,   --dataPath <arg>       Local path for raw data files previously downloaded.
 -dbh, --dbHost <arg>         Hostname for the PostgreSQL database. Default: localhost
 -dbt, --dbPort <arg>         Port for the PostgreSQL database. Default: 5432
 -dbn, --dbName <arg>         Database name for the PostgreSQL database. Default: naps
 -dbu, --dbUser <arg>         Database user name for the PostgreSQL database. Default: postgres
 -dbp, --dbPass <arg>         Database password for the PostgreSQL database. Default: password
 -t,   --threadCount <arg>    Maximum number of parallel threads.
```

# Integrated Data

## NAPSIntegratedDataDownloader

A Java tool that will download all of the integrated data for the provided years into the provided directory. Since many of the file names of the files conflict, each year will be downloaded into its own sub-directory. Files are downloaded from https://data-donnees.az.ec.gc.ca/data/air/monitor/national-air-pollution-surveillance-naps-program/Data-Donnees/. 

You can invoke this tool by running the class com.dbf.naps.data.download.integrated.NAPSIntegratedDataDownloader.

**Command line usage:**
```
 -o,  --overwriteFiles       Replace existing files.
 -p,  --downloadPath <arg>   Local path for downloaded files.
 -t,  --threadCount <arg>    Maximum number of parallel threads.
 -ye, --yearEnd <arg>        End year (inclusive).
 -ys, --yearStart <arg>      Start year (inclusive).
```

## NAPSIntegratedDataLoader

A Java tool that loads all of the raw integrated data (downloaded by the NAPSIntegratedDataDownloader) from the provided directory into a PostgreSQL database, as specified. The database schema is automatically created when the tool runs. This tool automatically cleans-up and fixes data inconsistencies as it finds them. Once all the data is loaded, there should be about ____ million rows of data (as of May 2024) in the integrated_data table of your database.

You can invoke this tool by running the class com.dbf.naps.data.loader.integrated.NAPSIntegratedDataLoader.

**Command line usage:**
```
 -p,   --dataPath <arg>       Local path for raw data files previously downloaded.
 -dbh, --dbHost <arg>         Hostname for the PostgreSQL database. Default: localhost
 -dbt, --dbPort <arg>         Port for the PostgreSQL database. Default: 5432
 -dbn, --dbName <arg>         Database name for the PostgreSQL database. Default: naps
 -dbu, --dbUser <arg>         Database user name for the PostgreSQL database. Default: postgres
 -dbp, --dbPass <arg>         Database password for the PostgreSQL database. Default: password
 -t,   --threadCount <arg>    Maximum number of parallel threads.
```


# How To Run

In the /target directory you will find the pre-compiled jar file NAPSData.jar. This is a shaded jar file which means that it contains all of the 3rd party dependancies inside of it. Assuming you hava Java 17 installed and 
part of your system path you can simply invoke the class by running the following:
```
 java -cp NAPSData.jar com.dbf.naps.data.loader.continuous.NAPSContinuousDataLoader -p C:\temp\NAPSData\RawFiles -t 24
```

In this example, the data will be loaded from the C:\temp\NAPSData\RawFiles directory into the database using a thread pool size of 24, and all default database connection options (see above for details).

# Notes

- Requires Java 17
- Tested with PostgreSQL 16.3. The database should be created with the UTF-8 characterset in order to support accented characters.
- If you want to build the jar from the source code you will need Apache Maven: https://maven.apache.org/
