# Overview
Canada National Air Pollution Surveillance Program data downloader, extractor and schema importer. 

This project will eventually contain a collection of tools to assist in the analysis of Canadian air quality data. The data is provided by the National Air Pollution Surveillance (NAPS) program, which is part of Environment and Climate Change Canada. You can view the original data here: https://data-donnees.az.ec.gc.ca/data/air/monitor/national-air-pollution-surveillance-naps-program/

All usage is for non-comercial research purposes. I am not affiliated with the Government of Canada.

# NAPSDownloader

Will download all of the hourly, continuous data for the provided years into the provided directory.

Command line usage:

 -o, --overwriteFiles       Replace existing files.
 -p, --downloadPath <arg>   Local path for downloaded files.
 -t, --threadCount <arg>    Maximum number of parallel threads.
 -ye, --yearEnd <arg>       End year (inclusive).
 -ys, --yearStart <arg>     Start year (inclusive).
