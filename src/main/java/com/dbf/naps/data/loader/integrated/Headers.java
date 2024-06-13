package com.dbf.naps.data.loader.integrated;

import java.util.ArrayList;
import java.util.List;

public class Headers {
	
	//These are all of the known headers that are derived or represent metadata rather than raw data.
	public static final List<String> DEFAULT_IGNORED_HEADERS = new ArrayList<String>();
	
	static {
		DEFAULT_IGNORED_HEADERS.add("%"); //% Recovery
		DEFAULT_IGNORED_HEADERS.add("RECOVERY"); //Recovery %, Recovery-AE, Recovery-PHE, etc.
		DEFAULT_IGNORED_HEADERS.add("SAMPLE"); //Sample Volume, Sample Type, Sample Date & Sample ID
		DEFAULT_IGNORED_HEADERS.add("SAMPLING"); //Sampling Date, Sampling Type
		DEFAULT_IGNORED_HEADERS.add("TSP"); //Total suspended particles
		DEFAULT_IGNORED_HEADERS.add("T.S.P"); //Total suspended particles
		DEFAULT_IGNORED_HEADERS.add("D.L."); //Detection limit
		DEFAULT_IGNORED_HEADERS.add("_DL"); //Detection limit
		DEFAULT_IGNORED_HEADERS.add("-MDL"); //Detection limit
		DEFAULT_IGNORED_HEADERS.add("TOTAL"); //TOTAL PAH
		DEFAULT_IGNORED_HEADERS.add("C/F"); //Coarse/Fine
		DEFAULT_IGNORED_HEADERS.add("F/C"); //Fine/Coarse
		DEFAULT_IGNORED_HEADERS.add("MASS"); //Sample Mass
		DEFAULT_IGNORED_HEADERS.add("SURROGATE"); //Surrogate Recovery
		DEFAULT_IGNORED_HEADERS.add("48 H"); //Not sure why this is a column
		DEFAULT_IGNORED_HEADERS.add("48-H"); //Same as 48 H
		DEFAULT_IGNORED_HEADERS.add("CANISTER"); //Canister ID#
		DEFAULT_IGNORED_HEADERS.add("CART"); //Cart, Cartridge
		DEFAULT_IGNORED_HEADERS.add("START"); //Start Time
		DEFAULT_IGNORED_HEADERS.add("END"); //End Time
		DEFAULT_IGNORED_HEADERS.add("STOP"); //Stop Time
		DEFAULT_IGNORED_HEADERS.add("DURATION"); //Duration
		DEFAULT_IGNORED_HEADERS.add("SUM"); //Sum PCB TEQ*
		DEFAULT_IGNORED_HEADERS.add("FIELD"); //Field ID
		DEFAULT_IGNORED_HEADERS.add("SPECIATION"); //Speciation Mass (ug/m3)
		DEFAULT_IGNORED_HEADERS.add("MEDIA"); //Media
		DEFAULT_IGNORED_HEADERS.add("FRACTION"); //Fraction
		DEFAULT_IGNORED_HEADERS.add("DICH"); //Dich/Partisol Mass (ug/m3)
		DEFAULT_IGNORED_HEADERS.add("PRES"); //PRESS, Pres.
		DEFAULT_IGNORED_HEADERS.add("TEMP"); //TEMP, Temp.
		DEFAULT_IGNORED_HEADERS.add("WS"); //WS
		DEFAULT_IGNORED_HEADERS.add("HUM"); //HUM
		DEFAULT_IGNORED_HEADERS.add("TDP"); //TDP
		DEFAULT_IGNORED_HEADERS.add("WD"); //WD
		DEFAULT_IGNORED_HEADERS.add("-VFLAG"); //Validation Flag
		DEFAULT_IGNORED_HEADERS.add("VOLUME"); //Actual Volume
		DEFAULT_IGNORED_HEADERS.add("SITE"); //Site Type
	}
	
	//These are all of the know sheet that can be safely ignored
	public static final List<String> DEFAULT_IGNORED_SHEETS = new ArrayList<String>();
	static {
		
		DEFAULT_IGNORED_SHEETS.add("CHANGELOG"); 
		DEFAULT_IGNORED_SHEETS.add("STATION");
		DEFAULT_IGNORED_SHEETS.add("METADATA"); 
		DEFAULT_IGNORED_SHEETS.add("TSP"); 
	}
}
