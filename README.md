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
- [Pollutants](#pollutants)
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

The NAPS Data Analysis Toolbox provides tools for generating heat map diagrams for both the [continuous](#napscontinuousheatmap) and [integrated](#napsintegratedheatmap) data. These heat maps are highly customizable and can be generated in a single command. They make it much easier to spot trends in the data. For example, here is the entire history carbon monoxide readings for all NAPS sites, for all of Canada, aggregated into a single heat map diagram.

![Avg_CO_By Day of the Year and Year](https://github.com/user-attachments/assets/c252ea64-4493-49aa-8295-33709243a8ce)

From this diagram alone there are some trends that immediately stand out, such as:
- the significant improvement to air quality over the years,
- the higher concentrations of CO in winter months,
- the reduction of CO on weekends (the apparent diagonal lines),
- the reduction of CO on holidays, such as Christmas and New Year's day,
- the larger number of outlier reading in 2016.

We can change the x-axis to get a view of what is happening on the weekends compared to the weekdays:

![Avg_CO_By Day of the Week and Year](https://github.com/user-attachments/assets/25024f6e-e70f-43b2-9926-006a93ec4be6)

(Note that this second heat map uses a different colour palette that better suits the data.)

Focusing on a single year (2002) makes it easy to see the difference between summer and winter months:

![Monoxide_Yearly_2002](https://github.com/user-attachments/assets/9224b0b7-d57a-447f-ab1c-324bd36fd991)

Similarly, we can look at any pollutant, or all pollutants. Here is SO2, for all years, using a third colour palette:

![Avg_SO2_By Day of the Year and Year](https://github.com/user-attachments/assets/721cf42e-cd32-42b7-ab47-9f0538856cea)

And here are two heat maps for lead, showing the average and maximum concentrations, respectively. This makes it easy to see the large reduction in air pollution starting with the ban on leaded gasoline in 1990:

![Avg_Lead_By Month and Year](https://github.com/user-attachments/assets/9660ba50-52bf-4b9b-ba17-7fcde5766e34)

![Max_Lead_By Month and Year](https://github.com/user-attachments/assets/a7b8b1ce-dfcf-4778-b158-fabde5fb27f3)

(The provinces of Ontario & Quebec were only chosen to demonstrate the ability to filter by province/territory.)

We can limit the results to only large and medium urban areas, and focus in around the year 1990 to better see the large effect of the ban on leaded gasoline:

![Integrated_Avg By Week of the Year and Year](https://github.com/user-attachments/assets/d8acdda9-2def-4580-8310-df719ebf4627)

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
 -ct,  --title <arg>              Chart title. Will be automatically generated if not defined.
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
 -st, --siteType <arg>            NAPS site type classification (PE, RB, T, PS).
 -stdDevPop, --showStdDevPop      Include the population standard deviation in the result set.
 -stdDevSmp, --showStdDevSamp     Include the sample standard deviation in the result set.
 -t,   --threadCount <arg>        Maximum number of parallel threads.
 -u,   --urbanization <arg>       NAPS site urbanization classification (LU, MU, SU, NU).
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
- The possible values for `siteType` are `PE, RB, T, PS`, representing `General Population, Regional Background, Transportation, Point Source` (respectively).
- The possible values for `urbanization` are `LU, MU, SU, NU`, representing `Large Urban, Medium Urban, Small Urban, Rural (Non Urban)` (respectively).
- Both site (station) names and city names are treated as case-insensitive partial matches. This means a value of `labrador` will match the city name of `LABRADOR CITY`.

**Other Notes:**
- A title will be automatically generated for the report based on the aggregation and filtering rules that you provide. You can override this title by using the `--title` option. Setting it to empty `""` will omit it entirely.

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
 -cg,  --colourGradient <arg>     Heat map colour gradient choice. Values are 1-9 (inclusive).
 -clb, --colourLowerBound <arg>   Heat map colour lower bound (inclusive).
 -cn,  --cityName <arg>           City name, partial match.
 -csv, --generateCSV              Generate a corresponding CSV file containing the raw data for each heat map.
 -ct,  --title <arg>              Chart title. Will be automatically generated if not defined.
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
 -st,  --siteType <arg>           NAPS site type classification (PE, RB, T, PS).
 -t,   --threadCount <arg>        Maximum number of parallel threads.
 -u,   --urbanization <arg>       NAPS site urbanization classification (LU, MU, SU, NU).
 -v,   --verbose                  Make logging more verbose.
 -vlb, --valueLowerBound <arg>    Lower bound (inclusive) of pre-aggregated raw values to include. Values less than this
                                     threshold will be filtered out before aggregation.
 -vub, --valueUpperBound <arg>    Upper bound (inclusive) of pre-aggregated raw values to include. Values greater than
                                     this threshold will be filtered out before aggregation.
 -ye,  --yearEnd <arg>            End year (inclusive).
 -ys,  --yearStart <arg>          Start year (inclusive).
```

**Colour Palettes:**

9 different colour palettes are currently offered. I will plan to eventually add more in the future. The current palettes are the following:
1. A smooth gradient based on the colour wheel from blue to red. All the of the colours are fully saturated.
2. A 12 step gradient from blue to red with less saturation than the first colour palette.
3. A simplified 5 step gradient from blue to red.
4. A two colour gradient from blue to red, with purple mixed in-between.
5. A 5 step colour blind friendly gradient of greenish-yellow to dark orange.
6. A 3 step black-red-orange gradient, similar to black-body radiation, up to approximately 1300 degrees kelvin.
7. Same as number 6 but two more steps are added to extend the scale up to approximately 6500k degrees kelvin.
8. A 50 step colour gradient based on [Dave Green's ‘cubehelix’ colour scheme](https://people.phy.cam.ac.uk/dag9/CUBEHELIX/)
9. A 2 step grey-scale gradient that should be used for non-colour screen/print-outs.

The default colour palette, if not specified, is number 1. Here are examples of what the colour palette look like, in order:

![Continuous_By Day of the Month and Month_C1](https://github.com/user-attachments/assets/c55ced9a-36af-4006-b47e-40b2ce04bc60)
![Continuous_By Day of the Month and Month_C2](https://github.com/user-attachments/assets/5ca35e2f-d8e8-46b7-b2ef-95e335128480)
![Continuous_By Day of the Month and Month_C3](https://github.com/user-attachments/assets/8485348e-c686-4cfd-81f7-35c6d4a4471e)
![Continuous_By Day of the Month and Month_C4](https://github.com/user-attachments/assets/051332ef-fb9d-465c-b832-e2f2f5a417e1)
![Continuous_By Day of the Month and Month_C5](https://github.com/user-attachments/assets/523a4438-bb37-4236-af18-d5885a4770a7)
![Continuous_By Day of the Month and Month_C6](https://github.com/user-attachments/assets/1085aec3-b82f-42e3-af67-72df9c5af137)
![Continuous_By Day of the Month and Month_C7](https://github.com/user-attachments/assets/fc17e80b-846b-4998-af25-766da3bc09ac)
![Continuous_By Day of the Month and Month_C8](https://github.com/user-attachments/assets/0c10d7ce-09cc-4d3c-be2e-ceb7b12272d1)
![Continuous_By Day of the Month and Month_C9](https://github.com/user-attachments/assets/a9f9100e-dfbe-4a39-8dce-1da1bd0e222f)

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
- The possible values for `siteType` are `PE, RB, T, PS`, representing `General Population, Regional Background, Transportation, Point Source` (respectively).
- The possible values for `urbanization` are `LU, MU, SU, NU`, representing `Large Urban, Medium Urban, Small Urban, Rural (Non Urban)` (respectively).
- Both site (station) names and city names are treated as case-insensitive partial matches. This means a value of `labrador` will match the city name of `LABRADOR CITY`.
- 
**Notes:**
- The `generateCSV` option will output a CSV file containing a table of all of the data that was used to generate the heat map. The file will be written in the same directory as the heat map and will have the same file name, except it will have a `.csv` file extension instead of a `.png` file extension.
- The `colourLowerBound` and `colourUpperBound` can be used to limit the scale that is mapped to the colour gradient. This is useful for helping to emphasize differences that appear in the centre of the overall range of values, or preventing outliers from shifting the entire scale. When specified, the legend will indicate that either the lower or upper bound by adding `>=` and `<=` to the bottom and top of the scale, respectively. If not specified, then the minimum and maximum values of the colour gradient scale will be calculated automatically. 
- A title will be automatically generated for the report based on the aggregation and filtering rules that you provide. You can override this title by using the `--title` option. Setting it to empty `""` will omit it entirely.

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
 -st,  --siteType <arg>           NAPS site type classification (PE, RB, T, PS).
 -stdDevPop, --showStdDevPop      Include the population standard deviation in the result set.
 -stdDevSmp, --showStdDevSamp     Include the sample standard deviation in the result set.
 -t,   --threadCount <arg>        Maximum number of parallel threads.
 -u,   --urbanization <arg>       NAPS site urbanization classification (LU, MU, SU, NU).
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
 -st,  --siteType <arg>           NAPS site type classification (PE, RB, T, PS).
 -t,   --threadCount <arg>        Maximum number of parallel threads.
 -u,   --urbanization <arg>       NAPS site urbanization classification (LU, MU, SU, NU).
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

You can find the latest package [here](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/packages). Alternatively, in the /target directory you can find the pre-compiled jar file naps_data.jar. This is a shaded jar file which means that it contains all of the 3rd party dependencies inside of it. Assuming you have Java 17 installed and 
part of your system path you can simply invoke the class by running the following:
```
 java -cp naps_data.jar com.dbf.naps.data.loader.continuous.NAPSContinuousDataLoader -p C:\temp\NAPSData\RawFiles -t 24
```

In the above example, the data will be loaded from the C:\temp\NAPSData\RawFiles directory into the database using a thread pool size of 24, and all default database connection options (see above for details).

# Pollutants

In order to analyse the data using tools such as the [NAPSContinuousHeatMap](#napscontinuousheatmap) or the [NAPSContinuousDataQuery](#napscontinuousdataquery), you will need to know the correct names of the pollutants. The data from the NAPS portal contains many inconsistencies and I have taken the time to resolve them as I find them. For example, there is a mix of chemical symbols and names (`Pb` vs. `Lead`, `Ti` vs `Titanium`), inconsistent capitalization (`AMMONIUM` vs. `Ammonium`, `a-Pinene` vs. `A-Pinene`), inconsistent spacing (`Chrysene&Triphenylene` vs. `Chrysene & Triphenylene`, `Freon114` vs. `Freon 114`) and the inconsistent naming of PM 2.5 (`PM25` vs. `PM2.5`).

The following table lists all of the compounds (pollutants) and how many data points are present in both the continuous data set and the integrated data set. 

<details>
<summary>Table of Compounds</summary>

### As of October 2024

|Compound|Continuous Data Points|Integrated Data Points|
|:--- | :---: | :---: |
|CO|22,515,492||
|NO|38,711,270||
|NO2|41,663,301||
|NOX|30,910,885||
|O3|57,283,903||
|PM10|9,785,097||
|PM2.5|35,593,881|39120|
|SO2|37,398,018|5354|
|1-Bromopropane||1209|
|1-Butene/Isobutene||76502|
|1-Butyne||54678|
|1-Decene||31484|
|1-Heptene||30900|
|1-Hexene||56593|
|1-Hexene/2-Methyl-1-Pentene||15836|
|1-Me-Pyrene||6532|
|1-Methylcyclohexene||34763|
|1-Methylcyclopentene||34771|
|1-Methylpyrene||3357|
|1-Nonene||31173|
|1-Octene||32108|
|1-Pentene||77107|
|1-Propyne||48033|
|1-Undecene||23655|
|1,1-Dichloroethane||50692|
|1,1-Dichloroethylene||52542|
|1,1,1-Trichloroethane||75177|
|1,1,2-Trichloroethane||69481|
|1,1,2,2-Tetrachloroethane||52405|
|1,2-Dichlorobenzene||52550|
|1,2-Dichloroethane||70470|
|1,2-Dichloropropane||52561|
|1,2-Diethylbenzene||39184|
|1,2,3-Trimethylbenzene||76829|
|1,2,4-Trichlorobenzene||52097|
|1,2,4-Trimethylbenzene||77070|
|1,3-Butadiene||77121|
|1,3-Dichlorobenzene||52576|
|1,3-Diethylbenzene||76850|
|1,3,5-Trimethylbenzene||77072|
|1,4-Dichlorobenzene||77075|
|1,4-Dichlorobutane||37265|
|1,4-Diethylbenzene||75956|
|1234678-H7CDD||3076|
|1234678-H7CDF||3076|
|123478-H6CDD||3076|
|123478-H6CDF||3076|
|1234789-H7CDF||3076|
|123678-H6CDD||3076|
|123678-H6CDF||3076|
|12378-P5CDD||3076|
|12378-P5CDF||3076|
|123789-H6CDD||3076|
|123789-H6CDF||3076|
|13C12-H6CDD||3076|
|13C12-H6CDF||3049|
|13C12-H7CDD||3076|
|13C12-H7CDF||3049|
|13C12-OCDD||3076|
|13C12-P5CDD||3076|
|13C12-P5CDF||3049|
|13C12-TCDD||3076|
|13C12-TCDF||3076|
|13C6-HCB||1609|
|13C6-PCP||1602|
|2-Ethyl-1-Butene||21807|
|2-Ethyltoluene||77108|
|2-Me-Fluorene||6534|
|2-Methyl-1-butene||73846|
|2-Methyl-1-Pentene||5574|
|2-Methyl-2-butene||73822|
|2-methyl-2-Pentene||993|
|2-Methylfluorene||3355|
|2-Methylheptane||77014|
|2-Methylhexane||77075|
|2-Methylpentane||76936|
|2-Pentanone||864|
|2-Pentanone/Isovaleraldehyde||7091|
|2,2-Dimethylbutane||76968|
|2,2-Dimethylhexane||46134|
|2,2-Dimethylpentane||59357|
|2,2-Dimethylpropane||54681|
|2,2,3-Trimethylbutane||35097|
|2,2,4-Trimethylpentane||77106|
|2,2,5-Trimethylhexane||58259|
|2,3-Dimethylbutane||77118|
|2,3-Dimethylpentane||77110|
|2,3,4-Trimethylpentane||77114|
|2,4-Dimethylhexane||77088|
|2,4-Dimethylpentane||77110|
|2,5-Dimethylbenzaldehyde||7237|
|2,5-Dimethylheptane||1304|
|2,5-Dimethylhexane||77078|
|234678-H6CDF||3076|
|23478-P5CDF||3076|
|2378-TCDD||3076|
|2378-TCDF||3076|
|3-Ethyltoluene||77111|
|3-Me-Cholanthrene||6525|
|3-Methyl-1-Butene||37045|
|3-Methyl-1-pentene||54388|
|3-Methylcholanthrene||3357|
|3-Methylheptane||77062|
|3-Methylhexane||77113|
|3-Methyloctane||1281|
|3-Methylpentane||76733|
|3,6-Dimethyloctane||37365|
|4-Ethyltoluene||77109|
|4-Methyl-1-pentene||53818|
|4-Methylheptane||77079|
|4-Methyloctane||1294|
|7-Me-Benz(a)Anthracene||6527|
|7-Methylbenz(a)Anthracene||3357|
|A-Pinene||51592|
|Acenaphthene||9886|
|Acenaphthylene||9877|
|Acetaldehyde||13399|
|Acetate||34557|
|Acetone||13374|
|Acetylene||75905|
|Acrolein||9442|
|Aluminum||111612|
|Ammonia||30530|
|Ammonium||45360|
|Anthanthrene||9886|
|Anthracene||9869|
|Antimony||114301|
|Arabitol||12662|
|Arsenic||34532|
|B-Pinene||51096|
|Barium||140508|
|Benz(a)Anthracene||9888|
|Benzaldehyde||10294|
|Benzene||77064|
|Benzo(a)Fluorene||9889|
|Benzo(a)Pyrene||9861|
|Benzo(b)&(k)Fluoranthene||1981|
|Benzo(b)Chrysene||9891|
|Benzo(b)Fluoranthene||7965|
|Benzo(b)Fluorene||9886|
|Benzo(e)Pyrene||9890|
|Benzo(g,h,i)Fluoranthene||9889|
|Benzo(g,h,i)Perylene||9886|
|Benzo(k)Fluoranthene||7969|
|Benzylchloride||41318|
|Beryllium||29991|
|Bismuth||3138|
|Bromide||40544|
|Bromine||87462|
|Bromodichloromethane||38568|
|Bromoform||70330|
|Bromomethane||68617|
|Bromotrichloromethane||10550|
|Butane||77109|
|Butyraldehyde/Iso-Butyraldehyde||1790|
|Cadmium||119962|
|Calcium||119604|
|Camphene||51214|
|Carbontetrachloride||77085|
|Cerium||36931|
|Cesium||54465|
|Chloride||39433|
|Chlorobenzene||69924|
|Chloroethane||68614|
|Chloroform||76803|
|Chloromethane||75181|
|Chromium||119568|
|Chrysene||7965|
|Chrysene & Triphenylene||2000|
|cis-1,2-Dichloroethylene||36928|
|cis-1,2-Dimethylcyclohexane||64142|
|cis-1,3-Dichloropropene||39194|
|cis-1,3-Dimethylcyclohexane||61191|
|cis-1,4/t-1,3-Dimethylcyclohexane||57679|
|cis-2-Butene||77119|
|cis-2-Heptene||35862|
|cis-2-Hexene||76758|
|cis-2-Octene||9755|
|cis-2-Pentene||77094|
|cis-3-Heptene||8941|
|cis-3-Methyl-2-Pentene||74116|
|cis-4-Methyl-2-pentene||65305|
|Cl-||29562|
|Cobalt||34532|
|Copper||40297|
|Crotonaldehyde||10294|
|Cyclohexane||77119|
|Cyclohexene||34678|
|Cyclopentane||76972|
|Cyclopentene||56770|
|D-Limonene||51242|
|Decane||77086|
|Dibenz(a,c)&(a,h)Anthracene||9819|
|Dibromochloromethane||39061|
|Dibromomethane||39198|
|Dodecane||75811|
|EC||5871|
|EC(A)||12298|
|EC(B)||6305|
|EC_R||11781|
|EC_T||11783|
|EC1||11783|
|EC1(A)||12298|
|EC1(B)||6305|
|EC2||11783|
|EC2(A)||12298|
|EC2(B)||6305|
|EC3||11783|
|EC3(A)||12298|
|EC3(B)||6305|
|EDB||38570|
|Ethane||76167|
|Ethylbenzene||77078|
|Ethylbromide||37371|
|Ethylene||74819|
|Fluoranthene||9891|
|Fluorene||9887|
|Fluoride||42340|
|Formaldehyde||13402|
|Formate||34559|
|Freon 11||76991|
|Freon 113||42474|
|Freon 114||70298|
|Freon 12||64825|
|Freon 22||76867|
|Galactosan||12663|
|Gallium||3138|
|H6CDD||3076|
|H6CDF||3076|
|H7CDD||3076|
|H7CDF||3076|
|HCB||1566|
|Heptane||77020|
|Hexachlorobutadiene||52067|
|Hexanal||10291|
|Hexane||76090|
|Hexylbenzene||35788|
|Indane||76685|
|Indeno(1,2,3-cd)Fluoranthene||5899|
|Indeno(1,2,3-cd)Pyrene||9877|
|Iron||119582|
|iso-Butylbenzene||54583|
|iso-Propylbenzene||77093|
|Isobutane||77086|
|Isopentane||77015|
|Isoprene||76826|
|Isovaleraldehyde||2654|
|Lanthanum||11662|
|Lead||121968|
|Levoglucosan||12662|
|Lithium||42260|
|m-Tolualdehyde||9080|
|m and p-Xylene||77086|
|Magnesium||42385|
|Manganese||126621|
|Mannitol||11798|
|Mannosan||12662|
|MEK||12501|
|MEK/Butyraidehyde||864|
|Methylcyclohexane||77097|
|Methylcyclopentane||77119|
|MIBK||10234|
|Molybdenum||34532|
|MSA||34567|
|MTBE||34606|
|n-Butylbenzene||61104|
|n-Propylbenzene||77108|
|N_NO3||5986|
|Na+||29470|
|Naphthalene||71450|
|NH4+||29562|
|Nickel||120129|
|Nitrate||63095|
|Nitric Acid||17728|
|Nitrite||58221|
|Nitrous Acid||12318|
|NO3||39241|
|Nonane||76954|
|o-Tolualdehyde||7290|
|o-Xylene||77082|
|OC||5871|
|OC(A)||12298|
|OC(B)||12298|
|OC(corr)||12297|
|OC_R||11783|
|OC_RA||5872|
|OC_RB||5871|
|OC_T||11783|
|OC1||11783|
|OC1(A)||12298|
|OC1(B)||12298|
|OC2||11783|
|OC2(A)||12298|
|OC2(B)||12298|
|OC3||11783|
|OC3(A)||12298|
|OC3(B)||12298|
|OC4||11783|
|OC4(A)||12298|
|OC4(B)||12298|
|OCDD||3076|
|OCDF||3075|
|OCS||1614|
|Octane||77112|
|Oxalate||42383|
|p-Cymene||76878|
|p-Tolualdehyde||9080|
|P5CDD||3076|
|P5CDF||3076|
|PCB-105||302|
|PCB-114||302|
|PCB-118||302|
|PCB-123||302|
|PCB-126||302|
|PCB-156||302|
|PCB-157||302|
|PCB-167||302|
|PCB-169||302|
|PCB-189||302|
|PCB-77||302|
|PCB-81||302|
|PCP||1598|
|Pentane||77097|
|Perylene||9877|
|Phenanthrene||9889|
|Phosphate||39032|
|PM2.5-10||14424|
|POC(A)||12298|
|POC(B)||6305|
|POC_R||11783|
|POC_T||11783|
|Potassium||119604|
|Propane||77034|
|Propionaldehyde||13399|
|Propionate||34567|
|Propylene||77061|
|Pyrene||9891|
|Retene||6779|
|Rubidium||82705|
|sec-Butylbenzene||54663|
|Selenium||119549|
|Silicon||85595|
|Silver||34470|
|SO4||39246|
|Sodium||33456|
|Strontium||140551|
|Styrene||74513|
|Sulfur||47350|
|Sulphate||50837|
|Sulphur||38250|
|Sulphur Dioxide||24502|
|T_NO3||5993|
|TC||11783|
|TC(A)||12298|
|TC(B)||6305|
|TC(corr)||12297|
|TCDD||3076|
|TCDF||3076|
|TEQ||3076|
|tert-Butylbenzene||37364|
|Tetrachloroethylene||70497|
|Thallium||29991|
|Tin||114287|
|Titanium||111408|
|Toluene||77097|
|trans-1,2-Dichloroethylene||39208|
|trans-1,2-Dimethylcyclohexane||54626|
|trans-1,3-Dichloropropene||39022|
|trans-1,4-Dimethylcyclohexane||56374|
|trans-2-Butene||77114|
|trans-2-Heptene||36398|
|trans-2-Hexene||76863|
|trans-2-Octene||44719|
|trans-2-Pentene||77090|
|trans-3-Heptene||34355|
|trans-3-Methyl-2-Pentene||74810|
|trans-4-Methyl-2-Pentene||45767|
|Trichloroethylene||76930|
|Triphenylene||7965|
|Tungsten||3138|
|Undecane||77077|
|Uranium||20905|
|Valeraldehyde||10292|
|Vanadium||120111|
|Vinylchloride||70498|
|Zinc||120132|
</details>

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
