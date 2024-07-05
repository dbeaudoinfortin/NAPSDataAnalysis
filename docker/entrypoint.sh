#!/bin/bash

set -e

THREADS=${THREADS:-2}

if [ ${1} = "downloader" ]; then
    java -cp naps_data.jar com.dbf.naps.data.download.sites.NAPSSitesDownloader -p /raw -t $THREADS
    java -cp naps_data.jar com.dbf.naps.data.download.continuous.NAPSContinuousDataDownloader -p /raw -t $THREADS
    java -cp naps_data.jar com.dbf.naps.data.download.integrated.NAPSIntegratedDataDownloader -p /raw -t $THREADS
elif [ ${1} = "loader" ]; then
    java -cp naps_data.jar com.dbf.naps.data.loader.sites.NAPSSitesLoader -p /raw -t $THREADS
    java -cp naps_data.jar com.dbf.naps.data.loader.continuous.NAPSContinuousDataLoader -p /raw -t $THREADS
    java -cp naps_data.jar com.dbf.naps.data.loader.integrated.NAPSIntegratedDataLoader -p /raw -t $THREADS
else
    exit 1
fi