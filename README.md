# NAPS Data Analysis Toolbox

<p align="center">
<img src="https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/bc1f2673-05fd-4713-8be7-57119d038358"/>
</p>

## Contents

- [NAPS Data Analysis Toolbox](#naps-data-analysis-toolbox)
- [Overview](#overview)
- [Data Analysis](#data-analysis)
- [Getting Started](#getting-started)
  * [Installing PostgreSQL](#installing-postgresql)
  * [Installing Java](#installing-java)
  * [Downloading the Data](#downloading-the-data)
  * [Loading the Data](#loading-the-data)
  * [Installing Microsoft Power BI](#installing-microsoft-power-bi)
  * [Opening the Reports](#opening-the-reports)
- [NAPS Site Tools](#naps-site-tools)
  * [NAPSSitesDownloader](#napssitesdownloader)
  * [NAPSSitesLoader](#napssitesloader)
- [Continuous Data Tools](#continuous-data-tools)
  * [NAPSContinuousDataDownloader](#napscontinuousdatadownloader)
  * [NAPSContinuousDataLoader](#napscontinuousdataloader)
- [Integrated Data Tools](#integrated-data-tools)
  * [NAPSIntegratedDataDownloader](#napsintegrateddatadownloader)
  * [NAPSIntegratedDataLoader](#napsintegrateddataloader)
- [How To Run Individual Tools](#how-to-run-individual-tools)
- [Known Issues](#known-issues)
- [Notes](#notes)
- [Legal Stuff](#legal-stuff)

# Overview
Canada National Air Pollution Surveillance Program (NAPS) data downloader, extractor, schema importer and visualization. 

This project will eventually contain a collection of tools to assist in the analysis of Canadian air quality data. The data is provided by the National Air Pollution Surveillance (NAPS) program, which is part of Environment and Climate Change Canada. You can view the original data [here](https://data-donnees.az.ec.gc.ca/data/air/monitor/national-air-pollution-surveillance-naps-program/).

All usage is for non-commercial research purposes. I am not affiliated with the Government of Canada.

# Data Analysis

(screenshots - coming soon)

In the /reports directory you will find several sample Microsoft Power BI reports for the purpose of visualizing the NAPS data. These reports are designed to be used in conjunction with the database schema built and populated by the tools in this tool box. For information on how to set-up your database check out the [Getting Started](#getting-started) section below.

(description of each report - coming soon)

I plan to eventually add sample reports for other BI/Data Visualization software that are open source, free, and available on more platforms than just Windows x86-64.

# Getting Started

The following steps will guide you in building a database from scratch and populating it with NAPS data.

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
java -cp naps_data.jar com.dbf.naps.data.download.sites.NAPSContinuousDataDownloader -p C:\temp\NAPSData\RawFiles -t 10
```

For more information about the possible command line arguments, see the NAPSContinuousDataDownloader section [below](#napscontinuousdatadownloader).

**NAPSIntegratedDataDownloader**

This tool will download all of the NAPS integrated air quality data from the NAPS website and save it to disk in the specified directory. This is optional and only needs to be run if you want to analyze the integrated air quality data. A sub-directory named `IntegratedData` will be automatically created. You can run the tool using the following command line command, on Windows, in the directory of the naps_data.jar: 

```
java -cp naps_data.jar com.dbf.naps.data.download.sites.NAPSIntegratedDataDownloader -p C:\temp\NAPSData\RawFiles -t 10
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

## Installing Microsoft Power BI

The desktop version of Microsoft Power BI is a free tool for exploring and visualizing data. You can find it [here](https://go.microsoft.com/fwlink/?LinkId=2240819). Unfortunately, it only supports Windows x86-64 systems. I do plan to eventually make sample reports for other BI/Data Visualization software.

## Opening the Reports

(this section coming soon)

- Connecting to the running database.
- Loading the BI reports from the /reports directory

# NAPS Site Tools

The following tools are used for downloading a list of NAPS sites and loading the site definitions into a database.

## NAPSSitesDownloader

A Java tool that downloads a single file containing all of the sites (sampling stations) for the NAPS program. This is the simplest tool in the toolbox and is only included for sake of completeness. The file is downloaded from [here](https://data-donnees.az.ec.gc.ca/api/file?path=/air%2Fmonitor%2Fnational-air-pollution-surveillance-naps-program%2FProgramInformation-InformationProgramme%2FStationsNAPS-StationsSNPA.csv) to the specified directory.

You can invoke this tool by running the class com.dbf.naps.data.download.sites.NAPSSitesDownloader. Note that the threadCount argument is meaningless since there is only one file to download.

**Command line usage:**
```
 -o,  --overwriteFiles       Replace the existing file.
 -p,  --downloadPath <arg>   Local path for downloaded files.
 -t,  --threadCount <arg>    Maximum number of parallel threads.
```

## NAPSSitesLoader

A Java tool that loads all of the sites (sampling stations) for the NAPS program (downloaded by the NAPSContinuousDataDownloader) from the provided directory into a PostgreSQL database, as specified. This tool looks for a single file named "sites.csv" in the provided directory. The database schema is automatically created when the tool runs. Once all the data is loaded, there should be 785 rows of data (as of May 2024) in the sites table of your database. 

You can invoke this tool by running the class com.dbf.naps.data.loader.sites.NAPSSitesLoader. Note that the threadCount argument is meaningless since there is only one file to process.

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

The following tools are used for downloading continuous air quality data loading the data into a database. The continuous data represents instantaneous air quality measurements collected on a continuous bases and reported hourly. 

## NAPSContinuousDataDownloader

A Java tool that will download all of the hourly continuous data for the provided years into the provided directory. All file names are unique and all files are downloaded into a single directory. Files are downloaded from [here](https://data-donnees.az.ec.gc.ca/data/air/monitor/national-air-pollution-surveillance-naps-program/Data-Donnees/).

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

# Integrated Data Tools

The following tools are used for downloading integrated air quality data and loading the data into a database.  The integrated data represents air quality measurements that are sampled over a longer duration (typically 24 hours) and collected on a regular basis (every few days).

## NAPSIntegratedDataDownloader

A Java tool that will download all of the integrated data for the provided years into the provided directory. Since many of the file names of the files conflict, each year will be downloaded into its own sub-directory. Files are downloaded from [here](https://data-donnees.az.ec.gc.ca/data/air/monitor/national-air-pollution-surveillance-naps-program/Data-Donnees/). 

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

A Java tool that loads all of the raw integrated data (downloaded by the NAPSIntegratedDataDownloader) from the provided directory into a PostgreSQL database, as specified. The database schema is automatically created when the tool runs. This tool automatically cleans-up and fixes data inconsistencies as it finds them. Once all the data is loaded, there should be about 65 million rows of data (as of May 2024) in the integrated_data table of your database.

You can invoke this tool by running the class com.dbf.naps.data.loader.integrated.NAPSIntegratedDataLoader.

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

# How To Run Individual Tools

You can find the latest package [here](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/packages/2159892). Alternatively, in the /target directory you can find the pre-compiled jar file naps_data.jar. This is a shaded jar file which means that it contains all of the 3rd party dependencies inside of it. Assuming you have Java 17 installed and 
part of your system path you can simply invoke the class by running the following:
```
 java -cp naps_data.jar com.dbf.naps.data.loader.continuous.NAPSContinuousDataLoader -p C:\temp\NAPSData\RawFiles -t 24
```

In the above example, the data will be loaded from the C:\temp\NAPSData\RawFiles directory into the database using a thread pool size of 24, and all default database connection options (see above for details).

# Known Issues

This repository makes use of GitHub's built-in issue tracker. You can view all open issues [here](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/issues). Most of the issues are problems with the data files that are distributed from the NAPS website.

# Notes

- Requires Java 17
- Tested with PostgreSQL 16.3. The database should be created with the UTF-8 characterset in order to support accented characters.
- If you want to build the jar from the source code you will need [Apache Maven](https://maven.apache.org/).
- Other than the sample reports, everything in this toolbox should be multi-platform (supporting Windows, Linux, MacOS, etc.) and multi-architecture (supporting x86 and ARM). However, I am only one person and I have only developed and tested the code on Windows 11 x64.

# Legal Stuff

Copyright (c) 2024 David Fortin

This software (NAPS Data Analysis Toolbox) is provided by David Fortin under the MIT License, meaning you are free to use it however you want, as long as you include the original copyright notice (above) and license notice in any copy you make. You just can't hold me liable in case something goes wrong. License details can be read [here](https://github.com/dbeaudoinfortin/NAPSDataAnalysis?tab=MIT-1-ov-file)

The data itself is provided by the [National Air Pollution Surveillance](https://www.canada.ca/en/environment-climate-change/services/air-pollution/monitoring-networks-data/national-air-pollution-program.html) (NAPS) program, which is run by the Analysis and Air Quality Section of Environment and Climate Change Canada. The data is licensed under the terms of the Canadian [Open Government Licence](https://open.canada.ca/en/open-government-licence-canada) and can be freely re-published under those terms.
