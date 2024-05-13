# Overview
Canada National Air Pollution Surveillance Program data downloader, extractor and schema importer. 

This project will eventually contain a collection of tools to assist in the analysis of Canadian air quality data. The data is provided by the National Air Pollution Surveillance (NAPS) program, which is part of Environment and Climate Change Canada. You can view the original data here: https://data-donnees.az.ec.gc.ca/data/air/monitor/national-air-pollution-surveillance-naps-program/

All usage is for non-comercial research purposes. I am not affiliated with the Government of Canada.

# NAPSContinuousDataDownloader

Will download all of the hourly continuous data for the provided years into the provided directory.

**Command line usage:**
```
 -o,  --overwriteFiles       Replace existing files.
 -p,  --downloadPath <arg>   Local path for downloaded files.
 -t,  --threadCount <arg>    Maximum number of parallel threads.
 -ye, --yearEnd <arg>        End year (inclusive).
 -ys, --yearStart <arg>      Start year (inclusive).
```

# NAPSContinuousDataLoader

Loads all of the raw data from the provided directory into a PostgreSQL database, as specified.

**Command line usage:**
```
 -p,   --dataPath <arg>       Local path for raw data files previously downloaded.
 -dbh, --dbHost <arg>        Hostname for the PostgreSQL database. Default: localhost
 -dbt, --dbPort <arg>        Port for the PostgreSQL database. Default: 5432
 -dbn, --dbName <arg>        Database name for the PostgreSQL database. Default: naps
 -dbu, --dbUser <arg>        Database user name for the PostgreSQL database. Default: postgres
 -dbp, --dbPass <arg>        Database password for the PostgreSQL database. Default: password
 -t,   --threadCount <arg>    Maximum number of parallel threads.
```

# Notes

- Requires Java 17
