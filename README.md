# NAPS Data Analysis Toolbox

<p align="center">
<img src="https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/bc1f2673-05fd-4713-8be7-57119d038358"/>
</p>

## Contents

- [Overview](#overview)
- [Clean Data Exports](#clean-data-exports)
- [Data Analysis](#data-analysis)
- [Dashboards](#dashboards)
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
  * [NAPSContinuousHeatMap](#napscontinuousheatmap)
  * [NAPSContinuousDataExporter](#napscontinuousdataexporter)
- [Integrated Data Tools](#integrated-data-tools)
  * [NAPSIntegratedDataDownloader](#napsintegrateddatadownloader)
  * [NAPSIntegratedDataLoader](#napsintegrateddataloader)
  * [NAPSIntegratedDataQuery](#napsintegrateddataquery)
  * [NAPSIntegratedHeatMap](#napsintegratedheatmap)
  * [NAPSIntegratedDataExporter](#napsintegrateddataexporter)
- [How To Run Individual Tools](#how-to-run-individual-tools)
- [Database Design](#database-design)
- [Known Issues](#known-issues)
- [Developer Notes](#developer-notes)
- [Legal Stuff](#legal-stuff)

# Overview
Welcome to the Canada National Air Pollution Surveillance Program (NAPS) data downloader, extractor, importer, analysis, and visualization toolbox. 

This project will eventually contain a collection of tools to assist in the analysis of Canadian air quality data. The data is provided by the National Air Pollution Surveillance (NAPS) program, which is part of Environment and Climate Change Canada. You can view the original data [here](https://data-donnees.az.ec.gc.ca/data/air/monitor/national-air-pollution-surveillance-naps-program/).

I started this project because, despite the wealth of data that NAPS provides, analysing it is challenging, time consuming and error prone. The data from the [NAPS portal](https://data-donnees.az.ec.gc.ca/data/air/monitor/national-air-pollution-surveillance-naps-program/) is spread out in hundreds of XLS/XLSX/CSV files, with dozens of formats, different units of measure, different naming conventions, etc. With this toolbox, anyone can use the [downloader tools](#napscontinuousdatadownloader) to download all of the data they need in one command. I then provide the [tools](#napscontinuousdataloader) needed to parse all this data, clean it up and import it into a single simple, clean database schema. After that, you can analyse the data using whatever tool works best for you. I provide a powerful [dynamic query](#napscontinuousdataquery) tool, a CSV [exporter tool](#napscontinuousdataexporter), a [heat map visualization](#data-analysis) [tool](#napscontinuousheatmap) to generate pretty graphs, and a couple example [BI dashboards](#dashboards) to get you started with BI tools. And if all of that is too complicated, you might still be interested in the [clean data exports](#clean-data-exports) that republish the NAPS data in a consistent format.

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

# Data Analysis

**Dynamic Queries**

The NAPS Data Analysis Toolbox provides powerful tools for the analysis of Canadian air quality data. The dynamic query tools for both the [continuous](#napscontinuousdataquery) and [integrated](#napsintegrateddataquery) data allow you to run highly customized queries to aggregate or simply retrieve the data in the way that you need it, in a single command.

The dynamic query tools have support for several types of aggregation functions, multiple levels of grouping, filtering on many dimensions (site IDs, site name, pollutants, hour, days of the week, days of the month, months, years, provinces/territories, city name, site type, site urbanization), standard deviation functions, sample counts, minimum sample counts (to optionally ensure a statistically significant number of data points), lower and upper bounds for data points (to optionally exclude outliers) and post-aggregation lower and upper bounds (to eliminate results outside the scope of interest). I'm planning to add even more functionality in the future.

Say, for example, you want to know how many times the hourly reading for carbon monoxide exceeded the national standard of 13ppm across all of Canada for the years between 1974 and 2022. You can produce the following table by running the following command:

```
-pollutants CO
-group1 year
-yearStart 1974
-yearEnd 2022
-aggregateFunction count
-valueLowerBound 13
```

For more details on how to run these query tools, see the [continuous](#napscontinuousdataquery) and [integrated](#napsintegrateddataquery) data query sections below.

![image](https://github.com/user-attachments/assets/b5d1d43a-8c7c-4424-aee9-596111532065)

**Heat Map Diagrams**

The NAPS Data Analysis Toolbox provides tools for generating heat map diagrams for both the [continuous](#napscontinuousheatmap) and [integrated](#napsintegratedheatmap) data. These heat maps are highly customizable can be generated in a single command. They make it much easier to spot trends in the data. For example, here is the entire history carbon monoxide readings for all NAPS sites, for all of Canada, aggregated into a single heat map diagram.

![Avg_CO_By Day of the Year and Year](https://github.com/user-attachments/assets/c252ea64-4493-49aa-8295-33709243a8ce)

From this diagram alone there are some trends that immediately stand out, such as:
- the significant improvement to air quality over the years,
- the higher concentrations of CO in winter months,
- the reduction of CO on weekends (the apparent diagonal lines),
- the reduction of CO on holidays, such as Christmas and New Year's day,
- the larger number of outlier reading in 2016.

We can change the x-axis to get a view of what is happening on the weekends compared to the weekdays:

![Avg_CO_By Day of the Week and Year](https://github.com/user-attachments/assets/25024f6e-e70f-43b2-9926-006a93ec4be6)

(Note that this second heat map uses a different colour palette that better suits the data.) Focusing on a single year (2002) makes it easy to see the difference between summer and winter months:

![Monoxide_Yearly_2002](https://github.com/user-attachments/assets/9224b0b7-d57a-447f-ab1c-324bd36fd991)

Similarly, we can look at any pollutant, or all pollutants. Here is the same time frame, but for SO2, using a third colour palette:

![Avg_SO2_By Day of the Year and Year](https://github.com/user-attachments/assets/721cf42e-cd32-42b7-ab47-9f0538856cea)

And here are two heat maps for lead, showing the average and maximum concentrations, respectively. This makes it easy to see the large reduction in air pollution starting with the ban on leaded gasoline in 1990:

![Avg_Lead_By Month and Year](https://github.com/user-attachments/assets/9660ba50-52bf-4b9b-ba17-7fcde5766e34)

![Max_Lead_By Month and Year](https://github.com/user-attachments/assets/a7b8b1ce-dfcf-4778-b158-fabde5fb27f3)

(The provinces of Ontario & Quebec were only chosen to demonstrate the ability to filter by province/territory.)

The queries used to generate these heat maps are fully dynamic and there are several colour palettes to choose from. The minimum and maximum values that determine the colour scale are calculated automatically, but there are also options to clamp/limit the values to a lower and an upper bound to prevent outliers from shifting the entire scale. The titles, axis labels, legends and file names are all automatically generated. There is also an option to produce an accompanying CSV table containing all of the data used to render the heat map. 

For more details on how to generate custom heat maps, see the [continuous](#napscontinuousheatmap) and [integrated](#napsintegratedheatmap) heat map sections below.

# Dashboards

In the [/reports](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/tree/main/reports) directory you will find a sample Microsoft Power BI report. This report is example of  how a BI tool can be used for visualizing the NAPS data. This report is designed to be used in conjunction with the database schema built and populated by the tools in this tool box. For information on how to set-up your database and connect to it using Power BI, check out the [Getting Started](#getting-started) section below.

![Report 2](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/8104f1e2-8c9d-4d86-ac32-284274d2eaed)

![Report 1](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/a6411def-4a27-45d1-8afe-9c8f17768577)

![Report 3](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/7869abff-c45e-4b16-892f-663dff79862f)

I plan to eventually add sample reports for other BI/Data Visualization software that are open source, free, and available on more platforms than just Windows x86-64.

# Getting Started

The following steps will guide you in building a database from scratch, populating it with NAPS data, and querying/analysing the NAPS data.

## Installing PostgreSQL

The tools in this toolbox are designed to be used with a PostgreSQL database. PostgreSQL was chosen because it is feature-rich, highly performant, open-source, free, and widely available on multiple platforms.

You can download an installer of PostgreSQL for every major desktop/server OS [here](https://www.postgresql.org/download/). The installer will walk you through the process of creating an initial database with a user and password.

## Installing Java

The tools in this toolbox are written in Java. You will need the Java 21 or later in order to run any of the tools. The Java JDK is free, multi-platform (supporting Windows, Linux, MacOS, etc.), multi-architecture (supporting x86 and ARM), and can be downloaded [directly from Oracle](https://www.oracle.com/ca-en/java/technologies/downloads/).

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

This tool will load all of the NAPS site definitions into the database. This must be run before running the NAPSIntegratedDataLoader. Assuming you are using all default database connection parameters, you can run the tool using the following command line command, on Windows, in the directory of the naps_data.jar: 

```
java -cp naps_data.jar com.dbf.naps.data.loader.sites.NAPSSitesLoader -p C:\temp\NAPSData\RawFiles -t 10
```

For more information about the possible command line arguments, see the NAPSSitesLoader section [below](#napssitesloader).

**NAPSContinuousDataLoader**

This tool will load all of the NAPS continuous air quality data into the database. This is optional and only needs to be run if you want to analyze the continuous air quality data. Assuming you are using all default database connection parameters, you can run the tool using the following command line command, on Windows, in the directory of the naps_data.jar: 

```
java -cp naps_data.jar com.dbf.naps.data.loader.continuous.NAPSContinuousDataLoader -p C:\temp\NAPSData\RawFiles\ContinuousData -t 10
```

For more information about the possible command line arguments, see the NAPSContinuousDataLoader section [below](#napscontinuousdataloader).

**NAPSIntegratedDataLoader**

This tool will load all of the NAPS integrated air quality data into the database. This is optional and only needs to be run if you want to analyze the integrated air quality data. This must be run after all of the NAPS site definitions have been loaded into the database using NAPSSitesLoader (see above). Assuming you are using all default database connection parameters, you can run the tool using the following command line command, on Windows, in the directory of the naps_data.jar: 

```
java -cp naps_data.jar com.dbf.naps.data.loader.integrated.NAPSIntegratedDataLoader -p C:\temp\NAPSData\RawFiles\IntegratedData -t 10
```

For more information about the possible command line arguments, see the NAPSIntegratedDataLoader section [below](#napsintegrateddataloader).

## Querying the Data

By now, all the data you wish to analyse should have been loaded into your database using either the [NAPSContinuousDataLoader](#napscontinuousdataloader), the [NAPSIntegratedDataLoader](#napsintegrateddataloader), or both. You may now want to query the data to retrieve what you need for your own analysis.

Assuming you are using all default database connection parameters, you can run the continuous query tool using the following command line command, on Windows, in the directory of the naps_data.jar: 

```
java -cp naps_data.jar com.dbf.naps.data.analysis.query.continuous.NAPSContinuousDataQuery -p C:\temp\NAPSData\queries\continuous -pollutants CO -group1 year -yearStart 1974 -yearEnd 2022 -aggregateFunction count -valueLowerBound 13
```

Likewise, you can run the integrated query tool using the following command line command, on Windows, in the directory of the naps_data.jar: 

```
java -cp naps_data.jar com.dbf.naps.data.analysis.query.integrated.NAPSIntegratedDataQuery -p C:\temp\NAPSData\queries\integrated -pollutants 3-Methyloctane -group1 day_of_week -aggregateFunction avg -showSampleCount
```

These are just example queries; you will need to craft a command that works for your scenario. For more information about the possible command line arguments, see either the [NAPSContinuousDataQuery](#napscontinuousdataquery) section or the [NAPSIntegratedDataQuery](#napsintegrateddataquery) section below.

## Generating Heat Maps

By now, all the data you wish to analyse should have been loaded into your database using either the [NAPSContinuousDataLoader](#napscontinuousdataloader), the [NAPSIntegratedDataLoader](#napsintegrateddataloader), or both. You may now want to generate heat map diagrams to visualize the data as part of your own analysis.

Assuming you are using all default database connection parameters, you can run the continuous heat map tool using the following command line command, on Windows, in the directory of the naps_data.jar: 

```
java -cp naps_data.jar com.dbf.naps.data.analysis.heatmap.continuous.NAPSContinuousDataHeatMap -p C:\temp\NAPSData\heatmaps\continuous -aggregateFunction avg -group1 day_of_year -group2 year -pollutants SO2 -generateCSV -colourGradient 3
```

Likewise, you can run the integrated query tool using the following command line command, on Windows, in the directory of the naps_data.jar: 

```
java -cp naps_data.jar com.dbf.naps.data.analysis.heatmap.integrated.NAPSIntegratedDataHeatMap -p C:\temp\NAPSData\heatmaps\integrated -aggregateFunction max -group1 month -group2 year -pollutants Lead -provTerr ON,QC -generateCSV -colourGradient 2
```

These are just example heat maps; you will need to craft a command that works for your scenario. For more information about the possible command line arguments, see either the [NAPSContinuousHeatMap section](#napscontinuousheatmap) or the [NAPSIntegratedHeatMap section](#napsintegratedheatmap) below.

## Installing Microsoft Power BI

If you are interested, I have created an example Power BI report to demonstrate how dashboards can make use of the database that you previously created and populated with NAPS data in the previous steps above.

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
 -v,  --verbose              Make logging more verbose.
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
 -v,   --verbose              Make logging more verbose.
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
 -v,  --verbose                    Make logging more verbose.
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
 -v,   --verbose              Make logging more verbose.
```

## NAPSContinuousDataQuery

This powerful Java tool allows you to dynamically query the NAPS continuous data that was loaded into a PostgreSQL database using the [NAPSContinuousDataLoader](#napscontinuousdataloader). It will output a CSV file containing a table of data based on the query rules that you provide. This tool is intended to be used for aggregating data (i.e. average, sum, minimum, maximum, etc.) that is grouped by one or more fields (e.g. pollutant, site, year, month, day, etc.). If you need to generate large tables of data that do not involve grouping functions, have a look at the [NAPSContinuousDataExporter](#napscontinuousdataexporter).

You can invoke this tool by running the class `com.dbf.naps.data.analysis.query.continuous.NAPSContinuousDataQuery`.

**Command line usage:**
```
 -a,   --aggregateFunction <arg>  Data aggregation function (AVG, MIN, MAX, COUNT, SUM, NONE).
 -cn,  --cityName <arg>           City name, partial match.
 -d,   --days <arg>               Comma-separated list of days of the month.
 -dbh, --dbHost <arg>             Hostname for the PostgreSQL database. Default: localhost
 -dbn, --dbName <arg>             Database name for the PostgreSQL database. Default: naps
 -dbp, --dbPass <arg>             Database password for the PostgreSQL database. Default: password
 -dbt, --dbPort <arg>             Port for the PostgreSQL database. Default: 5432
 -dbu, --dbUser <arg>             Database user name for the PostgreSQL database. Default: postgres
 -dow, --daysOfWeek <arg>         Comma-separated list of days of the week, starting at 1 for Sunday.
 -fn,  --fileName <arg>           Custom file name without the extension. Will be automatically generated if not defined.
 -fp,  --filePerPollutant         Create a separate file for each pollutant.
 -fs,  --filePerSite              Create a separate file for each site.
 -fy,  --filePerYear              Create a separate file for each year.
 -g1,  --group1 <arg>             Data field for level 1 grouping.
 -g2,  --group2 <arg>             Data field for optional level 2 grouping.
 -g3,  --group3 <arg>             Data field for optional level 3 grouping
 -g4,  --group4 <arg>             Data field for optional level 4 grouping
 -g5,  --group5 <arg>             Data field for optional level 5 grouping
 -m,   --months <arg>             Comma-separated list of months of the year, starting at 1 for January.
 -o,   --overwriteFiles           Replace existing files.
 -p,   --dataPath <arg>           Local path to save the data.
 -pn,  --pollutants <arg>         Comma-separated list of pollutant names.
 -pt,  --provTerr <arg>           Comma-separated list of 2-digit province & territory codes.
 -rlb, --resultLowerBound <arg>   Lower bound (inclusive) of post-aggregated results to include. Results less than this
                                     threshold will be filtered out of the result set after aggregation.
 -rub, --resultUpperBound <arg>   Upper bound (inclusive) of post-aggregated results to include. Results greater than
                                     this threshold will be filtered out of the result set after aggregation.
 -sc,  --showSampleCount          Include the sample count (number of samples or data points) in the result set.
 -scm, --minSampleCount <arg>     Minimum sample count (number of samples or data points) in order to be included in the
                                     result set.
 -sid, --sites <arg>              Comma-separated list of site IDs.
 -sn,  --siteName <arg>           NAPS site (station) name, partial match.
 -stdDevPop, --showStdDevPop       Include the population standard deviation in the result set.
 -stdDevSmp, --showStdDevSamp      Include the sample standard deviation in the result set.
 -t,   --threadCount <arg>        Maximum number of parallel threads.
 -v,   --verbose                  Make logging more verbose.
 -vlb, --valueLowerBound <arg>    Lower bound (inclusive) of pre-aggregated raw values to include. Values less than this
                                     threshold will be filtered out before aggregation.
 -vub, --valueUpperBound <arg>    Upper bound (inclusive) of pre-aggregated raw values to include. Values greater than
                                     this threshold will be filtered out before aggregation.
 -ye,  --yearEnd <arg>            End year (inclusive).
 -ys,  --yearStart <arg>          Start year (inclusive).
```

**Aggregation Rules:**
- The possible values for the aggregation function are (`AVG, MIN, MAX, COUNT, SUM, NONE`).
- The default aggregation function, if not specified, is `AVG`.
- The possible values for `group1` through `group5` are `YEAR, MONTH, DAY, HOUR, DAY_OF_WEEK, DAY_OF_YEAR, WEEK_OF_YEAR, NAPS_ID, POLLUTANT, PROVINCE_TERRITORY, SITE_TYPE, URBANIZATION`.
- The use of an aggregation function does not require the use of grouping (options `group1` through `group5`). This will effectively aggregate all of the data points into a single value. Use the option `--showSampleCount` to include the number of data points that were aggregated.
- The aggregation function cannot be set to `NONE` when specifying grouping using the options `group1` through `group5`. It is possible to set the aggregation function to `NONE` if no groups are specified, but this has limited usefulness since it will produce a table with a single column containing only the raw values (sample data points).
- The minimum sample count option cannot be used when the aggregate function is set to `NONE` since the sample count will always be 1.
- Post-aggregated bounds (both upper and lower) cannot be used when the aggregate function is set to `NONE`.
- Both the population standard deviation and the sample standard deviation cannot be used when the aggregate function is set to `NONE`.
- A check is performed to prevent the aggregation of data from different pollutants with different units of measurement. For example, it would not make sense to calculate the average of data points measured in a mix of µg/m³ and ppb.

**Filtering Rules:**
- The possible values for `provTerr` (province/territory) are either the short codes (`NL, PE, NS, NB, QC, ON, MB, SK, AB, BC, YT, NT, NU`), or the long form (`NEWFOUNDLAND AND LABRADOR, PRINCE EDWARD ISLAND, NOVA SCOTIA, NEW BRUNSWICK, QUEBEC, ONTARIO, MANITOBA, SASKATCHEWAN, ALBERTA, BRITISH COLUMBIA, YUKON, NORTHWEST TERRITORIES, NUNAVUT`).
- The possible values for `month` are either the full names (`JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER`) case-insensitive, or the month numbers (`1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12`), starting at 1 for January.
- The possible values for `daysOfWeek` are either the full names (`SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY`) case-insensitive, or the day of the week numbers (`1, 2, 3, 4, 5, 6, 7`), starting at 1 for Sunday.
- Both site (station) names and city names are treated as case-insensitive partial matches. This means a value of `labrador` will match the city name of `LABRADOR CITY`.

**Other Notes:**
- A title will be automatically generated for the report based on the aggregation and filtering rules that you provide.  

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

## NAPSContinuousHeatMap

A Java tool that generates highly customizable heat map diagrams for the visualization of NAPS continuous data. These heat maps are saved in high resolution PNG format.

You can invoke this tool by running the class `com.dbf.naps.data.analysis.heatmap.continuous.NAPSContinuousDataHeatMap`.

**Command line usage:**
```
 -a,   --aggregateFunction <arg>  Data aggregation function (AVG, MIN, MAX, COUNT, SUM).
 -cg,  --colourGradient <arg>     Heat map colour gradient choice. Values are 1-8 (inclusive).
 -clb, --colourLowerBound <arg>   Heat map colour lower bound (inclusive).
 -cn,  --cityName <arg>           City name, partial match.
 -csv, --generateCSV              Generate a corresponding CSV file containing the raw data for each heat map.
 -cub, --colourUpperBound <arg>   Heat map colour upper bound (inclusive).
 -d,   --days <arg>               Comma-separated list of days of the month.
 -dbh, --dbHost <arg>             Hostname for the PostgreSQL database. Default: localhost
 -dbn, --dbName <arg>             Database name for the PostgreSQL database. Default: naps
 -dbp, --dbPass <arg>             Database password for the PostgreSQL database. Default: password
 -dbt, --dbPort <arg>             Port for the PostgreSQL database. Default: 5432
 -dbu, --dbUser <arg>             Database user name for the PostgreSQL database. Default: postgres
 -dow, --daysOfWeek <arg>         Comma-separated list of days of the week, starting at 1 for Sunday.
 -fn,  --fileName <arg>           Custom file name without the extension. Will be automatically generated if not defined.
 -fp,  --filePerPollutant         Create a separate file for each pollutant.
 -fs,  --filePerSite              Create a separate file for each site.
 -fy,  --filePerYear              Create a separate file for each year.
 -g1,  --group1 <arg>             Data field for the heat map X-axis.
 -g2,  --group2 <arg>             Data field for the heat map Y-axis.
 -m,   --months <arg>             Comma-separated list of months of the year, starting at 1 for January.
 -o,   --overwriteFiles           Replace existing files.
 -p,   --dataPath <arg>           Local path to save the data.
 -pn,  --pollutants <arg>         Comma-separated list of pollutant names.
 -pt,  --provTerr <arg>           Comma-separated list of 2-digit province & territory codes.
 -rlb, --resultLowerBound <arg>   Lower bound (inclusive) of post-aggregated results to include. Results less than this
                                     threshold will be filtered out of the result set after aggregation.
 -rub, --resultUpperBound <arg>   Upper bound (inclusive) of post-aggregated results to include. Results greater than
                                     this threshold will be filtered out of the result set after aggregation.
 -scm, --minSampleCount <arg>     Minimum sample count (number of samples or data points) in order to be included in the
                                     result set.
 -sid, --sites <arg>              Comma-separated list of site IDs.
 -sn,  --siteName <arg>           NAPS site (station) name, partial match.
 -t,   --threadCount <arg>        Maximum number of parallel threads.
 -v,   --verbose                  Make logging more verbose.
 -vlb, --valueLowerBound <arg>    Lower bound (inclusive) of pre-aggregated raw values to include. Values less than this
                                     threshold will be filtered out before aggregation.
 -vub, --valueUpperBound <arg>    Upper bound (inclusive) of pre-aggregated raw values to include. Values greater than
                                     this threshold will be filtered out before aggregation.
 -ye,  --yearEnd <arg>            End year (inclusive).
 -ys,  --yearStart <arg>          Start year (inclusive).
```

**Colour Palettes:**

8 different colour palettes are currently offered. I will plan to eventually add more in the future. The current palettes are the following:
1. A smooth gradient based on the colour wheel from blue to red. All the of the colours are fully saturated.
2. A 12 step gradient from blue to red with less saturation than the first colour palette.
3. A simplified 5 step gradient from blue to red.
4. A two colour gradient from blue to red, with purple mixed in-between.
5. A 5 step colour blind friendly gradient of greenish-yellow to dark orange.
6. A 3 step black-red-orange gradient, similar to black-body radiation, up to approximately 1300 degrees kelvin.
7. Same as number 6 but two more steps are added to extend the scale up to approximately 6500k degrees kelvin.
8. A 2 step grey-scale gradient that should be used for non-colour screen/print-outs.

The default colour palette, if not specified, is number 1. Here are examples of what the colour palette look like, in order:

![Continuous_By Day of the Month and Month_C1](https://github.com/user-attachments/assets/c55ced9a-36af-4006-b47e-40b2ce04bc60)
![Continuous_By Day of the Month and Month_C2](https://github.com/user-attachments/assets/5ca35e2f-d8e8-46b7-b2ef-95e335128480)
![Continuous_By Day of the Month and Month_C3](https://github.com/user-attachments/assets/8485348e-c686-4cfd-81f7-35c6d4a4471e)
![Continuous_By Day of the Month and Month_C4](https://github.com/user-attachments/assets/051332ef-fb9d-465c-b832-e2f2f5a417e1)
![Continuous_By Day of the Month and Month_C5](https://github.com/user-attachments/assets/523a4438-bb37-4236-af18-d5885a4770a7)
![Continuous_By Day of the Month and Month_C6](https://github.com/user-attachments/assets/1085aec3-b82f-42e3-af67-72df9c5af137)
![Continuous_By Day of the Month and Month_C7](https://github.com/user-attachments/assets/fc17e80b-846b-4998-af25-766da3bc09ac)
![Continuous_By Day of the Month and Month_C8](https://github.com/user-attachments/assets/a9f9100e-dfbe-4a39-8dce-1da1bd0e222f)

**Aggregation Rules:**
- The possible values for the aggregation function are (`AVG, MIN, MAX, COUNT, SUM`).
- The use of an aggregation function is mandatory to generate the heat map.
- The default aggregation function, if not specified, is `AVG`.
- Both the `group1` and `group2` options are mandatory since they represent the x-axis and y-axis of the chart, respectively. 
- The possible values for `group1` and `group2` are `YEAR, MONTH, DAY, HOUR, DAY_OF_WEEK, DAY_OF_YEAR, WEEK_OF_YEAR, NAPS_ID, POLLUTANT, PROVINCE_TERRITORY, SITE_TYPE, URBANIZATION`.
- A check is performed to prevent the aggregation of data from different pollutants with different units of measurement. For example, it would not make sense to calculate the average of data points measured in a mix of µg/m³ and ppb.

**Filtering Rules:**
- The possible values for `provTerr` (province/territory) are either the short codes (`NL, PE, NS, NB, QC, ON, MB, SK, AB, BC, YT, NT, NU`), or the long form (`NEWFOUNDLAND AND LABRADOR, PRINCE EDWARD ISLAND, NOVA SCOTIA, NEW BRUNSWICK, QUEBEC, ONTARIO, MANITOBA, SASKATCHEWAN, ALBERTA, BRITISH COLUMBIA, YUKON, NORTHWEST TERRITORIES, NUNAVUT`).
- The possible values for `month` are either the full names (`JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER`) case-insensitive, or the month numbers (`1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12`), starting at 1 for January.
- The possible values for `daysOfWeek` are either the full names (`SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY`) case-insensitive, or the day of the week numbers (`1, 2, 3, 4, 5, 6, 7`), starting at 1 for Sunday.
- Both site (station) names and city names are treated as case-insensitive partial matches. This means a value of `labrador` will match the city name of `LABRADOR CITY`.
- 
**Notes:**
- The `generateCSV` option will output a CSV file containing a table of all of the data that was used to generate the heat map. The file will be written in the same directory as the heat map and will have the same file name, except it will have a `.csv` file extension instead of a `.png` file extension.
- The `colourLowerBound` and `colourUpperBound` can be used to limit the scale that is mapped to the colour gradient. This is useful for helping to emphasize differences that appear in the centre of the overall range of values, or preventing outliers from shifting the entire scale. When specified, the legend will indicate that either the lower or upper bound by adding `>=` and `<=` to the bottom and top of the scale, respectively. If not specified, then the minimum and maximum values of the colour gradient scale will be calculated automatically. 
- A title will be automatically generated for the report based on the aggregation and filtering rules that you provide.

## NAPSContinuousDataExporter

A Java tool that exports the continuous data, previously loaded by the NAPSContinuousDataLoader, from a PostgreSQL database to one or more CSV files at the directory location specified. The data is in a flat, denormalized, CSV format and is encoded in UTF-8 with a BOM. This format is compatible with all modern versions of Excel. The tool allows you to specify what years, pollutants, and sites you want to export. It also lets you specify if you want the data grouped into a single file by any combination of per year, per pollutant and per site.

You can invoke this tool by running the class `com.dbf.naps.data.exporter.continuous.NAPSContinuousDataExporter`.

**Command line usage:**
```
 -t,   --threadCount <arg> Maximum number of parallel threads.
 -dbh, --dbHost <arg>      Hostname for the PostgreSQL database. Default: localhost
 -dbn, --dbName <arg>      Database name for the PostgreSQL database. Default: naps
 -dbp, --dbPass <arg>      Database password for the PostgreSQL database. Default: password
 -dbt, --dbPort <arg>      Port for the PostgreSQL database. Default: 5432
 -dbu, --dbUser <arg>      Database user name for the PostgreSQL database. Default: postgres
 -fn,  --fileName <arg>    Custom file name without the extension. Will be automatically generated if not defined.
 -fp,  --filePerPollutant  Create a separate file for each pollutant.
 -fs,  --filePerSite       Create a separate file for each site.
 -fy,  --filePerYear       Create a separate file for each year.
 -o,   --overwriteFiles    Replace existing files.
 -p,   --dataPath <arg>    Local path to save the exported data.
 -pn,  --pollutants <arg>  Comma-separated list of pollutant names.
 -sid, --sites <arg>       Comma-separated list of site IDs.
 -v,   --verbose           Make logging more verbose.
 -ye,  --yearEnd <arg>     End year (inclusive).
 -ys,  --yearStart <arg>   Start year (inclusive).
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
 -v,  --verbose              Make logging more verbose.
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
 -v,   --verbose              Make logging more verbose.
```

## NAPSIntegratedDataQuery

This powerful Java tool allows you to dynamically query the NAPS integrated data that was loaded into a PostgreSQL database using the [NAPSIntegratedDataLoader](#napsintegrateddataloader).  It will output a CSV file containing a table of data based on the query rules that you provide. It functions the same as the [NAPSContinuousDataQuery](#napscontinuousdataloader) and accepts all of the same command line arguments, with the exception that the data fields used for grouping cannot include `HOUR`, since hour attribute only applies to continuous data, not integrated data.

You can invoke this tool by running the class `com.dbf.naps.data.analysis.query.integrated.NAPSIntegratedDataQuery`.

**Command line usage:**
```
 -a,   --aggregateFunction <arg>  Data aggregation function (AVG, MIN, MAX, COUNT, SUM, NONE).
 -cn,  --cityName <arg>           City name, partial match.
 -d,   --days <arg>               Comma-separated list of days of the month.
 -dbh, --dbHost <arg>             Hostname for the PostgreSQL database. Default: localhost
 -dbn, --dbName <arg>             Database name for the PostgreSQL database. Default: naps
 -dbp, --dbPass <arg>             Database password for the PostgreSQL database. Default: password
 -dbt, --dbPort <arg>             Port for the PostgreSQL database. Default: 5432
 -dbu, --dbUser <arg>             Database user name for the PostgreSQL database. Default: postgres
 -dow, --daysOfWeek <arg>         Comma-separated list of days of the week, starting at 1 for Sunday.
 -fn,  --fileName <arg>           Custom file name without the extension. Will be automatically generated if not defined.
 -fp,  --filePerPollutant         Create a separate file for each pollutant.
 -fs,  --filePerSite              Create a separate file for each site.
 -fy,  --filePerYear              Create a separate file for each year.
 -g1,  --group1 <arg>             Data field for level 1 grouping.
 -g2,  --group2 <arg>             Data field for optional level 2 grouping.
 -g3,  --group3 <arg>             Data field for optional level 3 grouping
 -g4,  --group4 <arg>             Data field for optional level 4 grouping
 -g5,  --group5 <arg>             Data field for optional level 5 grouping
 -m,   --months <arg>             Comma-separated list of months of the year, starting at 1 for January.
 -o,   --overwriteFiles           Replace existing files.
 -p,   --dataPath <arg>           Local path to save the data.
 -pn,  --pollutants <arg>         Comma-separated list of pollutant names.
 -pt,  --provTerr <arg>           Comma-separated list of 2-digit province & territory codes.
 -rlb, --resultLowerBound <arg>   Lower bound (inclusive) of post-aggregated results to include. Results less than this
                                     threshold will be filtered out of the result set after aggregation.
 -rub, --resultUpperBound <arg>   Upper bound (inclusive) of post-aggregated results to include. Results greater than
                                     this threshold will be filtered out of the result set after aggregation.
 -sc,  --showSampleCount          Include the sample count (number of samples or data points) in the result set.
 -scm, --minSampleCount <arg>     Minimum sample count (number of samples or data points) in order to be included in the
                                     result set.
 -sid, --sites <arg>              Comma-separated list of site IDs.
 -sn,  --siteName <arg>           NAPS site (station) name, partial match.
 -stdDevPop, --showStdDevPop       Include the population standard deviation in the result set.
 -stdDevSmp, --showStdDevSamp      Include the sample standard deviation in the result set.
 -t,   --threadCount <arg>        Maximum number of parallel threads.
 -v,   --verbose                  Make logging more verbose.
 -vlb, --valueLowerBound <arg>    Lower bound (inclusive) of pre-aggregated raw values to include. Values less than this
                                     threshold will be filtered out before aggregation.
 -vub, --valueUpperBound <arg>    Upper bound (inclusive) of pre-aggregated raw values to include. Values greater than
                                     this threshold will be filtered out before aggregation.
 -ye,  --yearEnd <arg>            End year (inclusive).
 -ys,  --yearStart <arg>          Start year (inclusive).
```

Possible values for `group1` through `group5` are `YEAR,MONTH, DAY, DAY_OF_WEEK, DAY_OF_YEAR, WEEK_OF_YEAR, NAPS_ID, POLLUTANT, PROVINCE_TERRITORY, SITE_TYPE, URBANIZATION`. 

All of the same rules and restrictions of the [NAPSContinuousDataQuery](#napscontinuousdataquery) apply.

## NAPSIntegratedHeatMap

A Java tool that generates highly customizable heat map diagrams for the visualization of NAPS integrated data. It functions the same as the [NAPSContinuousHeatMap](#napscontinuousheatmap) and accepts all of the same command line arguments, with the exception that the data fields used for the x and y axes (`group1` and `group2`) cannot include `HOUR`, since hour attribute only applies to continuous data, not integrated data.

You can invoke this tool by running the class `com.dbf.naps.data.analysis.heatmap.integrated.NAPSIntegratedDataHeatMap`.

**Command line usage:**
```
 -a,   --aggregateFunction <arg>  Data aggregation function (AVG, MIN, MAX, COUNT, SUM).
 -cg,  --colourGradient <arg>     Heat map colour gradient choice. Values are 1-8 (inclusive).
 -clb, --colourLowerBound <arg>   Heat map colour lower bound (inclusive).
 -cn,  --cityName <arg>           City name, partial match.
 -csv, --generateCSV              Generate a corresponding CSV file containing the raw data for each heat map.
 -cub, --colourUpperBound <arg>   Heat map colour upper bound (inclusive).
 -d,   --days <arg>               Comma-separated list of days of the month.
 -dbh, --dbHost <arg>             Hostname for the PostgreSQL database. Default: localhost
 -dbn, --dbName <arg>             Database name for the PostgreSQL database. Default: naps
 -dbp, --dbPass <arg>             Database password for the PostgreSQL database. Default: password
 -dbt, --dbPort <arg>             Port for the PostgreSQL database. Default: 5432
 -dbu, --dbUser <arg>             Database user name for the PostgreSQL database. Default: postgres
 -dow, --daysOfWeek <arg>         Comma-separated list of days of the week, starting at 1 for Sunday.
 -fn,  --fileName <arg>           Custom file name without the extension. Will be automatically generated if not defined.
 -fp,  --filePerPollutant         Create a separate file for each pollutant.
 -fs,  --filePerSite              Create a separate file for each site.
 -fy,  --filePerYear              Create a separate file for each year.
 -g1,  --group1 <arg>             Data field for the heat map X-axis.
 -g2,  --group2 <arg>             Data field for the heat map Y-axis.
 -m,   --months <arg>             Comma-separated list of months of the year, starting at 1 for January.
 -o,   --overwriteFiles           Replace existing files.
 -p,   --dataPath <arg>           Local path to save the data.
 -pn,  --pollutants <arg>         Comma-separated list of pollutant names.
 -pt,  --provTerr <arg>           Comma-separated list of 2-digit province & territory codes.
 -rlb, --resultLowerBound <arg>   Lower bound (inclusive) of post-aggregated results to include. Results less than this
                                     threshold will be filtered out of the result set after aggregation.
 -rub, --resultUpperBound <arg>   Upper bound (inclusive) of post-aggregated results to include. Results greater than
                                     this threshold will be filtered out of the result set after aggregation.
 -scm, --minSampleCount <arg>     Minimum sample count (number of samples or data points) in order to be included in the
                                     result set.
 -sid, --sites <arg>              Comma-separated list of site IDs.
 -sn,  --siteName <arg>           NAPS site (station) name, partial match.
 -t,   --threadCount <arg>        Maximum number of parallel threads.
 -v,   --verbose                  Make logging more verbose.
 -vlb, --valueLowerBound <arg>    Lower bound (inclusive) of pre-aggregated raw values to include. Values less than this
                                     threshold will be filtered out before aggregation.
 -vub, --valueUpperBound <arg>    Upper bound (inclusive) of pre-aggregated raw values to include. Values greater than
                                     this threshold will be filtered out before aggregation.
 -ye,  --yearEnd <arg>            End year (inclusive).
 -ys,  --yearStart <arg>          Start year (inclusive).
```

Possible values for `group1` and `group2` are `YEAR,MONTH, DAY, DAY_OF_WEEK, DAY_OF_YEAR, WEEK_OF_YEAR, NAPS_ID, POLLUTANT, PROVINCE_TERRITORY, SITE_TYPE, URBANIZATION`. 

All of the same rules and restrictions of the [NAPSContinuousHeatMap](#napscontinuousheatmap) apply.

## NAPSIntegratedDataExporter

A Java tool that exports the integrated data, previously loaded by the NAPSIntegratedDataLoader, from a PostgreSQL database to one or more CSV files at the directory location specified. The data is in a flat, denormalized, CSV format and is encoded in UTF-8 with a BOM. This format is compatible with all modern versions of Excel. The tool allows you to specify what years, pollutants, and sites you want to export. It also lets you specify if you want the data grouped into a single file by any combination of per year, per pollutant and per site.

You can invoke this tool by running the class `com.dbf.naps.data.exporter.integrated.NAPSIntegratedDataExporter`.

**Command line usage:**
```
 -t,--threadCount <arg>    Maximum number of parallel threads.
 -dbh, --dbHost <arg>      Hostname for the PostgreSQL database. Default: localhost
 -dbn, --dbName <arg>      Database name for the PostgreSQL database. Default: naps
 -dbp, --dbPass <arg>      Database password for the PostgreSQL database. Default: password
 -dbt, --dbPort <arg>      Port for the PostgreSQL database. Default: 5432
 -dbu, --dbUser <arg>      Database user name for the PostgreSQL database. Default: postgres
 -fn,  --fileName <arg>    Custom file name without the extension. Will be automatically generated if not defined.
 -fp,  --filePerPollutant  Create a separate file for each pollutant.
 -fs,  --filePerSite       Create a separate file for each site.
 -fy,  --filePerYear       Create a separate file for each year.
 -o,   --overwriteFiles    Replace existing files.
 -p,   --dataPath <arg>    Local path to save the exported data.
 -pn,  --pollutants <arg>  Comma-separated list of pollutant names.
 -sid, --sites <arg>       Comma-separated list of site IDs.
 -v,   --verbose           Make logging more verbose.
 -ye,  --yearEnd <arg>     End year (inclusive).
 -ys,  --yearStart <arg>   Start year (inclusive).
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

![schema diagram](https://github.com/user-attachments/assets/c9888f32-6f5a-44f2-b3ad-bad6e76d2ef3)


# Known Issues

This repository makes use of GitHub's built-in issue tracker. You can view all open issues [here](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/issues). Most of the issues are problems with the data files that are distributed from the NAPS website.

# Developer Notes

- Requires Java 21 or later.
- Tested with PostgreSQL 16.3. The database should be created with the UTF-8 characterset in order to support accented characters.
- If you want to build the jar from the source code you will need [Apache Maven](https://maven.apache.org/).
- Other than the sample reports, everything in this toolbox should be multi-platform (supporting Windows, Linux, MacOS, etc.) and multi-architecture (supporting x86 and ARM). However, I am only one person and I have only developed and tested the code on Windows 11 x64.
- The NAPS data is parsed from the Excel files downloaded from the website. There are 3 different Excel formats used: XLSX (Excel 2007 and later), XLS (BIFF8, Excel 5 - 1993 and later), and XLS (BIFF4, Excel 4 - 1992). For parsing XLSX, I'm using the [Apache POI](https://poi.apache.org/) library. For parsing XLS BIFF8, I'm using JXL - the [Java Excel API](https://jexcelapi.sourceforge.net/). For parsing XLS BIFF4, I could not find a Java library that supports it, so I built my own parser adapted from the Apache POI library's [OldExcelExtractor](https://github.com/apache/poi/blob/trunk/poi/src/main/java/org/apache/poi/hssf/extractor/OldExcelExtractor.java).
- The rendering of heat maps is entirely written from scratch by myself. I intend to make a few small enhancements to add more customization and flexibility, then I'll spin it off into its own GitHub project so others can benefit from it.

# Legal Stuff

Copyright (c) 2024 David Fortin

This software (NAPS Data Analysis Toolbox) is provided by David Fortin under the MIT License, meaning you are free to use it however you want, as long as you include the original copyright notice (above) and license notice in any copy you make. You just can't hold me liable in case something goes wrong. License details can be read [here](https://github.com/dbeaudoinfortin/NAPSDataAnalysis?tab=MIT-1-ov-file)

The data itself is provided by the [National Air Pollution Surveillance](https://www.canada.ca/en/environment-climate-change/services/air-pollution/monitoring-networks-data/national-air-pollution-program.html) (NAPS) program, which is run by the Analysis and Air Quality Section of Environment and Climate Change Canada. The data is licensed under the terms of the Canadian [Open Government Licence](https://open.canada.ca/en/open-government-licence-canada) and can be freely re-published under those terms.
