#!/bin/bash

set -e

if [ ${OVERWRITE_FILES:-false} = true ]; then
    OVERWRITE_FILES="-o"
else
    OVERWRITE_FILES=""
fi

SITES_DOWNLOADER_ARGS="-p /raw ${OVERWRITE_FILES} -t ${NAPS_THREADS}"
DATA_DOWNLOADER_ARGS="-p /raw ${OVERWRITE_FILES} ${NAPS_YEAR_START:+"-ys ${NAPS_YEAR_START}"} ${NAPS_YEAR_END:+"-ye ${NAPS_YEAR_END}"} -t ${NAPS_THREADS}"
LOADER_ARGS="-p /raw -dbh ${NAPS_DB_HOST} -dbn ${NAPS_DB_NAME} -dbu ${NAPS_DB_USER} -dbp ${NAPS_DB_PASSWORD} -t ${NAPS_THREADS}"

if [ ${1} = "downloader" ]; then
    java -cp naps_data.jar com.dbf.naps.data.download.sites.NAPSSitesDownloader $SITES_DOWNLOADER_ARGS
    java -cp naps_data.jar com.dbf.naps.data.download.continuous.NAPSContinuousDataDownloader $DATA_DOWNLOADER_ARGS
    java -cp naps_data.jar com.dbf.naps.data.download.integrated.NAPSIntegratedDataDownloader $DATA_DOWNLOADER_ARGS
elif [ ${1} = "loader" ]; then
    java -cp naps_data.jar com.dbf.naps.data.loader.sites.NAPSSitesLoader $LOADER_ARGS
    java -cp naps_data.jar com.dbf.naps.data.loader.continuous.NAPSContinuousDataLoader $LOADER_ARGS
    java -cp naps_data.jar com.dbf.naps.data.loader.integrated.NAPSIntegratedDataLoader $LOADER_ARGS
else
    exit 1
fi
