package com.dbf.naps.data.analysis.heatmap;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class HeatMapGradient {
	

	private static final Color[] EXTENDED_GRADIENT = new Color[] {
			Color.decode("#3288bd"), // Blue
			Color.decode("#65c1a5"),
			Color.decode("#91d5a6"),
			Color.decode("#c9e89a"), 
			Color.decode("#eaf79b"),
			Color.decode("#fcffba"),
			Color.decode("#fffbba"), 
			Color.decode("#fee492"),
			Color.decode("#fdc771"),
			Color.decode("#fda159"), 
			Color.decode("#f46c43"), 
			Color.decode("#d53e4f")  //Red
	};
	
	private static final Color[] BASIC_GRADIENT = new Color[] {
			Color.decode("#1d4877"), //Dark Blue
			Color.decode("#1b8a5a"), //Green
			Color.decode("#fbb021"), //Golden Yellow
			Color.decode("#f68838"), //Orange
			Color.decode("#ee3e32")  //Red
	};
	
	private static final Color[] TWO_COLOUR_GRADIENT = new Color[] {
			Color.decode("#0000FF"), //Blue
			Color.decode("#FF0000"), //Red
	};
	
	
	private static final Color[] COLOUR_BLIND_GRADIENT = new Color[] {
			Color.decode("#e4ff7a"), //Light Greenish Yellow
			Color.decode("#ffe81a"), //Bright Yellow
			Color.decode("#ffbd00"), //Deep Yellow
			Color.decode("#ffa000"), //Orange
			Color.decode("#fc7f00")  //Darker Orange
	};
	
	private static final Color[] BLACK_RED_ORANGE_GRADIENT = new Color[] {
			Color.decode("#000000"), //Black
			Color.decode("#8e060a"), //Red
			Color.decode("#fda32b")  //Orange
	};
	
	private static final Color[] WHITE_HOT_GRADIENT = new Color[] {
			Color.decode("#000000"), //Black
			Color.decode("#8e060a"), //Red
			Color.decode("#fda32b"), //Orange
			Color.decode("#FFCF9F"), //Light Orange
			Color.decode("#FEF9FF")  //Whitish-blue
	};
	
	private static final Color[] GREY_GRADIENT = new Color[] {
			Color.decode("#E3E3E3"), //Grey
			Color.decode("#000000"), //Black
	};
	
	
	private static final List<Color[]> GRADIENTS = new ArrayList<Color[]>();
	
	static {
		GRADIENTS.add(EXTENDED_GRADIENT);
		GRADIENTS.add(BASIC_GRADIENT);
		GRADIENTS.add(TWO_COLOUR_GRADIENT);
		GRADIENTS.add(COLOUR_BLIND_GRADIENT);
		GRADIENTS.add(BLACK_RED_ORANGE_GRADIENT);
		GRADIENTS.add(WHITE_HOT_GRADIENT);
		GRADIENTS.add(GREY_GRADIENT);
	}
	
	public static int getGradientCount() {
		return GRADIENTS.size() + 1;
	}
	
	public static Color getColour(double value, int gradient) {
       if(gradient == 1) {
    	   return getColourForValueSmooth(value);
       } else {
    	   return getColourForValueStops(value, GRADIENTS.get(gradient-2));
       }
    }
	
	private static Color getColourForValueSmooth(double value) {
		if(value > 1.0 || value < 0.0) {
			throw new RuntimeException("Unexpect value: " + value);
		}
        float hue = (float) ((1.0 - value) * 240 / 360);  // Blue to red hue
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }
	
	public static Color getColourForValueStops(double value, Color[] stops) {
		if(value > 1.0 || value < 0.0) {
			throw new RuntimeException("Unexpect value: " + value);
		}
        //Determine between which colours we sit
        double scaledPosition = value * (stops.length - 1);
        int stopIndex = (int) Math.floor(scaledPosition);
        if (stopIndex == stops.length -1) {
        	return stops[stopIndex];
        }
        Color colour1 = stops[stopIndex];
        Color colour2 = stops[stopIndex + 1];

        //Linearly interpolate between the two stops
        double stopFraction = scaledPosition - stopIndex;
        int r = (int) (colour1.getRed() * (1 - stopFraction) + colour2.getRed() * stopFraction);
        int g = (int) (colour1.getGreen() * (1 - stopFraction) + colour2.getGreen() * stopFraction);
        int b = (int) (colour1.getBlue() * (1 - stopFraction) + colour2.getBlue() * stopFraction);
        return new Color(r, g, b);
    }
}
