# NAPS Data Analysis Toolbox

<p align="center">
<img src="https://github.com/dbeaudoinfortin/NAPSDataAnalysis/assets/15943629/bc1f2673-05fd-4713-8be7-57119d038358"/>
</p>
<p align="center"><b>Download NAPS air quality data <a href="https://dbeaudoinfortin.github.io/NAPSDataAnalysis/">here</a></b></p>

## Contents

- [Overview](#overview)
- [Data Download Web Page](#data-download-web-page)
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

I started this project because, despite the wealth of data that NAPS provides, analysing it is challenging, time consuming and error prone. The data from the [NAPS portal](https://data-donnees.az.ec.gc.ca/data/air/monitor/national-air-pollution-surveillance-naps-program/) is spread out in hundreds of XLS/XLSX/CSV files, with dozens of formats, different units of measure, different naming conventions, etc. With this toolbox, anyone can use the [downloader tools](#napscontinuousdatadownloader) to download all of the data they need in one command. I then provide the [tools](#napscontinuousdataloader) needed to parse all this data, clean it up and import it into a single simple, clean database schema. After that, you can analyse the data using whatever tool works best for you. I provide a powerful [dynamic query](#napscontinuousdataquery) tool, a CSV [exporter tool](#napscontinuousdataexporter), a [heat map visualization](#data-analysis) [tool](#napscontinuousheatmap) to generate pretty graphs, and a couple example [BI dashboards](#dashboards) to get you started with BI tools. And if all of that is too complicated, you might still be interested in either the [data download web page](https://dbeaudoinfortin.github.io/NAPSDataAnalysis/) or the [clean data exports](#clean-data-exports) that republish the NAPS data in a consistent format.

All usage is for non-commercial research purposes. I am not affiliated with the Government of Canada.

# Data Download Web Page

If you are simply looking to download NAPS air quality data, I have created a [simple web page](https://dbeaudoinfortin.github.io/NAPSDataAnalysis/) that makes it quick and easy to download CSV files. This web page is hosted on GitHub Pages and delivers static content from the [/docs](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/tree/main/docs) directory of this project.


# Clean Data Exports

**Last Updated March 2025**

The NAPS data is messy; the data files contain many inconsistencies in structure, formatting, labelling, etc. In order to load all this data into a clean database, I needed to implement many clean-up rules and handle many exceptional cases. I believe this work could be of benefit to others.   

If you are curious about the data issues I have encountered, I have started keeping track of some of the non-trivial issues [here](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/issues?q=is%3Aissue+label%3A%22Data+Issue%22).

In the [/exports](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/tree/main/exports) directory you will find many CSV files that re-publish the same NAPS data but cleaned-up and grouped. These files were generated using the [NAPSContinuousDataExporter](#napscontinuousdataexporter) and [NAPSIntegratedDataExporter](#napsintegrateddataexporter).

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

**Even More Granular Data**

If you are looking for even more granular data, have a look at the [/docs/data](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/tree/main/docs/data) directory which contains nearly 300,000 CSV files. I have created a [simple web page](https://dbeaudoinfortin.github.io/NAPSDataAnalysis/) with simple dropdowns that make it quick and easy to download these CSV files.

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
The table output will look something like this:
<p align="center">
 <img src="https://github.com/user-attachments/assets/b5d1d43a-8c7c-4424-aee9-596111532065" height="600" />
</p>

For more details on how to run these query tools, see the [continuous](#napscontinuousdataquery) and [integrated](#napsintegrateddataquery) data query sections below.


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

<p align="center">
 <img src="https://github.com/user-attachments/assets/25024f6e-e70f-43b2-9926-006a93ec4be6" height="600" />
</p>

(Note that this second heat map uses a different colour palette that better suits the data.)

Focusing on a single year (2002) makes it easy to see the difference between summer and winter months:

<p align="center">
 <img src="https://github.com/user-attachments/assets/9224b0b7-d57a-447f-ab1c-324bd36fd991" width="400" />
</p>

Similarly, we can look at any pollutant, or all pollutants. Here is SO2, for all years, using a third colour palette:

![Avg_SO2_By Day of the Year and Year](https://github.com/user-attachments/assets/721cf42e-cd32-42b7-ab47-9f0538856cea)

And here are two heat maps for lead, showing the average and maximum concentrations, respectively. This makes it easy to see the large reduction in air pollution starting with the ban on leaded gasoline in 1990:

<p align="center">
 <img src="https://github.com/user-attachments/assets/9660ba50-52bf-4b9b-ba17-7fcde5766e34" width="400" />
 <img src="https://github.com/user-attachments/assets/a7b8b1ce-dfcf-4778-b158-fabde5fb27f3" width="400" />
</p>

(The provinces of Ontario & Quebec were only chosen to demonstrate the ability to filter by province/territory.)

We can limit the results to only large and medium urban areas, and focus in around the year 1990 to better see the large effect of the ban on leaded gasoline:

![Integrated_Avg By Week of the Year and Year](https://github.com/user-attachments/assets/d8acdda9-2def-4580-8310-df719ebf4627)

Here is a breakdown by province/territory of PM2.5 concentrations. The upper and lower bounds of the colour scales have been fixed to be consistent between all 13 graphs. The seasonal trends by province become apparent when averaging the data over many years.

<p align="center">
 <img src="https://github.com/user-attachments/assets/4cd0131e-955a-4943-bb16-f861090f64c6" width="400" />
 <img src="https://github.com/user-attachments/assets/2effe67d-7dd0-4336-b1f1-0f025076e115" width="400" />
 <img src="https://github.com/user-attachments/assets/33d54e35-bea0-4c18-b2fa-09732a52452b" width="400" />
 <img src="https://github.com/user-attachments/assets/8b2d4df2-9f58-467c-83ae-b2eea672859a" width="400" />
 <img src="https://github.com/user-attachments/assets/11b91e0f-8633-48d7-990c-ab2cf6297a1a" width="400" />
 <img src="https://github.com/user-attachments/assets/918b3147-e795-4014-b1d2-86cf703599b5" width="400" />
 <img src="https://github.com/user-attachments/assets/36725f0d-1295-4acd-a238-8aa510a63f90" width="400" />
 <img src="https://github.com/user-attachments/assets/492b226e-3a79-44e4-9354-e1cd3b282b0f" width="400" />
 <img src="https://github.com/user-attachments/assets/fc3b572a-0049-41cd-8549-e0b507ca8bc8" width="400" />
 <img src="https://github.com/user-attachments/assets/9850b8c2-9904-4ac5-ae65-81284f8e573e" width="400" />
 <img src="https://github.com/user-attachments/assets/f759dc62-0e62-492a-a671-d55092ac1157" width="400" />
 <img src="https://github.com/user-attachments/assets/5430a222-5192-4fa0-b890-6ff99f97214d" width="400" />
 <img src="https://github.com/user-attachments/assets/4c3a4def-4755-4d05-8bd0-586cc7e5b8b7" width="400" />
</p>

The seasonal differences between Ontario and Quebec become more apparent when looking at just the large and medium urban zones. Here is a comparison of both the average and maximum PM2.5 concentrations:

<p align="center">
 <img src="https://github.com/user-attachments/assets/22d829c6-05c3-4059-b4a1-befeede455e7" width="400" />
 <img src="https://github.com/user-attachments/assets/e0c27530-fa35-4e24-b780-e0e4c148cbb8" width="400" />
 <img src="https://github.com/user-attachments/assets/a4b07961-e5a8-4f87-a77a-a03e956f40b2" width="400" />
 <img src="https://github.com/user-attachments/assets/14f63809-3ea6-42d7-bcc5-7e79a3ee40d1" width="400" />
</p>

Here are two different colour gradients showing the average concentration (ppb) of O3 for all NAPS sites, spanning all available years. This shows a drastic shift of peak ozone concentrations by 3 months from the end of June to the end of March.

<p align="center">
 <img src="https://github.com/user-attachments/assets/a9d9ec31-6ca6-4d40-a598-b8ff7df48927" width="400" />
 <img src="https://github.com/user-attachments/assets/425e73c5-eeac-4927-a21b-7614bbc3a9ad" width="400" />
</p>

We can further separate the data out between rural and urban sites. Rural data is not available prior to 1980. There is a significant difference between rural and urban sites in the way peak ozone concentrations have shifted over the years, with rural areas seeing a more rapid shift in 1980s. Overall, rural areas have shifted nearly 4 weeks more than urban areas.

<p align="center">
 <img src="https://github.com/user-attachments/assets/7e2e4af8-2577-45a3-b049-5a84fb400314" width="400" />
 <img src="https://github.com/user-attachments/assets/8264db3d-e469-49df-afa8-0dee88eaef55" width="400" />
</p>

To reduce the impact of different sampling sites over the years, the following shows just rural Ontario locations. The colour scale has been adjusted to fit this data compared to the previous graphs.
<p align="center">
 <img src="https://github.com/user-attachments/assets/3a66bd45-29d0-497a-844d-ce3c21486f89" width="400" />
</p>

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

A Java tool that loads all of the sites (sampling stations) for the NAPS program (downloaded by the NAPSContinuousDataDownloader) from the provided directory into a PostgreSQL database, as specified. This tool looks for a single file named "sites.csv" in the provided directory. The database schema is automatically created when the tool runs. Once all the data is loaded, there should be 793 rows of data (as of March 2025) in the sites table of your database. 

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

A Java tool that loads all of the raw continuous data, previously downloaded by the NAPSContinuousDataDownloader, from the provided directory into a PostgreSQL database, as specified. The database schema is automatically created when the tool runs. This tool automatically cleans-up and fixes data inconsistencies as it finds them. Once all the data is loaded, there should be about 285 million rows of data (as of March 2025) in the continuous_data table of your database.

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
 -a,   --aggregateFunction <arg>  Data aggregation function (AVG, MIN, MAX, COUNT, SUM, P50, P95, P98, P99, NONE).
 -aqhi,--aqhi                     Calculate the AQHI value from its component pollutants.
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
 -mtd, --methods <arg>            Comma-separated list of analytical method names.
                                     (170, 181, 184, 195, 236, 636, 703, 706, 731, 760, N/A)
 -o,   --overwriteFiles           Replace existing files.
 -ot,  --outputTypes <arg>        Comma-separated list of file output types (CSV, JSON, JSON_SLIM). Defaults to CSV if unspecified.
 -p,   --dataPath <arg>           Local path to save the data.
 -pn,  --pollutants <arg>         Comma-separated list of pollutant names.
 -pt,  --provTerr <arg>           Comma-separated list of 2-digit province & territory codes.
 -rlb, --resultLowerBound <arg>   Lower bound (inclusive) of post-aggregated results to include. Results less than this
                                     threshold will be filtered out of the result set after aggregation.
 -rt,  --reportTypes <arg>        Comma-separated list of report types. This represents the origin of the data.
                                     (CO, NO, NO2, NOX, O3, PM10, PM2.5, SO2)
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

**Aggregation Rules:**
- The possible values for the aggregation function are `AVG, MIN, MAX, COUNT, SUM, P50, P95, P98, P99, NONE`.
- The default aggregation function, if not specified, is `AVG`.
- P95, P98, and P99 represent the 95<sup>th</sup>, 98<sup>th</sup>, and 99<sup>th</sup> percentiles of concentration values, respectively.
- P50 represents the 50<sup>th</sup> percentile of concentration values, which is equivalent to the median for non-discrete data.
- The possible values for `group1` through `group5` are `YEAR, MONTH, DAY, HOUR, DAY_OF_WEEK, DAY_OF_YEAR, WEEK_OF_YEAR, NAPS_ID, POLLUTANT, PROVINCE_TERRITORY, SITE_TYPE, URBANIZATION`.
- The use of an aggregation function does not require the use of grouping (options `group1` through `group5`). This will effectively aggregate all of the data points into a single value. Use the option `--showSampleCount` to include the number of data points that were aggregated.
- The aggregation function cannot be set to `NONE` when specifying grouping using the options `group1` through `group5`. It is possible to set the aggregation function to `NONE` if no groups are specified, but this has limited usefulness since it will produce a table with a single column containing only the raw values (sample data points).
- The minimum sample count option cannot be used when the aggregate function is set to `NONE` since the sample count will always be 1.
- Post-aggregated bounds (both upper and lower) cannot be used when the aggregate function is set to `NONE`.
- Both the population standard deviation and the sample standard deviation cannot be used when the aggregate function is set to `NONE`.
- A check is performed to prevent the aggregation of data from different pollutants with different units of measurement. For example, it would not make sense to calculate the average of data points measured in a mix of µg/m³ and ppb.

**AQHI Rules:**
- Setting the option `aqhi` will return AQHI values calculated from its component pollutants (O3, NO2, PM2.5), rather than the usual concentration value. This value can be used with all of the possible aggregation functions listed above.
- AQHI values are calculated using the standard formula:
![aqhi_formula](https://github.com/user-attachments/assets/8be19495-3dcd-4ea2-a506-af8fd215efb8)
- AQHI values are calculated for each hour and require all three component pollutants (O3, NO2, PM2.5) to be present. This differs from the typical approach used for real-time reporting, which uses a rolling 3 hour average and only requires 2 of the 3 component pollutents to be present.
- AQHI values are only supported for the continuous data set.
- It is not possible for the results broken out into a single file per pollutant when calculating the AQHI values.
- It is not possible to filter by pollutant when calculating the AQHI values because the AQHI is only based on the O3, NO2, and PM2.5 pollutants.
- It is not possible to filter by method or filter by report type when calculating the AQHI values. The AQHI is based on the standard methods for the O3, NO2, and PM2.5 pollutants.

**Filtering Rules:**
- The possible values for `provTerr` (province/territory) are either the short codes (`NL, PE, NS, NB, QC, ON, MB, SK, AB, BC, YT, NT, NU`), or the long form (`NEWFOUNDLAND AND LABRADOR, PRINCE EDWARD ISLAND, NOVA SCOTIA, NEW BRUNSWICK, QUEBEC, ONTARIO, MANITOBA, SASKATCHEWAN, ALBERTA, BRITISH COLUMBIA, YUKON, NORTHWEST TERRITORIES, NUNAVUT`).
- The possible values for `month` are either the full names (`JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER`) case-insensitive, or the month numbers (`1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12`), starting at 1 for January.
- The possible values for `daysOfWeek` are either the full names (`SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY`) case-insensitive, or the day of the week numbers (`1, 2, 3, 4, 5, 6, 7`), starting at 1 for Sunday.
- The possible values for `siteType` are `PE, RB, T, PS`, representing `General Population, Regional Background, Transportation, Point Source` (respectively).
- The possible values for `urbanization` are `LU, MU, SU, NU`, representing `Large Urban, Medium Urban, Small Urban, Rural (Non Urban)` (respectively).
- Both site (station) names and city names are treated as case-insensitive partial matches. This means a value of `labrador` will match the city name of `LABRADOR CITY`.
- See the [section below](#pollutants) for a list of all supported pollutants.
- The possible values for `methods` are `170, 181, 184, 195, 236, 636, 703, 706, 731, 760`. These represent the main analytical methods used for analysis and only apply to the PM2.5 pollutant. All other pollutants are have a value of `N/A`.
- The possible values for `reportTypes` are `CO, NO, NO2, NOX, O3, PM10, PM2.5, SO2`, corresponding directly to the pollutant names. These represent the type of report from which the data was originally sourced.

**Other Notes:**
- A title will be automatically generated for the report based on the aggregation and filtering rules that you provide. You can override this title by using the `--title` option. Setting it to empty `""` will omit it entirely.
- The `outputTypes` option allows you to specify the file format for the ouput data. `CSV` will output a CSV file containing a table of data, with column headers. `JSON` will output the same data but in a JSON format, with metadata for grouping. `JSON_SLIM` will output just the data itself, in JSON format, with all metadata descriptors removed. All 3 output types, or any combination of output types, can be used at the same time. 
`JSON` output example:
```json
{
  "title": "Maximum AQHI for NAPS Site 60104, Spanning January of the Year 2023, Grouped by Year, Month, and Hour",
  "data": {
    "values": {
      "2023": {
        "values": {
          "1": {
            "values": {
              "1": {
                "value": 3.779493123673372745900,
                "sampleCount": 31,
                "name": "MAX(VALUES)"
              }
            },
            "name": "HOUR"
          }
        },
        "name": "MONTH"
      }
    },
    "name": "YEAR"
  }
}
```
`JSON_SLIM` output example:
```json
{"2023":{"1":{"1":3.779}}}
```

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
 -a,    --aggregateFunction <arg> Data aggregation function (AVG, MIN, MAX, COUNT, SUM, P50, P95, P98, P99).
 -aqhi, --aqhi                    Calculate the AQHI value from its component pollutants.
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
 -fts, --fontScale                Relative font size. Must be greater than 0 and no more than 10. Default is 1.
 -fy,  --filePerYear              Create a separate file for each year.
 -g1,  --group1 <arg>             Data field for the heat map X-axis.
 -g2,  --group2 <arg>             Data field for the heat map Y-axis.
 -gl,  --gridLines                Include grid lines on the heat map.
 -gv,  --gridValues               Include grid values on the heat map.
 -json,--generateJSON             Generate a corresponding JSON file containing the raw data for each heat map.
 -ls,  --legendDecimals <arg>     Number of decimal digits to use for the legend, 0 to 20 (inclusive). Default is 4.
 -m,   --months <arg>             Comma-separated list of months of the year, starting at 1 for January.
 -mtd, --methods <arg>            Comma-separated list of analytical method names.
                                     (170, 181, 184, 195, 236, 636, 703, 706, 731, 760, N/A)
 -o,   --overwriteFiles           Replace existing files.
 -p,   --dataPath <arg>           Local path to save the data.
 -pn,  --pollutants <arg>         Comma-separated list of pollutant names.
 -pt,  --provTerr <arg>           Comma-separated list of 2-digit province & territory codes.
 -rlb, --resultLowerBound <arg>   Lower bound (inclusive) of post-aggregated results to include. Results less than this
                                     threshold will be filtered out of the result set after aggregation.
 -rt,  --reportTypes <arg>        Comma-separated list of report types. This represents the origin of the data.
                                     (CO, NO, NO2, NOX, O3, PM10, PM2.5, SO2)
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

9 different colour palettes are currently offered. I plan to eventually add more in the future. The current palettes are the following:
1. A smooth gradient based on the colour wheel from blue to red. All of the colours are fully saturated.
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
- The possible values for the aggregation function are `AVG, MIN, MAX, COUNT, SUM, P50, P95, P98, P99`.
- The use of an aggregation function is mandatory to generate the heat map.
- The default aggregation function, if not specified, is `AVG`.
- P95, P98, and P99 represent the 95<sup>th</sup>, 98<sup>th</sup>, and 99<sup>th</sup> percentiles of concentration values, respectively.
- P50 represents the 50<sup>th</sup> percentile of concentration values, which is equivalent to the median for non-discrete data.
- Both the `group1` and `group2` options are mandatory since they represent the x-axis and y-axis of the chart, respectively. 
- The possible values for `group1` and `group2` are `YEAR, MONTH, DAY, HOUR, DAY_OF_WEEK, DAY_OF_YEAR, WEEK_OF_YEAR, NAPS_ID, POLLUTANT, PROVINCE_TERRITORY, SITE_TYPE, URBANIZATION`.
- A check is performed to prevent the aggregation of data from different pollutants with different units of measurement. For example, it would not make sense to calculate the average of data points measured in a mix of µg/m³ and ppb.

**AQHI Rules:**
- Setting the option `aqhi` will return AQHI values calculated from its component pollutants (O3, NO2, PM2.5), rather than the usual concentration value. This value can be used with all of the possible aggregation functions listed above.
- AQHI values are calculated using the standard formula:
![aqhi_formula](https://github.com/user-attachments/assets/8be19495-3dcd-4ea2-a506-af8fd215efb8)
- AQHI values are calculated for each hour and require all three component pollutants (O3, NO2, PM2.5) to be present. This differs from the typical approach used for real-time reporting, which uses a rolling 3 hour average and only requires 2 of the 3 component pollutents to be present.
- AQHI values are only supported for the continuous data set.
- It is not possible for the results broken out into a single file per pollutant when calculating the AQHI values.
- It is not possible to filter by pollutant when calculating the AQHI values because the AQHI is only based on the O3, NO2, and PM2.5 pollutants.
- It is not possible to filter by method or filter by report type when calculating the AQHI values. The AQHI is based on the standard methods for the O3, NO2, and PM2.5 pollutants.

**Filtering Rules:**
- The possible values for `provTerr` (province/territory) are either the short codes (`NL, PE, NS, NB, QC, ON, MB, SK, AB, BC, YT, NT, NU`), or the long form (`NEWFOUNDLAND AND LABRADOR, PRINCE EDWARD ISLAND, NOVA SCOTIA, NEW BRUNSWICK, QUEBEC, ONTARIO, MANITOBA, SASKATCHEWAN, ALBERTA, BRITISH COLUMBIA, YUKON, NORTHWEST TERRITORIES, NUNAVUT`).
- The possible values for `month` are either the full names (`JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER`) case-insensitive, or the month numbers (`1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12`), starting at 1 for January.
- The possible values for `daysOfWeek` are either the full names (`SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY`) case-insensitive, or the day of the week numbers (`1, 2, 3, 4, 5, 6, 7`), starting at 1 for Sunday.
- The possible values for `siteType` are `PE, RB, T, PS`, representing `General Population, Regional Background, Transportation, Point Source` (respectively).
- The possible values for `urbanization` are `LU, MU, SU, NU`, representing `Large Urban, Medium Urban, Small Urban, Rural (Non Urban)` (respectively).
- Both site (station) names and city names are treated as case-insensitive partial matches. This means a value of `labrador` will match the city name of `LABRADOR CITY`.
- See the [section below](#pollutants) for a list of all supported pollutants.
- The possible values for `methods` are `170, 181, 184, 195, 236, 636, 703, 706, 731, 760`. These represent the main analytical methods used for analysis and only apply to the PM2.5 pollutant. All other pollutants are have a value of `N/A`.
- The possible values for `reportTypes` are `CO, NO, NO2, NOX, O3, PM10, PM2.5, SO2`, corresponding directly to the pollutant names. These represent the type of report from which the data was originally sourced.

**Rendering Options:**
- The `colourLowerBound` and `colourUpperBound` can be used to limit the scale that is mapped to the colour gradient. This is useful for helping to emphasize differences that appear in the centre of the overall range of values, or preventing outliers from shifting the entire scale. When specified, the legend will indicate that either the lower or upper bound by adding `>=` and `<=` to the bottom and top of the scale, respectively. If not specified, then the minimum and maximum values of the colour gradient scale will be calculated automatically.
- The `fontScale` option is used to grow or shink the font size that is used to render text on the heat maps. This will scale all text (legend, labels, titles, etc.) the same amount. The value, must be greater than 0 and no more than 10. The default value is 1, which means no scaling is performed.
- The `legendDecimals` option can be used to control the number of decimal digits diplayed used for the legend labels. This is useful when the value of each colour step is interpolated. The value can range between 0 to 20 (inclusive), and the default value is 4.
- The `gridLines` option can be used to render a thin black line between all of the cells of the heat map. By default, the grid lines are not rendered.
- The `gridValues` option can be used to display the value of each heat map cell within the cell itself. The size of the text is scaled automatically based on the `fontScale` option.  By default, the grid values are not rendered.
 
**Notes:**
- The `generateCSV` option will output a CSV file containing a table of all of the data that was used to generate the heat map. The file will be written in the same directory as the heat map and will have the same file name, except it will have a `.csv` file extension instead of a `.png` file extension.
- Similarly, the `generateJSON` option will output a JSON file containing a table of all of the data that was used to generate the heat map. The file will be written in the same directory as the heat map and will have the same file name, except it will have a `.json` file extension instead of a `.png` file extension. Both the `generateCSV` option and the `generateJSON` option can be used at the same time.
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

A Java tool that loads all of the raw integrated data, previously downloaded by the NAPSIntegratedDataDownloader, from the provided directory into a PostgreSQL database, as specified. The database schema is automatically created when the tool runs. This tool automatically cleans-up and fixes data inconsistencies as it finds them. Once all the data is loaded, there should be about 15 million rows of data (as of March 2025) in the integrated_data table of your database.

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
 -a,   --aggregateFunction <arg>  Data aggregation function (AVG, MIN, MAX, COUNT, SUM, P50, P95, P98, P99, NONE).
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
 -mtd, --methods <arg>            Comma-separated list of analytical method names.
                                     (ED-XRF, GC-FID, GC-MS, GC-MS TP+G, HPLC, IC, IC-PAD, ICPMS, Microbalance, TOR, WICPMS)
 -o,   --overwriteFiles           Replace existing files.
 -ot,  --outputTypes <arg>        Comma-separated list of file output types (CSV, JSON, JSON_SLIM). Defaults to CSV if unspecified.
 -p,   --dataPath <arg>           Local path to save the data.
 -pn,  --pollutants <arg>         Comma-separated list of pollutant names.
 -pt,  --provTerr <arg>           Comma-separated list of 2-digit province & territory codes.
 -rlb, --resultLowerBound <arg>   Lower bound (inclusive) of post-aggregated results to include. Results less than this
                                     threshold will be filtered out of the result set after aggregation.
 -rt,  --reportTypes <arg>        Comma-separated list of report types. This represents the origin of the data.
                                     (CARB, CARBONYLS, DICHOT, HCB, IC, ICPMS, LEV, NA, NH4, PAH, PCB, PCDD, PM10, PM2.5, PM2.5-10, SPEC, VOC, VOC_4HR, WICPMS)
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

**Notes:**
- Possible values for `group1` through `group5` are `YEAR,MONTH, DAY, DAY_OF_WEEK, DAY_OF_YEAR, WEEK_OF_YEAR, NAPS_ID, POLLUTANT, PROVINCE_TERRITORY, SITE_TYPE, URBANIZATION`.
- AQHI values are not supported for the integrated data set.
- The possible values for `methods` are `ED-XRF, GC-FID, GC-MS, GC-MS TP+G, HPLC, IC, IC-PAD, ICPMS, Microbalance, TOR, WICPMS`. These represent the main analytical methods used for analysis.
- The possible values for `reportTypes` are `CARB, CARBONYLS, DICHOT, HCB, IC, ICPMS, LEV, NA, NH4, PAH, PCB, PCDD, PM10, PM2.5, PM2.5-10, SPEC, VOC, VOC_4HR, WICPMS`. These represent the type of report from which the data was originally sourced.
- With the exception of the above, all of the other rules and restrictions of the [NAPSContinuousDataQuery](#napscontinuousdataquery) apply.

## NAPSIntegratedHeatMap

A Java tool that generates highly customizable heat map diagrams for the visualization of NAPS integrated data. It functions the same as the [NAPSContinuousHeatMap](#napscontinuousheatmap) and accepts all of the same command line arguments, with the exception that the data fields used for the x and y axes (`group1` and `group2`) cannot include `HOUR`, since hour attribute only applies to continuous data, not integrated data.

You can invoke this tool by running the class `com.dbf.naps.data.analysis.heatmap.integrated.NAPSIntegratedDataHeatMap`.

**Command line usage:**
```
 -a,   --aggregateFunction <arg>  Data aggregation function (AVG, MIN, MAX, COUNT, SUM, P50, P95, P98, P99).
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
 -fts, --fontScale                Relative font size. Must be greater than 0 and no more than 10. Default is 1.
 -fy,  --filePerYear              Create a separate file for each year.
 -g1,  --group1 <arg>             Data field for the heat map X-axis.
 -g2,  --group2 <arg>             Data field for the heat map Y-axis.
 -gl,  --gridLines                Include grid lines on the heat map.
 -gv,  --gridValues               Include grid values on the heat map.
 -json,--generateJSON             Generate a corresponding JSON file containing the raw data for each heat map.
 -ls,  --legendDecimals <arg>     Number of decimal digits to use for the legend, 0 to 20 (inclusive). Default is 4.
 -m,   --months <arg>             Comma-separated list of months of the year, starting at 1 for January.
 -mtd, --methods <arg>            Comma-separated list of analytical method names.
                                     (ED-XRF, GC-FID, GC-MS, GC-MS TP+G, HPLC, IC, IC-PAD, ICPMS, Microbalance, TOR, WICPMS)
 -o,   --overwriteFiles           Replace existing files.
 -p,   --dataPath <arg>           Local path to save the data.
 -pn,  --pollutants <arg>         Comma-separated list of pollutant names.
 -pt,  --provTerr <arg>           Comma-separated list of 2-digit province & territory codes.
 -rlb, --resultLowerBound <arg>   Lower bound (inclusive) of post-aggregated results to include. Results less than this
                                     threshold will be filtered out of the result set after aggregation.
 -rt,  --reportTypes <arg>        Comma-separated list of report types. This represents the origin of the data.
                                     (CARB, CARBONYLS, DICHOT, HCB, IC, ICPMS, LEV, NA, NH4, PAH, PCB, PCDD, PM10, PM2.5, PM2.5-10, SPEC, VOC, VOC_4HR, WICPMS)
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

**Notes:**
- Possible values for `group1` and `group2` are `YEAR,MONTH, DAY, DAY_OF_WEEK, DAY_OF_YEAR, WEEK_OF_YEAR, NAPS_ID, POLLUTANT, PROVINCE_TERRITORY, SITE_TYPE, URBANIZATION`. 
- AQHI values are not supported for the integrated data set.
- The possible values for `methods` are `ED-XRF, GC-FID, GC-MS, GC-MS TP+G, HPLC, IC, IC-PAD, ICPMS, Microbalance, TOR, WICPMS`. These represent the main analytical methods used for analysis.
- The possible values for `reportTypes` are `CARB, CARBONYLS, DICHOT, HCB, IC, ICPMS, LEV, NA, NH4, PAH, PCB, PCDD, PM10, PM2.5, PM2.5-10, SPEC, VOC, VOC_4HR, WICPMS`. These represent the type of report from which the data was originally sourced.
- With the exception of the above, all of the other rules and restrictions of the [NAPSContinuousHeatMap](#napscontinuousheatmap) apply.

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

You can find the latest package [here](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/packages). Alternatively, in the [/target](https://github.com/dbeaudoinfortin/NAPSDataAnalysis/tree/main/target) directory you can find the pre-compiled jar file naps_data.jar. This is a shaded jar file which means that it contains all of the 3rd party dependencies inside of it. Assuming you have Java 21 installed and 
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

### As of March 2025

**Continuous**
|Compound|Continuous Data Points|
|:--- | ---:|
|O3|59212333|
|NO2|43286990|
|NO|40336561|
|SO2|38562169|
|PM2.5|37529024|
|NOX|32531756|
|CO|22996259|
|PM10|10049428|

**Integrated**
|Compound|Integrated Data Points|
|:--- | ---:|
|1-Bromopropane|3024|
|1-Butene/Isobutene|78317|
|1-Butyne|54678|
|1-Decene|31484|
|1-Heptene|32715|
|1-Hexene|56593|
|1-Hexene/2-Methyl-1-Pentene|17651|
|1-Me-Pyrene|6532|
|1-Methylcyclohexene|34763|
|1-Methylcyclopentene|34771|
|1-Methylpyrene|3357|
|1-Nonene|31173|
|1-Octene|32108|
|1-Pentene|78922|
|1-Propyne|48033|
|1-Undecene|23655|
|1,1-Dichloroethane|52507|
|1,1-Dichloroethylene|54357|
|1,1,1-Trichloroethane|76992|
|1,1,2-Trichloroethane|71296|
|1,1,2,2-Tetrachloroethane|54220|
|1,2-Dichlorobenzene|54365|
|1,2-Dichloroethane|72285|
|1,2-Dichloropropane|54376|
|1,2-Diethylbenzene|39184|
|1,2,3-Trimethylbenzene|78644|
|1,2,4-Trichlorobenzene|53912|
|1,2,4-Trimethylbenzene|78885|
|1,3-Butadiene|78936|
|1,3-Dichlorobenzene|54391|
|1,3-Diethylbenzene|78665|
|1,3,5-Trimethylbenzene|78887|
|1,4-Dichlorobenzene|78890|
|1,4-Dichlorobutane|37265|
|1,4-Diethylbenzene|77771|
|1234678-H7CDD|3076|
|1234678-H7CDF|3076|
|123478-H6CDD|3076|
|123478-H6CDF|3076|
|1234789-H7CDF|3076|
|123678-H6CDD|3076|
|123678-H6CDF|3076|
|12378-P5CDD|3076|
|12378-P5CDF|3076|
|123789-H6CDD|3076|
|123789-H6CDF|3076|
|13C12-H6CDD|3076|
|13C12-H6CDF|3049|
|13C12-H7CDD|3076|
|13C12-H7CDF|3049|
|13C12-OCDD|3076|
|13C12-P5CDD|3076|
|13C12-P5CDF|3049|
|13C12-TCDD|3076|
|13C12-TCDF|3076|
|13C6-HCB|1609|
|13C6-PCP|1602|
|2-Ethyl-1-Butene|21807|
|2-Ethyltoluene|78923|
|2-Me-Fluorene|6534|
|2-Methyl-1-Butene|75660|
|2-Methyl-1-Pentene|5574|
|2-Methyl-2-Butene|75637|
|2-methyl-2-Pentene|993|
|2-Methylfluorene|3355|
|2-Methylheptane|78829|
|2-Methylhexane|78890|
|2-Methylpentane|78751|
|2-Pentanone|864|
|2-Pentanone/Isovaleraldehyde|7091|
|2,2-Dimethylbutane|78783|
|2,2-Dimethylhexane|46134|
|2,2-Dimethylpentane|59357|
|2,2-Dimethylpropane|54681|
|2,2,3-Trimethylbutane|35097|
|2,2,4-Trimethylpentane|78921|
|2,2,5-Trimethylhexane|58259|
|2,3-Dimethylbutane|78933|
|2,3-Dimethylpentane|78925|
|2,3,4-Trimethylpentane|78929|
|2,4-Dimethylhexane|78903|
|2,4-Dimethylpentane|78925|
|2,5-Dimethylbenzaldehyde|7237|
|2,5-Dimethylheptane|1304|
|2,5-Dimethylhexane|78893|
|234678-H6CDF|3076|
|23478-P5CDF|3076|
|2378-TCDD|3076|
|2378-TCDF|3076|
|3-Ethyltoluene|78926|
|3-Me-Cholanthrene|6525|
|3-Methyl-1-Butene|38860|
|3-Methyl-1-Pentene|54388|
|3-Methylcholanthrene|3357|
|3-Methylheptane|78877|
|3-Methylhexane|78928|
|3-Methyloctane|1281|
|3-Methylpentane|78548|
|3,6-Dimethyloctane|37365|
|4-Ethyltoluene|78924|
|4-Methyl-1-Pentene|53818|
|4-Methylheptane|78894|
|4-Methyloctane|1294|
|7-Me-Benz(a)Anthracene|6527|
|7-Methylbenz(a)Anthracene|3357|
|A-Pinene|53407|
|Acenaphthene|9886|
|Acenaphthylene|9877|
|Acetaldehyde|13910|
|Acetate|34557|
|Acetone|13885|
|Acetylene|77720|
|Acrolein|9442|
|Aluminum|119434|
|Ammonia|32528|
|Ammonium|76889|
|Anthanthrene|9886|
|Anthracene|9869|
|Antimony|116846|
|Arabitol|13660|
|Arsenic|37016|
|B-Pinene|52911|
|Barium|143053|
|Benz(a)Anthracene|9888|
|Benzaldehyde|10805|
|Benzene|78879|
|Benzo(a)Fluorene|9889|
|Benzo(a)Pyrene|9861|
|Benzo(b)&(k)Fluoranthene|1981|
|Benzo(b)Chrysene|9891|
|Benzo(b)Fluoranthene|7965|
|Benzo(b)Fluorene|9886|
|Benzo(e)Pyrene|9890|
|Benzo(g,h,i)Fluoranthene|9889|
|Benzo(g,h,i)Perylene|9886|
|Benzo(k)Fluoranthene|7969|
|Benzylchloride|43133|
|Beryllium|29991|
|Bismuth|4131|
|Bromide|40544|
|Bromine|93793|
|Bromodichloromethane|38568|
|Bromoform|72145|
|Bromomethane|70432|
|Bromotrichloromethane|10550|
|Butane|78924|
|Butyraldehyde/Iso-Butyraldehyde|2301|
|Cadmium|128777|
|Calcium|127900|
|Camphene|53029|
|Carbontetrachloride|78900|
|Cerium|37924|
|Cesium|54526|
|Chloride|70959|
|Chlorobenzene|71739|
|Chloroethane|70429|
|Chloroform|78618|
|Chloromethane|76996|
|Chromium|128383|
|Chrysene|7965|
|Chrysene & Triphenylene|2000|
|cis-1,2-Dichloroethylene|36928|
|cis-1,2-Dimethylcyclohexane|65957|
|cis-1,3-Dichloropropene|39194|
|cis-1,3-Dimethylcyclohexane|61191|
|cis-1,4/t-1,3-Dimethylcyclohexane|57679|
|cis-2-Butene|78934|
|cis-2-Heptene|35862|
|cis-2-Hexene|78573|
|cis-2-Octene|9755|
|cis-2-Pentene|78909|
|cis-3-Heptene|8941|
|cis-3-Methyl-2-Pentene|75931|
|cis-4-Methyl-2-pentene|65305|
|Cobalt|37016|
|Copper|49051|
|Crotonaldehyde|10805|
|Cyclohexane|78934|
|Cyclohexene|34678|
|Cyclopentane|78787|
|Cyclopentene|56770|
|D-Limonene|53057|
|Decane|78901|
|Dibenz(a,c)&(a,h)Anthracene|9819|
|Dibromochloromethane|39061|
|Dibromomethane|39198|
|Dodecane|77626|
|EC|5871|
|EC(A)|13320|
|EC(B)|6305|
|EC_R|5900|
|EC_T|5900|
|EC1|5900|
|EC1(A)|13320|
|EC1(B)|6305|
|EC2|5900|
|EC2(A)|13320|
|EC2(B)|6305|
|EC3|5900|
|EC3(A)|13320|
|EC3(B)|6305|
|EDB|38570|
|Ethane|77982|
|Ethylbenzene|78893|
|Ethylbromide|37371|
|Ethylene|76634|
|Fluoranthene|9891|
|Fluorene|9887|
|Fluoride|44302|
|Formaldehyde|13913|
|Formate|34559|
|Freon 11|78806|
|Freon 113|42474|
|Freon 114|72113|
|Freon 12|66640|
|Freon 22|78682|
|Galactosan|13661|
|Gallium|4131|
|H6CDD|3076|
|H6CDF|3076|
|H7CDD|3076|
|H7CDF|3076|
|HCB|1566|
|Heptane|78835|
|Hexachlorobutadiene|53882|
|Hexanal|10802|
|Hexane|77905|
|Hexylbenzene|35788|
|Indane|78500|
|Indeno(1,2,3-cd)Fluoranthene|5899|
|Indeno(1,2,3-cd)Pyrene|9877|
|Iron|128397|
|iso-Butylbenzene|54583|
|iso-Propylbenzene|78908|
|Isobutane|78901|
|Isopentane|78830|
|Isoprene|78641|
|Isovaleraldehyde|3165|
|Lanthanum|12655|
|Lead|130783|
|Levoglucosan|13660|
|Lithium|44208|
|m-Tolualdehyde|9591|
|m and p-Xylene|78901|
|Magnesium|44352|
|Manganese|135436|
|Mannitol|12223|
|Mannosan|13660|
|MEK|13012|
|MEK/Butyraidehyde|864|
|Methanesulphonic Acid|34567|
|Methylcyclohexane|78912|
|Methylcyclopentane|78934|
|MIBK|10745|
|Molybdenum|37016|
|MTBE|36421|
|n-Butylbenzene|61104|
|n-Propylbenzene|78923|
|N_NO3|5986|
|Naphthalene|73265|
|Nickel|128944|
|Nitrate|105295|
|Nitric Acid|18728|
|Nitrite|59213|
|Nitrous Acid|13318|
|Nonane|78769|
|o-Tolualdehyde|7290|
|o-Xylene|78897|
|OC|5871|
|OC(A)|13320|
|OC(B)|13320|
|OC(corr)|13284|
|OC_R|5900|
|OC_RA|5872|
|OC_RB|5871|
|OC_T|5900|
|OC1|5900|
|OC1(A)|13320|
|OC1(B)|13320|
|OC2|5900|
|OC2(A)|13320|
|OC2(B)|13320|
|OC3|5900|
|OC3(A)|13320|
|OC3(B)|13320|
|OC4|5900|
|OC4(A)|13320|
|OC4(B)|13320|
|OCDD|3076|
|OCDF|3075|
|OCS|1614|
|Octane|78927|
|Oxalate|44349|
|p-Cymene|78693|
|p-Tolualdehyde|9591|
|P5CDD|3076|
|P5CDF|3076|
|PCB-105|302|
|PCB-114|302|
|PCB-118|302|
|PCB-123|302|
|PCB-126|302|
|PCB-156|302|
|PCB-157|302|
|PCB-167|302|
|PCB-169|302|
|PCB-189|302|
|PCB-77|302|
|PCB-81|302|
|PCP|1598|
|Pentane|78910|
|Perylene|9877|
|Phenanthrene|9889|
|Phosphate|39032|
|PM10|1560|
|PM2.5|78284|
|PM2.5-10|45397|
|POC(A)|13320|
|POC(B)|6305|
|POC_R|5900|
|POC_T|5900|
|Potassium|127902|
|Propane|78849|
|Propionaldehyde|13910|
|Propionate|34567|
|Propylene|78876|
|Pyrene|9891|
|Retene|6779|
|Rubidium|83759|
|sec-Butylbenzene|54663|
|Selenium|128364|
|Silicon|91926|
|Silver|36954|
|Sodium|64891|
|Strontium|143096|
|Styrene|76328|
|Sulphate|92050|
|Sulphur|91931|
|Sulphur Dioxide|31856|
|T_NO3|5993|
|TC|5900|
|TC(A)|13320|
|TC(B)|6305|
|TC(corr)|13284|
|TCDD|3076|
|TCDF|3076|
|TEQ|3076|
|tert-Butylbenzene|37364|
|Tetrachloroethylene|72312|
|Thallium|29991|
|Tin|116832|
|Titanium|119230|
|Toluene|78912|
|trans-1,2-Dichloroethylene|39208|
|trans-1,2-Dimethylcyclohexane|54626|
|trans-1,3-Dichloropropene|39022|
|trans-1,4-Dimethylcyclohexane|56374|
|trans-2-Butene|78929|
|trans-2-Heptene|36398|
|trans-2-Hexene|78678|
|trans-2-Octene|46534|
|trans-2-Pentene|78905|
|trans-3-Heptene|34355|
|trans-3-Methyl-2-Pentene|76625|
|trans-4-Methyl-2-Pentene|47582|
|Trichloroethylene|78745|
|Triphenylene|7965|
|Tungsten|4131|
|Undecane|78892|
|Uranium|20905|
|Valeraldehyde|10803|
|Vanadium|128926|
|Vinylchloride|72313|
|Zinc|128947|
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
- The rendering of heat maps is entirely written from scratch by myself. I have spun the code out to its own [GitHub project](https://github.com/dbeaudoinfortin/HeatMaps). Check it out if you want to add beautiful, customizable heat maps to you own Java project.

# Legal Stuff

Copyright (c) 2024 David Fortin

This software (NAPS Data Analysis Toolbox) is provided by David Fortin under the MIT License, meaning you are free to use it however you want, as long as you include the original copyright notice (above) and license notice in any copy you make. You just can't hold me liable in case something goes wrong. License details can be read [here](https://github.com/dbeaudoinfortin/NAPSDataAnalysis?tab=MIT-1-ov-file)

The data itself is provided by the [National Air Pollution Surveillance](https://www.canada.ca/en/environment-climate-change/services/air-pollution/monitoring-networks-data/national-air-pollution-program.html) (NAPS) program, which is run by the Analysis and Air Quality Section of Environment and Climate Change Canada. The data is licensed under the terms of the Canadian [Open Government Licence](https://open.canada.ca/en/open-government-licence-canada) and can be freely re-published under those terms.
