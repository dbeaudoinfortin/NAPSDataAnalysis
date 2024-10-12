# NAPS Data Analysis Toolbox

<p align="center">
<img src="https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/bc1f2673-05fd-4713-8be7-57119d038358"/>
</p>

## Contents

- [Overview](#overview)
- [Clean Data Exports](#clean-data-exports)
- [Data Analysis and Dashboards](#data-analysis-and-dashboards)
- [Getting Started](#getting-started)
  * [Installing PostgreSQL](#installing-postgresql)
  * [Installing Java](#installing-java)
  * [Downloading the Data](#downloading-the-data)
  * [Loading the Data](#loading-the-data)
  * [Querying the Data](#querying-the-data)
  * [Generating Heat Maps](#generating-heat-maps)
  * [Installing Microsoft Power BI](#installing-microsoft-power-bi)
  * [Creating a Report](#creating-a-report)
- [NAPS Site Tools](#naps-site-tools)
  * [NAPSSitesDownloader](#napssitesdownloader)
  * [NAPSSitesLoader](#napssitesloader)
- [Continuous Data Tools](#continuous-data-tools)
  * [NAPSContinuousDataDownloader](#napscontinuousdatadownloader)
  * [NAPSContinuousDataLoader](#napscontinuousdataloader)
  * [NAPSContinuousDataQuery](#napscontinuousdataquery)
  * [NAPSContinuousDataExporter](#napscontinuousdataexporter)
- [Integrated Data Tools](#integrated-data-tools)
  * [NAPSIntegratedDataDownloader](#napsintegrateddatadownloader)
  * [NAPSIntegratedDataLoader](#napsintegrateddataloader)
  * [NAPSIntegratedDataQuery](#napsintegrateddataquery)
  * [NAPSIntegratedDataExporter](#napsintegrateddataexporter)
- [How To Run Individual Tools](#how-to-run-individual-tools)
- [Database Design](#database-design)
- [Known Issues](#known-issues)
- [Notes](#notes)
- [Legal Stuff](#legal-stuff)

# Overview
Canada National Air Pollution Surveillance Program (NAPS) data downloader, extractor, schema importer and visualization. 

This project will eventually contain a collection of tools to assist in the analysis of Canadian air quality data. The data is provided by the National Air Pollution Surveillance (NAPS) program, which is part of Environment and Climate Change Canada. You can view the original data [here](https://data-donnees.az.ec.gc.ca/data/air/monitor/national-air-pollution-surveillance-naps-program/).

All usage is for non-commercial research purposes. I am not affiliated with the Government of Canada.

# Clean Data Exports

**Last Updated October 2024**

The NAPS data is messy; the data files contain many inconsistencies in structure, formatting, labelling, etc. In order to load all this data into a clean database, I needed to implement many clean-up rules and handle many exceptional cases. I believe this work could be of benefit to others.   

In the [/exports](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/tree/main/exports) directory you will find many CSV files that re-publish the same NAPS data but cleaned-up. These files were generated using the [NAPSContinuousDataExporter](#napscontinuousdataexporter) and [NAPSIntegratedDataExporter](#napsintegrateddataexporter).

If you are curious about the data issues I have encountered, I have started keeping track of some of the non-trivial issues [here](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/issues?q=is%3Aissue+label%3A%22Data+Issue%22).

**Integrated Data**

All of the integrated data exports have been zipped to compress them. I have exported the data 3 different ways:
- [PerPollutant](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/tree/main/exports/IntegratedData/PerPollutant) - contains data that is grouped into a single file for each pollutant.
- [PerSite](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/tree/main/exports/IntegratedData/PerSite) - contains data that is grouped into a single file for each site (station).
- [PerYear](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/tree/main/exports/IntegratedData/PerYear) - contains data that is grouped into a single file for each year.

**Continuous Data**

All of the continuous data exports have been zipped to compress them. There is significantly more continuous data than integrated data. The zip files will expand to about 15GB in total. I have exported the data 3 different ways:
- [PerPollutant](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/tree/main/exports/ContinuousData/PerPollutant) - contains data that is grouped into a single file for each pollutant.
- [PerSite](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/tree/main/exports/ContinuousData/PerSite) - contains data that is grouped into a single file for each site (station).
- [PerYear](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/tree/main/exports/ContinuousData/PerYear) - contains data that is grouped into a single file for each year.

To work around GitHub's file size limit of 100MB, some of the zip files have been created as multi-part archives. You will need to download all of the parts of the archive before you can extract the main zip file. 

# Data Analysis and Dashboards

![Report 2](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/8104f1e2-8c9d-4d86-ac32-284274d2eaed)

![Report 1](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/a6411def-4a27-45d1-8afe-9c8f17768577)

![Report 3](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/7869abff-c45e-4b16-892f-663dff79862f)

In the [/reports](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/tree/main/reports) directory you will find a sample Microsoft Power BI report. This report is example of  how a BI tool can be used for visualizing the NAPS data. This report is designed to be used in conjunction with the database schema built and populated by the tools in this tool box. For information on how to set-up your database and connect to it using Power BI, check out the [Getting Started](#getting-started) section below.

I plan to eventually add sample reports for other BI/Data Visualization software that are open source, free, and available on more platforms than just Windows x86-64.


(Example heat maps coming soon)

# Getting Started

The following steps will guide you in building a database from scratch, populating it with NAPS data, and querying/analyzing the NAPS data.

## Installing PostgreSQL

The tools in this toolbox are designed to be used with a PostgreSQL database. PostgreSQL was chosen because it is feature-rich, highly performant, open-source, free, and widely available on multiple platforms.

You can download an installer of PostgreSQL for every major desktop/server OS [here](https://www.postgresql.org/download/). The installer will walk you through the process of creating an initial database with a user and password.

## Installing Java

The tools in this toolbox are written in Java. You will need the Java 17 or later in order to run any of the tools. The Java JDK is free, multi-platform (supporting Windows, Linux, MacOS, etc.), multi-architecture (supporting x86 and ARM), and can be downloaded [directly from Oracle](https://www.oracle.com/ca-en/java/technologies/downloads/).

## Downloading the Data

There are three separate tools included in this tool box that will automatically download that data files from the NAPS website and save them to a local directory. These tools were created to eliminate the tedious clicking of manually downloading and extracting all the data files. You can download any of the files in any order. The sites definition data file is only required if you will be downloading the integrated data.

**NAPSSitesDownloader**

This tool will download all of the NAPS site definitions from the NAPS website and save them to disk in the specified directory. You can run the tool using the following command line command, on Windows, in the directory of the naps_data.jar: 

```
java -cp naps_data.jar com.dbf.naps.data.download.sites.NAPSSitesDownloader -p C:\temp\NAPSData\RawFiles -t 10
```

For more information about the possible command line arguments, see the NAPSSitesDownloader section [below](#napssitesdownloader).

**NAPSContinuousDataDownloader**

This tool will download all of the NAPS continuous air quality data from the NAPS website and save it to disk in the specified directory. This is optional and only needs to be run if you want to analyze the continuous air quality data. A sub-directory named `ContinuousData` will be automatically created. You can run the tool using the following command line command, on Windows, in the directory of the naps_data.jar: 

```
java -cp naps_data.jar com.dbf.naps.data.download.continuous.NAPSContinuousDataDownloader -p C:\temp\NAPSData\RawFiles -t 10
```

For more information about the possible command line arguments, see the NAPSContinuousDataDownloader section [below](#napscontinuousdatadownloader).

**NAPSIntegratedDataDownloader**

This tool will download all of the NAPS integrated air quality data from the NAPS website and save it to disk in the specified directory. This is optional and only needs to be run if you want to analyze the integrated air quality data. A sub-directory named `IntegratedData` will be automatically created. You can run the tool using the following command line command, on Windows, in the directory of the naps_data.jar: 

```
java -cp naps_data.jar com.dbf.naps.data.download.integrated.NAPSIntegratedDataDownloader -p C:\temp\NAPSData\RawFiles -t 10
```

For more information about the possible command line arguments, see the NAPSIntegratedDataDownloader section [below](#napsintegrateddatadownloader).

## Loading the Data

There are three separate tools included in this tool box that are used to parse the data from the files downloaded in the previous step and insert that data in that database. Any of the three tools will automatically create the database schema, if needed. The sites definition data must be loaded first and is required if you will be loading the integrated data into the database. Ensure that the PostgreSQL database previously created is running.

**NAPSSitesLoader**

This tool will load all of the NAPS site definitions into the database. This must be run before running the NAPSIntegratedDataLoader. Assuming using all default database connection parameters, you can run the tool using the following command line command, on Windows, in the directory of the naps_data.jar: 

```
java -cp naps_data.jar com.dbf.naps.data.loader.sites.NAPSSitesLoader -p C:\temp\NAPSData\RawFiles -t 10
```

For more information about the possible command line arguments, see the NAPSSitesLoader section [below](#napssitesloader).

**NAPSContinuousDataLoader**

This tool will load all of the NAPS continuous air quality data into the database. This is optional and only needs to be run if you want to analyze the continuous air quality data. Assuming using all default database connection parameters, you can run the tool using the following command line command, on Windows, in the directory of the naps_data.jar: 

```
java -cp naps_data.jar com.dbf.naps.data.loader.continuous.NAPSContinuousDataLoader -p C:\temp\NAPSData\RawFiles\ContinuousData -t 10
```

For more information about the possible command line arguments, see the NAPSContinuousDataLoader section [below](#napscontinuousdataloader).

**NAPSIntegratedDataLoader**

This tool will load all of the NAPS integrated air quality data into the database. This is optional and only needs to be run if you want to analyze the integrated air quality data. This must be run after all of the NAPS site definitions have been loaded into the database using NAPSSitesLoader (see above). Assuming using all default database connection parameters, you can run the tool using the following command line command, on Windows, in the directory of the naps_data.jar: 

```
java -cp naps_data.jar com.dbf.naps.data.loader.integrated.NAPSIntegratedDataLoader -p C:\temp\NAPSData\RawFiles\IntegratedData -t 10
```

For more information about the possible command line arguments, see the NAPSIntegratedDataLoader section [below](#napsintegrateddataloader).

## Querying the Data

(coming soon)

## Generating Heat Maps

(coming soon)

## Installing Microsoft Power BI

The desktop version of Microsoft Power BI is a free tool for exploring and visualizing data. You can find it [here](https://go.microsoft.com/fwlink/?LinkId=2240819). Unfortunately, it only supports Windows x86-64 systems. I do plan to eventually make sample reports for other BI/Data Visualization software.

## Creating a Report

After starting Microsoft Power BI and creating a new blank report, you will need connect to your database, previously populated with NAPS data (see steps above). Provided your database is installed locally and you have used all default database settings, connecting should be as easy as selecting the PostgreSQL database option in the Get Data menu, and entering `localhost` as the Server and `postgres` as the User.
![get data](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/f9cf552f-cefa-4386-b911-bb440c9835aa)
![Connect to DB](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/878af378-57a2-4792-8dd3-dd133fcda7e0)

If you previously chose to load the entire NAPS dataset into your database, then I would highly suggest using the DirectQuery Data Connectivity mode, since there is likely too much database for Power BI to import. After successfully connecting to the database, Power BI will ask what tables you want to use. You should select all of them.

![Screenshot 2024-06-19 231428](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/cac00bbc-557a-4f7c-bb9e-4f2d65cea60c)

It will then connect to each of the tables and build a model.
![image](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/21fa57d8-a15f-407c-bbd9-057712c945fa)

If all goes well, the model view of your report should look like the following:

![image](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/0e7c51a7-d90a-421a-9d11-a12d1d8dfe62)

You can now drag-and-drop columns onto the visualization to start building your report/dashboard. If you would like to view the sample report to see how it was made, you can simply download it from [here](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/raw/main/reports/Pollutant%20Levels%20per%20Site.pbix) and open it in Power BI.



# NAPS Site Tools

The following tools are used for downloading a list of NAPS sites and loading the site definitions into a database.

## NAPSSitesDownloader

A Java tool that downloads a single file containing all of the sites (sampling stations) for the NAPS program. This is the simplest tool in the toolbox and is only included for sake of completeness. The file is downloaded from [here](https://data-donnees.az.ec.gc.ca/api/file?path=/air%2Fmonitor%2Fnational-air-pollution-surveillance-naps-program%2FProgramInformation-InformationProgramme%2FStationsNAPS-StationsSNPA.csv) to the specified directory.

You can invoke this tool by running the class `com.dbf.naps.data.download.sites.NAPSSitesDownloader`. Note that the threadCount argument is meaningless since there is only one file to download.

**Command line usage:**
```
 -o,  --overwriteFiles       Replace the existing file.
 -p,  --downloadPath <arg>   Local path for downloaded files.
 -t,  --threadCount <arg>    Maximum number of parallel threads.
```

## NAPSSitesLoader

A Java tool that loads all of the sites (sampling stations) for the NAPS program (downloaded by the NAPSContinuousDataDownloader) from the provided directory into a PostgreSQL database, as specified. This tool looks for a single file named "sites.csv" in the provided directory. The database schema is automatically created when the tool runs. Once all the data is loaded, there should be 789 rows of data (as of October 2024) in the sites table of your database. 

You can invoke this tool by running the class `com.dbf.naps.data.loader.sites.NAPSSitesLoader`. Note that the threadCount argument is meaningless since there is only one file to process.

**Command line usage:**
```
 -p,   --dataPath <arg>       Local path for the raw data file previously downloaded (sites.csv).
 -dbh, --dbHost <arg>         Hostname for the PostgreSQL database. Default: localhost
 -dbt, --dbPort <arg>         Port for the PostgreSQL database. Default: 5432
 -dbn, --dbName <arg>         Database name for the PostgreSQL database. Default: naps
 -dbu, --dbUser <arg>         Database user name for the PostgreSQL database. Default: postgres
 -dbp, --dbPass <arg>         Database password for the PostgreSQL database. Default: password
 -t,   --threadCount <arg>    Maximum number of parallel threads.
```

# Continuous Data Tools

The following tools are used for downloading continuous air quality data, loading the data into a database and exporting the data to CSV files. The continuous data represents instantaneous air quality measurements collected on a continuous bases and reported hourly. 

## NAPSContinuousDataDownloader

A Java tool that will download all of the hourly continuous data for the provided years into the provided directory. All file names are unique and all files are downloaded into a single directory. Files are downloaded from [here](https://data-donnees.az.ec.gc.ca/data/air/monitor/national-air-pollution-surveillance-naps-program/Data-Donnees/).

You can invoke this tool by running the class `com.dbf.naps.data.download.continuous.NAPSContinuousDataDownloader`.

**Command line usage:**
```
 -o,  --overwriteFiles       Replace existing files.
 -p,  --downloadPath <arg>   Local path for downloaded files.
 -t,  --threadCount <arg>    Maximum number of parallel threads.
 -ye, --yearEnd <arg>        End year (inclusive).
 -ys, --yearStart <arg>      Start year (inclusive).
```

## NAPSContinuousDataLoader

A Java tool that loads all of the raw continuous data, previously downloaded by the NAPSContinuousDataDownloader, from the provided directory into a PostgreSQL database, as specified. The database schema is automatically created when the tool runs. This tool automatically cleans-up and fixes data inconsistencies as it finds them. Once all the data is loaded, there should be about 275 million rows of data (as of October 2024) in the continuous_data table of your database.

You can invoke this tool by running the class `com.dbf.naps.data.loader.continuous.NAPSContinuousDataLoader`.

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

## NAPSContinuousDataQuery

This powerful Java tool allows you to dynamically query the NAPS continuous data that was loaded into a PostgreSQL database using the [NAPSContinuousDataLoader](#napscontinuousdataloader). This tool is intended to be used for aggregating data (i.e. average, sum, minimum, maximum, etc.) that is group by one or more field (e.g. pollutant, site, year, month, day, etc.). If you need to generate large tables of data that do not involve grouping functions, have a look at the [NAPSContinuousDataExporter](#napscontinuousdataexporter).

You can invoke this tool by running the class `com.dbf.naps.data.analysis.query.continuous.NAPSContinuousDataQuery`.

**Command line usage:**
```
 -a,	--aggregateFunction <arg>  Data aggregation function (AVG, MIN, MAX, COUNT, SUM, NONE).
 -cn,	--cityName <arg>           City name, partial match.
 -d,	--days <arg>               Comma-separated list of days of the month.
 -dbh,	--dbHost <arg>             Hostname for the PostgreSQL database. Default: localhost
 -dbn,	--dbName <arg>             Database name for the PostgreSQL database. Default: naps
 -dbp,	--dbPass <arg>             Database password for the PostgreSQL database. Default: password
 -dbt,	--dbPort <arg>             Port for the PostgreSQL database. Default: 5432
 -dbu,	--dbUser <arg>             Database user name for the PostgreSQL database. Default: postgres
 -fp,	--filePerPollutant         Create a separate file for each pollutant.
 -fs,	--filePerSite              Create a separate file for each site.
 -fy,	--filePerYear              Create a separate file for each year.
 -g1,	--group1 <arg>             Data field for level 1 grouping.
 -g2,	--group2 <arg>             Data field for optional level 2 grouping.
 -g3,	--group3 <arg>             Data field for optional level 3 grouping
 -g4,	--group4 <arg>             Data field for optional level 4 grouping
 -g5,	--group5 <arg>             Data field for optional level 5 grouping
 -m,	--months <arg>             Comma-separated list of months of the year, starting at 1 for January.
 -o,	--overwriteFiles           Replace existing files.
 -p,	--dataPath <arg>           Local path to save the data.
 -pn,	--pollutants <arg>         Comma-separated list of pollutant names.
 -pt,	--provTerr <arg>           Comma-separated list of 2-digit province & territory codes.
 -rlb,	--resultLowerBound <arg>   Lower bound (inclusive) of post-aggregated results to include. Results less than this
                                     threshold will be filtered out of the result set after aggregation.
 -rub,	--resultUpperBound <arg>   Upper bound (inclusive) of post-aggregated results to include. Results greater than
                                     this threshold will be filtered out of the result set after aggregation.
 -sc,	--showSampleCount          Include the sample count (number of samples or data points) in the result set.
 -scm,	--minSampleCount <arg>     Minimum sample count (number of samples or data points) in order to be included in the
                                     result set.
 -sid,	--sites <arg>              Comma-separated list of site IDs.
 -sn,	--siteName <arg>           NAPS site (station) name, partial match.
 -stdDevPop, --showStdDevPop       Include the population standard deviation in the result set.
 -stdDevSmp, --showStdDevSamp      Include the sample standard deviation in the result set.
 -t,	--threadCount <arg>        Maximum number of parallel threads.
 -vlb,	--valueLowerBound <arg>    Lower bound (inclusive) of pre-aggregated raw values to include. Values less than this
                                     threshold will be filtered out before aggregation.
 -vub,--valueUpperBound <arg>      Upper bound (inclusive) of pre-aggregated raw values to include. Values greater than
                                     this threshold will be filtered out before aggregation.
 -ye,--yearEnd <arg>               End year (inclusive).
 -ys,--yearStart <arg>             Start year (inclusive).
```

**Notes:**
- Possible values for `group1` through `group5` are `YEAR,MONTH, DAY, HOUR, DAY_OF_WEEK, DAY_OF_YEAR, WEEK_OF_YEAR, NAPS_ID, POLLUTANT, PROVINCE_TERRITORY, URBANIZATION`.
- (TBD - additional rules & restrictions)


**Example Query:**

Say, for example, you are studying the effects of wildfires on air quality in Canada and you want generate a unique table of data for each site and each year. You want those tables to contain each day of summer months between 2018 and 2022 where the average PM2.5 measurement for that day exceeded a threshold of 20, in all sites in Alberta.

In addition to the obligatory `-dataPath` option, you can add the following command line options:
```
-provTerr AB
-months 5,6,7,8,9
-pollutants PM2.5
-yearStart 2018
-yearEnd 2022
-group1 month
-group2 day
-aggregateFunction avg
-minSampleCount 4
-valueUpperBound 1000
-resultLowerBound 20
-showSampleCount
-showStdDevSamp
-filePerSite
-filePerYear
```

Here is an explanation of what each option does:
- `-provTerr AB` limits the results to only sites in Alberta.
- `-months 5,6,7,8,9` limits the results to only the months of May-September
- `-pollutants PM2.5` limits the results to only PM2.5 measurements.
- `-yearStart 2018` will consider all years 2018 and later.
- `-yearEnd 2022` will consider all years 2022 and earlier.
- `-group1 month` will group and order the data points first by month of the year, then
- `-group2 day` will group and order the data points second by day of the month.
- `-aggregateFunction avg` will average out the all the data points for each day.
- `-minSampleCount 4` will only include results for days that had at least 4 data points.
- `-valueUpperBound 1000` will discard any data points with a value greater than 1000.
- `-resultLowerBound 20` will only include results for day where the average PM2.5 measurement exceeded the threshold of 20.
- `-showSampleCount` will include a column showing the number of data points.
- `-showStdDevSamp` will include a column showing sample standard deviation.
- `-filePerSite` will generate a separate table for each NAPS site, and
- `-filePerYear` will generate a separate table for each year. 

With these options, the results will include the average of the 24 PM2.5 measurements that were taken each day. It will only include results for days that had at least 4 data points, ensuring the results are statistically significant. Any data point with a value greater than 1000 is considered outliers and will be excluded because, in our hypothetical scenario, they exceed the maximum possible range for PM2.5 for the air quality sensors. A column indicating the number of samples (data points) will be included in the results. Also included is a column indicating the standard deviation, which shows how much the measurements for each day vary. Since a lower bound is specified, all the days where the average PM2.5 measurement was below the threshold of 20 (considered 'Poor' in terms of air quality) will be excluded; only poor air quality days will be included in the results. A separate table of results will be generated for each combination of naps site and year, provided data is available for that combination.

The complete command line command is the following:
```
 java -cp naps_data.jar com.dbf.naps.data.analysis.query.continuous.NAPSContinuousDataQuery -p C:\temp\NAPSData\Queries -t 5 -provTerr AB -months 5,6,7,8,9 -pollutants PM2.5 -yearStart 2018 -yearEnd 2022 -group1 month -group2 day -aggregateFunction avg -minSampleCount 4 -valueUpperBound 1000 -resultLowerBound 20 -showSampleCount -showStdDevSamp -filePerSite -filePerYear
```
The above example generated 224 tables of data, each saved in its own CSV file. The table for Redwater, Alberta contains only the poor air quality days and shows that nearly the entire month of August 2018 had poor air quality due to smoke from [B.C. wildfires](https://www.cbc.ca/news/canada/edmonton/smoke-from-b-c-wildfires-prompts-air-quality-advisories-across-alberta-1.4777625). 

![image](https://github.com/user-attachments/assets/9e888c4d-524d-4e81-91d9-f97a3cfa74af)

## NAPSContinuousDataExporter

A Java tool that exports the continuous data, previously loaded by the NAPSContinuousDataLoader, from a PostgreSQL database to one or more CSV files at the directory location specified. The data is in a flat, denormalized, CSV format and is encoded in UTF-8 with a BOM. This format is compatible with all modern versions of Excel. The tool allows you to specify what years, pollutants, and sites you want to export. It also lets you specify if you want the data grouped into a single file by any combination of per year, per pollutant and per site.

You can invoke this tool by running the class `com.dbf.naps.data.exporter.continuous.NAPSContinuousDataExporter`.

**Command line usage:**
```
 -t,--threadCount <arg>   Maximum number of parallel threads.
 -dbh,--dbHost <arg>      Hostname for the PostgreSQL database. Default: localhost
 -dbn,--dbName <arg>      Database name for the PostgreSQL database. Default: naps
 -dbp,--dbPass <arg>      Database password for the PostgreSQL database. Default: password
 -dbt,--dbPort <arg>      Port for the PostgreSQL database. Default: 5432
 -dbu,--dbUser <arg>      Database user name for the PostgreSQL database. Default: postgres
 -fp,--filePerPollutant   Create a separate file for each pollutant.
 -fs,--filePerSite        Create a separate file for each site.
 -fy,--filePerYear        Create a separate file for each year.
 -o,--overwriteFiles      Replace existing files.
 -p,--dataPath <arg>      Local path to save the exported data.
 -pn,--pollutants <arg>   Comma-separated list of pollutant names.
 -sid,--sites <arg>       Comma-separated list of site IDs.
 -ye,--yearEnd <arg>      End year (inclusive).
 -ys,--yearStart <arg>    Start year (inclusive).
```

# Integrated Data Tools

The following tools are used for downloading integrated air quality data, loading the data into a database and exporting the data to CSV files. The integrated data represents air quality measurements that are sampled over a longer duration (typically 24 hours) and collected on a regular basis (every few days).

## NAPSIntegratedDataDownloader

A Java tool that will download all of the integrated data for the provided years into the provided directory. Since many of the file names of the files conflict, each year will be downloaded into its own sub-directory. Files are downloaded from [here](https://data-donnees.az.ec.gc.ca/data/air/monitor/national-air-pollution-surveillance-naps-program/Data-Donnees/). 

You can invoke this tool by running the class `com.dbf.naps.data.download.integrated.NAPSIntegratedDataDownloader`.

**Command line usage:**
```
 -o,  --overwriteFiles       Replace existing files.
 -p,  --downloadPath <arg>   Local path for downloaded files.
 -t,  --threadCount <arg>    Maximum number of parallel threads.
 -ye, --yearEnd <arg>        End year (inclusive).
 -ys, --yearStart <arg>      Start year (inclusive).
```

## NAPSIntegratedDataLoader

A Java tool that loads all of the raw integrated data, previously downloaded by the NAPSIntegratedDataDownloader, from the provided directory into a PostgreSQL database, as specified. The database schema is automatically created when the tool runs. This tool automatically cleans-up and fixes data inconsistencies as it finds them. Once all the data is loaded, there should be about 14 million rows of data (as of October 2024) in the integrated_data table of your database.

You can invoke this tool by running the class `com.dbf.naps.data.loader.integrated.NAPSIntegratedDataLoader`.

**NOTE:** The NAPSSitesLoader must be run first in order to populate the naps.sites table with site (station) definitions prior to loading the integrated data! 

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

## NAPSIntegratedDataQuery

This powerful Java tool allows you to dynamically query the NAPS integrated data that was loaded into a PostgreSQL database using the [NAPSIntegratedDataLoader](#napsintegrateddataloader). It functions the same as the [NAPSContinuousDataQuery](#napsContinuousdataloader) and accepts all of the same command line arguments, with the exception that the data fields used for grouping cannot include `HOUR`, since hour attribute only applies to continuous data, not integrated data.

You can invoke this tool by running the class `com.dbf.naps.data.analysis.query.integrated.NAPSIntegratedDataQuery`.

**Command line usage:**
```
 -a,	--aggregateFunction <arg>  Data aggregation function (AVG, MIN, MAX, COUNT, SUM, NONE).
 -cn,	--cityName <arg>           City name, partial match.
 -d,	--days <arg>               Comma-separated list of days of the month.
 -dbh,	--dbHost <arg>             Hostname for the PostgreSQL database. Default: localhost
 -dbn,	--dbName <arg>             Database name for the PostgreSQL database. Default: naps
 -dbp,	--dbPass <arg>             Database password for the PostgreSQL database. Default: password
 -dbt,	--dbPort <arg>             Port for the PostgreSQL database. Default: 5432
 -dbu,	--dbUser <arg>             Database user name for the PostgreSQL database. Default: postgres
 -fp,	--filePerPollutant         Create a separate file for each pollutant.
 -fs,	--filePerSite              Create a separate file for each site.
 -fy,	--filePerYear              Create a separate file for each year.
 -g1,	--group1 <arg>             Data field for level 1 grouping.
 -g2,	--group2 <arg>             Data field for optional level 2 grouping.
 -g3,	--group3 <arg>             Data field for optional level 3 grouping
 -g4,	--group4 <arg>             Data field for optional level 4 grouping
 -g5,	--group5 <arg>             Data field for optional level 5 grouping
 -m,	--months <arg>             Comma-separated list of months of the year, starting at 1 for January.
 -o,	--overwriteFiles           Replace existing files.
 -p,	--dataPath <arg>           Local path to save the data.
 -pn,	--pollutants <arg>         Comma-separated list of pollutant names.
 -pt,	--provTerr <arg>           Comma-separated list of 2-digit province & territory codes.
 -rlb,	--resultLowerBound <arg>   Lower bound (inclusive) of post-aggregated results to include. Results less than this
                                     threshold will be filtered out of the result set after aggregation.
 -rub,	--resultUpperBound <arg>   Upper bound (inclusive) of post-aggregated results to include. Results greater than
                                     this threshold will be filtered out of the result set after aggregation.
 -sc,	--showSampleCount          Include the sample count (number of samples or data points) in the result set.
 -scm,	--minSampleCount <arg>     Minimum sample count (number of samples or data points) in order to be included in the
                                     result set.
 -sid,	--sites <arg>              Comma-separated list of site IDs.
 -sn,	--siteName <arg>           NAPS site (station) name, partial match.
 -stdDevPop, --showStdDevPop       Include the population standard deviation in the result set.
 -stdDevSmp, --showStdDevSamp      Include the sample standard deviation in the result set.
 -t,	--threadCount <arg>        Maximum number of parallel threads.
 -vlb,	--valueLowerBound <arg>    Lower bound (inclusive) of pre-aggregated raw values to include. Values less than this
                                     threshold will be filtered out before aggregation.
 -vub,--valueUpperBound <arg>      Upper bound (inclusive) of pre-aggregated raw values to include. Values greater than
                                     this threshold will be filtered out before aggregation.
 -ye,--yearEnd <arg>               End year (inclusive).
 -ys,--yearStart <arg>             Start year (inclusive).
```

Possible values for `group1` through `group5` are `YEAR,MONTH, DAY, DAY_OF_WEEK, DAY_OF_YEAR, WEEK_OF_YEAR, NAPS_ID, POLLUTANT, PROVINCE_TERRITORY, URBANIZATION`. 

All of the same rules and restrictions of the [NAPSContinuousDataQuery](#napsContinuousdataloader) apply.

## NAPSIntegratedDataExporter

A Java tool that exports the integrated data, previously loaded by the NAPSIntegratedDataLoader, from a PostgreSQL database to one or more CSV files at the directory location specified. The data is in a flat, denormalized, CSV format and is encoded in UTF-8 with a BOM. This format is compatible with all modern versions of Excel. The tool allows you to specify what years, pollutants, and sites you want to export. It also lets you specify if you want the data grouped into a single file by any combination of per year, per pollutant and per site.

You can invoke this tool by running the class `com.dbf.naps.data.exporter.integrated.NAPSIntegratedDataExporter`.

**Command line usage:**
```
 -t,--threadCount <arg>   Maximum number of parallel threads.
 -dbh,--dbHost <arg>      Hostname for the PostgreSQL database. Default: localhost
 -dbn,--dbName <arg>      Database name for the PostgreSQL database. Default: naps
 -dbp,--dbPass <arg>      Database password for the PostgreSQL database. Default: password
 -dbt,--dbPort <arg>      Port for the PostgreSQL database. Default: 5432
 -dbu,--dbUser <arg>      Database user name for the PostgreSQL database. Default: postgres
 -fp,--filePerPollutant   Create a separate file for each pollutant.
 -fs,--filePerSite        Create a separate file for each site.
 -fy,--filePerYear        Create a separate file for each year.
 -o,--overwriteFiles      Replace existing files.
 -p,--dataPath <arg>      Local path to save the exported data.
 -pn,--pollutants <arg>   Comma-separated list of pollutant names.
 -sid,--sites <arg>       Comma-separated list of site IDs.
 -ye,--yearEnd <arg>      End year (inclusive).
 -ys,--yearStart <arg>    Start year (inclusive).
```

# How To Run Individual Tools

You can find the latest package [here](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/packages/2159892). Alternatively, in the /target directory you can find the pre-compiled jar file naps_data.jar. This is a shaded jar file which means that it contains all of the 3rd party dependencies inside of it. Assuming you have Java 17 installed and 
part of your system path you can simply invoke the class by running the following:
```
 java -cp naps_data.jar com.dbf.naps.data.loader.continuous.NAPSContinuousDataLoader -p C:\temp\NAPSData\RawFiles -t 24
```

In the above example, the data will be loaded from the C:\temp\NAPSData\RawFiles directory into the database using a thread pool size of 24, and all default database connection options (see above for details).

# Database Design

I am using a normalized relational PostgreSQL database to store the data. I have chosen to hold the continuous data and the integrated data in separate tables to improve performance. I don't think there is a frequent need to query the data in both tables at the same time. The following diagram illustrates the schema design.

![schema diagram](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/fdc3b74d-f073-491b-8bdc-c811fecfcb2b)


# Known Issues

This repository makes use of GitHub's built-in issue tracker. You can view all open issues [here](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/issues). Most of the issues are problems with the data files that are distributed from the NAPS website.

# Developer Notes

- Requires Java 17
- Tested with PostgreSQL 16.3. The database should be created with the UTF-8 characterset in order to support accented characters.
- If you want to build the jar from the source code you will need [Apache Maven](https://maven.apache.org/).
- Other than the sample reports, everything in this toolbox should be multi-platform (supporting Windows, Linux, MacOS, etc.) and multi-architecture (supporting x86 and ARM). However, I am only one person and I have only developed and tested the code on Windows 11 x64.
- The NAPS data is parsed from the Excel files downloaded from the website. There are 3 different Excel formats used: XLSX (Excel 2007 and later), XLS (BIFF8, Excel 5 - 1993 and later), and XLS (BIFF4, Excel 4 - 1992). For parsing XLSX, I'm using the [Apache POI](https://poi.apache.org/) library. For parsing XLS BIFF8, I'm using JXL - the [Java Excel API](https://jexcelapi.sourceforge.net/). For parsing XLS BIFF4, I could not find a Java library that supports it, so I built my own parser adapted from the Apache POI library's [OldExcelExtractor](https://github.com/apache/poi/blob/trunk/poi/src/main/java/org/apache/poi/hssf/extractor/OldExcelExtractor.java).

# Legal Stuff

Copyright (c) 2024 David Fortin

This software (NAPS Data Analysis Toolbox) is provided by David Fortin under the MIT License, meaning you are free to use it however you want, as long as you include the original copyright notice (above) and license notice in any copy you make. You just can't hold me liable in case something goes wrong. License details can be read [here](https://github.com/dbeaudoinfortin/NAPSDataAnalysis?tab=MIT-1-ov-file)

The data itself is provided by the [National Air Pollution Surveillance](https://www.canada.ca/en/environment-climate-change/services/air-pollution/monitoring-networks-data/national-air-pollution-program.html) (NAPS) program, which is run by the Analysis and Air Quality Section of Environment and Climate Change Canada. The data is licensed under the terms of the Canadian [Open Government Licence](https://open.canada.ca/en/open-government-licence-canada) and can be freely re-published under those terms.
