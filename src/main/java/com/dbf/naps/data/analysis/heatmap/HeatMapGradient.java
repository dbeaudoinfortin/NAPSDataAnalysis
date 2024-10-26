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
	
	private static final Color[] CUBEHELIX_GRADIENT = new Color[] {
			Color.decode("#000000"),
			Color.decode("#090309"),
			Color.decode("#100614"),
			Color.decode("#160a1f"),
			Color.decode("#190f2b"),
			Color.decode("#1a1536"),
			Color.decode("#1a1c3f"),
			Color.decode("#182448"),
			Color.decode("#152d4e"),
			Color.decode("#123752"),
			Color.decode("#104153"),
			Color.decode("#0e4b53"),
			Color.decode("#0d544f"),
			Color.decode("#0e5d4b"),
			Color.decode("#126644"),
			Color.decode("#176d3d"),
			Color.decode("#207336"),
			Color.decode("#2a782f"),
			Color.decode("#387b29"),
			Color.decode("#477d25"),
			Color.decode("#577d23"),
			Color.decode("#697d24"),
			Color.decode("#7b7c28"),
			Color.decode("#8d7a2f"),
			Color.decode("#9e7938"),
			Color.decode("#ae7745"),
			Color.decode("#bd7654"),
			Color.decode("#c87564"),
			Color.decode("#d27677"),
			Color.decode("#d9788a"),
			Color.decode("#dd7b9d"),
			Color.decode("#de80af"),
			Color.decode("#de86c1"),
			Color.decode("#db8dd1"),
			Color.decode("#d795de"),
			Color.decode("#d29fe9"),
			Color.decode("#cca9f1"),
			Color.decode("#c7b3f7"),
			Color.decode("#c3bdfa"),
			Color.decode("#c0c8fb"),
			Color.decode("#bed1fa"),
			Color.decode("#bfdaf8"),
			Color.decode("#c2e2f6"),
			Color.decode("#c7e9f3"),
			Color.decode("#cdeff1"),
			Color.decode("#d6f3f0"),
			Color.decode("#e0f7f0"),
			Color.decode("#eafaf3"),
			Color.decode("#f5fdf8"),
			Color.decode("#ffffff")
	};
	
	
	private static final List<Color[]> GRADIENTS = new ArrayList<Color[]>();
	
	static {
		GRADIENTS.add(EXTENDED_GRADIENT);
		GRADIENTS.add(BASIC_GRADIENT);
		GRADIENTS.add(TWO_COLOUR_GRADIENT);
		GRADIENTS.add(COLOUR_BLIND_GRADIENT);
		GRADIENTS.add(BLACK_RED_ORANGE_GRADIENT);
		GRADIENTS.add(WHITE_HOT_GRADIENT);
		GRADIENTS.add(CUBEHELIX_GRADIENT);
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
