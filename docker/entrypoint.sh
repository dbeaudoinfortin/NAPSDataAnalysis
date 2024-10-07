#!/bin/bash

set -e

OVERWRITE_FILES=$([[ $OVERWRITE_FILES = true ]] && echo "-o" || echo "")
NAPS_EXPORTER_FP=$([[ $NAPS_EXPORTER_FP = true ]] && echo "-fp" || echo "")
NAPS_EXPORTER_FS=$([[ $NAPS_EXPORTER_FS = true ]] && echo "-fs" || echo "")
NAPS_EXPORTER_FY=$([[ $NAPS_EXPORTER_FY = true ]] && echo "-fy" || echo "")

SITES_DOWNLOADER_ARGS="-p /raw ${OVERWRITE_FILES} -t ${NAPS_THREADS}"
DATA_DOWNLOADER_ARGS="-p /raw ${OVERWRITE_FILES} ${NAPS_YEAR_START:+"-ys ${NAPS_YEAR_START}"} ${NAPS_YEAR_END:+"-ye ${NAPS_YEAR_END}"} -t ${NAPS_THREADS}"
LOADER_ARGS="-p /raw -dbh ${NAPS_DB_HOST} -dbn ${NAPS_DB_NAME} -dbu ${NAPS_DB_USER} -dbp ${NAPS_DB_PASSWORD} -t ${NAPS_THREADS}"
EXPORTER_ARGS="
    -p /exported \
    -dbh ${NAPS_DB_HOST} -dbn ${NAPS_DB_NAME} -dbu ${NAPS_DB_USER} -dbp ${NAPS_DB_PASSWORD} \
    ${OVERWRITE_FILES} ${NAPS_EXPORTER_FP} ${NAPS_EXPORTER_FS} ${NAPS_EXPORTER_FY} \
    ${NAPS_EXPORTER_PN:+"-pn ${NAPS_EXPORTER_PN}"} \
    ${NAPS_EXPORTER_SID:+"-sid ${NAPS_EXPORTER_SID}"} \
    ${NAPS_YEAR_START:+"-ys ${NAPS_YEAR_START}"} \
    ${NAPS_YEAR_END:+"-ye ${NAPS_YEAR_END}"} \
    -t ${NAPS_THREADS}
"

if [ ${1} = "downloader" ]; then
    java -cp naps_data.jar com.dbf.naps.data.download.sites.NAPSSitesDownloader $SITES_DOWNLOADER_ARGS
    java -cp naps_data.jar com.dbf.naps.data.download.continuous.NAPSContinuousDataDownloader $DATA_DOWNLOADER_ARGS
    java -cp naps_data.jar com.dbf.naps.data.download.integrated.NAPSIntegratedDataDownloader $DATA_DOWNLOADER_ARGS
elif [ ${1} = "loader" ]; then
    java -cp naps_data.jar com.dbf.naps.data.loader.sites.NAPSSitesLoader $LOADER_ARGS
    java -cp naps_data.jar com.dbf.naps.data.loader.continuous.NAPSContinuousDataLoader $LOADER_ARGS
    java -cp naps_data.jar com.dbf.naps.data.loader.integrated.NAPSIntegratedDataLoader $LOADER_ARGS
elif [ ${1} = "exporter" ]; then
    java -cp naps_data.jar com.dbf.naps.data.exporter.continuous.NAPSContinuousDataExporter $EXPORTER_ARGS
    java -cp naps_data.jar com.dbf.naps.data.exporter.integrated.NAPSIntegratedDataExporter $EXPORTER_ARGS
else
    echo "Nothing to do!"
    exit 1
fi
