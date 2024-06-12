package com.dbf.naps.data.loader.records;

import java.math.BigDecimal;

public class SampleRecord {

	private Integer id;
	private String napsID;
	private String canisterID;
	private Boolean fine;
	private String cartridge;
	private String media;
	private String type;
	private BigDecimal mass;
	private BigDecimal specMass;
	private BigDecimal dichotMass;
	private BigDecimal volume;
	private Double duration;
	private BigDecimal tsp;

	public SampleRecord() {}

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

	public String getNapsID() {
		return napsID;
	}

	public void setNapsID(String napsID) {
		this.napsID = napsID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCanisterID() {
		return canisterID;
	}

	public void setCanisterID(String canisterID) {
		this.canisterID = canisterID;
	}

	public BigDecimal getSpecMass() {
		return specMass;
	}

	public void setSpecMass(BigDecimal specMass) {
		this.specMass = specMass;
	}

	public BigDecimal getDichotMass() {
		return dichotMass;
	}

	public void setDichotMass(BigDecimal dichotMass) {
		this.dichotMass = dichotMass;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}	
}
