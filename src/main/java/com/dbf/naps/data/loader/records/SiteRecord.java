package com.dbf.naps.data.loader.records;

import java.math.BigDecimal;

public class SiteRecord {

	private int id;
    private int NAPSId;
    private String stationName;
    private String cityName;
    private String provTerr;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String siteType;
    private String urbanization;
    private String neighbourhood;
    private String landUse;
    private String scale;
    private Integer elevation;

    public int getId() {
        return id;
    }

    public int getNAPSId() {
        return NAPSId;
    }

    public void setNAPSId(int NAPSId) {
        this.NAPSId = NAPSId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvTerr() {
        return provTerr;
    }

    public void setProvTerr(String provTerr) {
        this.provTerr = provTerr;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public String getUrbanization() {
        return urbanization;
    }

    public void setUrbanization(String urbanization) {
        this.urbanization = urbanization;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public String getLandUse() {
        return landUse;
    }

    public void setLandUse(String landUse) {
        this.landUse = landUse;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public Integer getElevation() {
        return elevation;
    }

    public void setElevation(Integer elevation) {
        this.elevation = elevation;
    }
}
