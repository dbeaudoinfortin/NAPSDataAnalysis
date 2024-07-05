#!/bin/bash

set -e

DOWNLOADER_ARGS="-p /raw -t ${NAPS_THREADS}"
LOADER_ARGS="-p /raw -dbh ${NAPS_DB_HOST} -dbu ${POSTGRES_USER} -dbp ${POSTGRES_PASSWORD} -t ${NAPS_THREADS}"

if [ ${1} = "downloader" ]; then
    java -cp naps_data.jar com.dbf.naps.data.download.sites.NAPSSitesDownloader $DOWNLOADER_ARGS
    java -cp naps_data.jar com.dbf.naps.data.download.continuous.NAPSContinuousDataDownloader $DOWNLOADER_ARGS
    java -cp naps_data.jar com.dbf.naps.data.download.integrated.NAPSIntegratedDataDownloader $DOWNLOADER_ARGS
elif [ ${1} = "loader" ]; then
    java -cp naps_data.jar com.dbf.naps.data.loader.sites.NAPSSitesLoader $LOADER_ARGS
    java -cp naps_data.jar com.dbf.naps.data.loader.continuous.NAPSContinuousDataLoader $LOADER_ARGS
    java -cp naps_data.jar com.dbf.naps.data.loader.integrated.NAPSIntegratedDataLoader $LOADER_ARGS
else
    exit 1
fi
