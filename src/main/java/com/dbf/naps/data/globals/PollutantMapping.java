package com.dbf.naps.data.globals;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PollutantMapping {
	private static final Map<String, String> POLLUTANT_LOOKUP = new ConcurrentHashMap<String, String>();
	static {
		POLLUTANT_LOOKUP.put("H", "Hydrogen");
		POLLUTANT_LOOKUP.put("He", "Helium");
		POLLUTANT_LOOKUP.put("Li", "Lithium");
		POLLUTANT_LOOKUP.put("Be", "Beryllium");
		POLLUTANT_LOOKUP.put("B", "Boron");
		POLLUTANT_LOOKUP.put("C", "Carbon");
		POLLUTANT_LOOKUP.put("N", "Nitrogen");
		POLLUTANT_LOOKUP.put("O", "Oxygen");
		POLLUTANT_LOOKUP.put("F", "Fluorine");
		POLLUTANT_LOOKUP.put("Ne", "Neon");
		POLLUTANT_LOOKUP.put("Na", "Sodium");
		POLLUTANT_LOOKUP.put("Mg", "Magnesium");
		POLLUTANT_LOOKUP.put("Al", "Aluminum");
		POLLUTANT_LOOKUP.put("Si", "Silicon");
		POLLUTANT_LOOKUP.put("P", "Phosphorus");
		POLLUTANT_LOOKUP.put("S", "Sulphur");
		POLLUTANT_LOOKUP.put("Cl", "Chlorine");
		POLLUTANT_LOOKUP.put("Ar", "Argon");
		POLLUTANT_LOOKUP.put("K", "Potassium");
		POLLUTANT_LOOKUP.put("Ca", "Calcium");
		POLLUTANT_LOOKUP.put("Sc", "Scandium");
		POLLUTANT_LOOKUP.put("Ti", "Titanium");
		POLLUTANT_LOOKUP.put("V", "Vanadium");
		POLLUTANT_LOOKUP.put("Cr", "Chromium");
		POLLUTANT_LOOKUP.put("Mn", "Manganese");
		POLLUTANT_LOOKUP.put("Fe", "Iron");
		POLLUTANT_LOOKUP.put("Co", "Cobalt");
		POLLUTANT_LOOKUP.put("Ni", "Nickel");
		POLLUTANT_LOOKUP.put("Cu", "Copper");
		POLLUTANT_LOOKUP.put("Zn", "Zinc");
		POLLUTANT_LOOKUP.put("Ga", "Gallium");
		POLLUTANT_LOOKUP.put("Ge", "Germanium");
		POLLUTANT_LOOKUP.put("As", "Arsenic");
		POLLUTANT_LOOKUP.put("Se", "Selenium");
		POLLUTANT_LOOKUP.put("Br", "Bromine");
		POLLUTANT_LOOKUP.put("Kr", "Krypton");
		POLLUTANT_LOOKUP.put("Rb", "Rubidium");
		POLLUTANT_LOOKUP.put("Sr", "Strontium");
		POLLUTANT_LOOKUP.put("Y", "Yttrium");
		POLLUTANT_LOOKUP.put("Zr", "Zirconium");
		POLLUTANT_LOOKUP.put("Nb", "Niobium");
		POLLUTANT_LOOKUP.put("Mo", "Molybdenum");
		POLLUTANT_LOOKUP.put("Tc", "Technetium");
		POLLUTANT_LOOKUP.put("Ru", "Ruthenium");
		POLLUTANT_LOOKUP.put("Rh", "Rhodium");
		POLLUTANT_LOOKUP.put("Pd", "Palladium");
		POLLUTANT_LOOKUP.put("Ag", "Silver");
		POLLUTANT_LOOKUP.put("Cd", "Cadmium");
		POLLUTANT_LOOKUP.put("In", "Indium");
		POLLUTANT_LOOKUP.put("Sn", "Tin");
		POLLUTANT_LOOKUP.put("Sb", "Antimony");
		POLLUTANT_LOOKUP.put("Te", "Tellurium");
		POLLUTANT_LOOKUP.put("I", "Iodine");
		POLLUTANT_LOOKUP.put("Xe", "Xenon");
		POLLUTANT_LOOKUP.put("Cs", "Cesium");
		POLLUTANT_LOOKUP.put("Ba", "Barium");
		POLLUTANT_LOOKUP.put("La", "Lanthanum");
		POLLUTANT_LOOKUP.put("Ce", "Cerium");
		POLLUTANT_LOOKUP.put("Pr", "Praseodymium");
		POLLUTANT_LOOKUP.put("Nd", "Neodymium");
		POLLUTANT_LOOKUP.put("Pm", "Promethium");
		POLLUTANT_LOOKUP.put("Sm", "Samarium");
		POLLUTANT_LOOKUP.put("Eu", "Europium");
		POLLUTANT_LOOKUP.put("Gd", "Gadolinium");
		POLLUTANT_LOOKUP.put("Tb", "Terbium");
		POLLUTANT_LOOKUP.put("Dy", "Dysprosium");
		POLLUTANT_LOOKUP.put("Ho", "Holmium");
		POLLUTANT_LOOKUP.put("Er", "Erbium");
		POLLUTANT_LOOKUP.put("Tm", "Thulium");
		POLLUTANT_LOOKUP.put("Yb", "Ytterbium");
		POLLUTANT_LOOKUP.put("Lu", "Lutetium");
		POLLUTANT_LOOKUP.put("Hf", "Hafnium");
		POLLUTANT_LOOKUP.put("Ta", "Tantalum");
		POLLUTANT_LOOKUP.put("W", "Tungsten");
		POLLUTANT_LOOKUP.put("Re", "Rhenium");
		POLLUTANT_LOOKUP.put("Os", "Osmium");
		POLLUTANT_LOOKUP.put("Ir", "Iridium");
		POLLUTANT_LOOKUP.put("Pt", "Platinum");
		POLLUTANT_LOOKUP.put("Au", "Gold");
		POLLUTANT_LOOKUP.put("Hg", "Mercury");
		POLLUTANT_LOOKUP.put("Tl", "Thallium");
		POLLUTANT_LOOKUP.put("Pb", "Lead");
		POLLUTANT_LOOKUP.put("Bi", "Bismuth");
		POLLUTANT_LOOKUP.put("Po", "Polonium");
		POLLUTANT_LOOKUP.put("At", "Astatine");
		POLLUTANT_LOOKUP.put("Rn", "Radon");
		POLLUTANT_LOOKUP.put("Fr", "Francium");
		POLLUTANT_LOOKUP.put("Ra", "Radium");
		POLLUTANT_LOOKUP.put("Ac", "Actinium");
		POLLUTANT_LOOKUP.put("Th", "Thorium");
		POLLUTANT_LOOKUP.put("Pa", "Protactinium");
		POLLUTANT_LOOKUP.put("U", "Uranium");
		POLLUTANT_LOOKUP.put("Np", "Neptunium");
		POLLUTANT_LOOKUP.put("Pu", "Plutonium");
		POLLUTANT_LOOKUP.put("Am", "Americium");
		POLLUTANT_LOOKUP.put("Cm", "Curium");
		POLLUTANT_LOOKUP.put("Bk", "Berkelium");
		POLLUTANT_LOOKUP.put("Cf", "Californium");
		POLLUTANT_LOOKUP.put("Es", "Einsteinium");
		POLLUTANT_LOOKUP.put("Fm", "Fermium");
		POLLUTANT_LOOKUP.put("Md", "Mendelevium");
		POLLUTANT_LOOKUP.put("No", "Nobelium");
		POLLUTANT_LOOKUP.put("Lr", "Lawrencium");
		POLLUTANT_LOOKUP.put("Rf", "Rutherfordium");
		POLLUTANT_LOOKUP.put("Db", "Dubnium");
		POLLUTANT_LOOKUP.put("Sg", "Seaborgium");
		POLLUTANT_LOOKUP.put("Bh", "Bohrium");
		POLLUTANT_LOOKUP.put("Hs", "Hassium");
		POLLUTANT_LOOKUP.put("Mt", "Meitnerium");
		POLLUTANT_LOOKUP.put("Ds", "Darmstadtium");
		POLLUTANT_LOOKUP.put("Rg", "Roentgenium");
		POLLUTANT_LOOKUP.put("Cn", "Copernicium");
		POLLUTANT_LOOKUP.put("Nh", "Nihonium");
		POLLUTANT_LOOKUP.put("Fl", "Flerovium");
		POLLUTANT_LOOKUP.put("Mc", "Moscovium");
		POLLUTANT_LOOKUP.put("Lv", "Livermorium");
		POLLUTANT_LOOKUP.put("Ts", "Tennessine");
		POLLUTANT_LOOKUP.put("Og", "Oganesson");
		POLLUTANT_LOOKUP.put("Sulfur", "Sulphur");
		POLLUTANT_LOOKUP.put("SO2", "Sulphur Dioxide");
		POLLUTANT_LOOKUP.put("SO4", "Sulphate");
		POLLUTANT_LOOKUP.put("NO3", "Nitrate");
		POLLUTANT_LOOKUP.put("NH4", "Ammonium");
		POLLUTANT_LOOKUP.put("NH4+", "Ammonium");
		POLLUTANT_LOOKUP.put("Na+", "Sodium");
		POLLUTANT_LOOKUP.put("Cl-", "Chloride");
		POLLUTANT_LOOKUP.put("AMMONIUM", "Ammonium");
		POLLUTANT_LOOKUP.put("AMMONIA", "Ammonia");
		POLLUTANT_LOOKUP.put("SULPHATE", "Sulphate");
		POLLUTANT_LOOKUP.put("NITRIC ACID", "Nitric Acid");
		POLLUTANT_LOOKUP.put("NITRATE", "Nitrate");
		POLLUTANT_LOOKUP.put("MethaneSulfonic Acid", "Methanesulphonic Acid");
		POLLUTANT_LOOKUP.put("MSA", "Methanesulphonic Acid");
		POLLUTANT_LOOKUP.put("Chrysene&Triphenylene", "Chrysene & Triphenylene");
		POLLUTANT_LOOKUP.put("Chrysene & Triphenylene (C&T)", "Chrysene & Triphenylene");
		POLLUTANT_LOOKUP.put("b-Pinene", "B-Pinene");
		POLLUTANT_LOOKUP.put("a-Pinene", "A-Pinene");
		POLLUTANT_LOOKUP.put("13c12-TCDD", "13C12-TCDD");
		POLLUTANT_LOOKUP.put("3-Me-ChOLanthrene", "3-Me-Cholanthrene");
		POLLUTANT_LOOKUP.put("3-Methyl-1-butene", "3-Methyl-1-Butene");
		POLLUTANT_LOOKUP.put("2-Methyl-1-butene", "2-Methyl-1-Butene");
		POLLUTANT_LOOKUP.put("2-Methyl-2-butene", "2-Methyl-2-Butene");
		POLLUTANT_LOOKUP.put("3-Methyl-1-pentene", "3-Methyl-1-Pentene");
		POLLUTANT_LOOKUP.put("4-Methyl-1-pentene", "4-Methyl-1-Pentene");
		POLLUTANT_LOOKUP.put("cis-3-Methyl-2-pentene", "cis-3-Methyl-2-Pentene");
		POLLUTANT_LOOKUP.put("d-Limonene", "D-Limonene");
		POLLUTANT_LOOKUP.put("Iso-Propylbenzene", "iso-Propylbenzene");
		POLLUTANT_LOOKUP.put("trans-3-Methyl-2-pentene", "trans-3-Methyl-2-Pentene");
		POLLUTANT_LOOKUP.put("trans-4-Methyl-2-pentene", "trans-4-Methyl-2-Pentene");
		POLLUTANT_LOOKUP.put("Freon11", "Freon 11");
		POLLUTANT_LOOKUP.put("Freon113", "Freon 113");
		POLLUTANT_LOOKUP.put("Freon114", "Freon 114");
		POLLUTANT_LOOKUP.put("Freon12", "Freon 12");
		POLLUTANT_LOOKUP.put("Freon22", "Freon 22");
		POLLUTANT_LOOKUP.put("PM25", "PM2.5");
	}
	
	public static String lookupPollutantName(String pollutantName) {
		return POLLUTANT_LOOKUP.getOrDefault(pollutantName, pollutantName);
	}
}
