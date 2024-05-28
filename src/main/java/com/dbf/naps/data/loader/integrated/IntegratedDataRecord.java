package com.dbf.naps.data.loader.integrated;

import java.math.BigDecimal;

import com.dbf.naps.data.loader.DataRecord;

public class IntegratedDataRecord extends DataRecord {

	private Boolean fine;
	private String cartridge;
	private String media;
	private BigDecimal mass;
	private BigDecimal volume;
	private BigDecimal tsp;
	private Double duration;

	public IntegratedDataRecord() {	}

	public Boolean getFine() {
		return fine;
	}

	public void setFine(Boolean fine) {
		this.fine = fine;
	}

	public BigDecimal getMass() {
		return mass;
	}

	public void setMass(BigDecimal mass) {
		this.mass = mass;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getTSP() {
		return tsp;
	}

	public void setTSP(BigDecimal tsp) {
		this.tsp = tsp;
	}

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public String getCartridge() {
		return cartridge;
	}

	public void setCartridge(String cartridge) {
		this.cartridge = cartridge;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
	}	
}
