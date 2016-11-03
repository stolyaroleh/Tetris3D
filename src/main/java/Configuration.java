import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Configuration {
    private static HashMap<Integer, List<Pair>> structures = new HashMap<Integer, List<Pair>>();
    private static HashMap<Integer, Color> colors = new HashMap<Integer, Color>();
    private static HashMap<Integer, Integer> orientations = new HashMap<Integer, Integer>();

    static {
        //cube
        structures.put(1, Arrays.asList(new Pair(0, -1), new Pair(0, 0), new Pair(1, 0), new Pair(1, -1)));
        colors.put(1, Color.GREEN);
        orientations.put(1, 0);
        //line
        structures.put(2, Arrays.asList(new Pair(0, 1), new Pair(0, 0), new Pair(0, 2), new Pair(0, -1)));
        colors.put(2, Color.CYAN);
        orientations.put(2, 1);
        //s
        structures.put(3, Arrays.asList(new Pair(0, 0), new Pair(1, 0), new Pair(0, -1), new Pair(-1, -1)));
        colors.put(3, Color.YELLOW);
        orientations.put(3, 1);
        //z
        structures.put(4, Arrays.asList(new Pair(0, 0), new Pair(1, 0), new Pair(0, 1), new Pair(-1, 1)));
        colors.put(4, new Color(111, 0, 255));    //violet
        orientations.put(4, 1);
        //t-shape
        structures.put(5, Arrays.asList(new Pair(0, 0), new Pair(-1, 0), new Pair(1, 0), new Pair(0, -1)));
        colors.put(5, Color.ORANGE);
        orientations.put(5, 3);
        //L
        structures.put(6, Arrays.asList(new Pair(0, 0), new Pair(-1, 0), new Pair(1, 0), new Pair(-1, -1)));
        colors.put(6, Color.BLUE);
        orientations.put(6, 3);
        //L reversed
        structures.put(7, Arrays.asList(new Pair(0, 0), new Pair(-1, 0), new Pair(1, 0), new Pair(1, -1)));
        colors.put(7, Color.RED);
        orientations.put(7, 3);
        //small l-shape
        structures.put(8, Arrays.asList(new Pair(0, 0), new Pair(0, 1), new Pair(0, -1)));
        colors.put(8, Color.GRAY);
        orientations.put(8, 3);
        //small green additional shape (really, how do you call this thing?)
        structures.put(9, Arrays.asList(new Pair(0, 0), new Pair(1, 0), new Pair(-1, -1)));
        colors.put(9, Color.GREEN);
        orientations.put(9, 3);
        //small diagonal line
        structures.put(10, Arrays.asList(new Pair(0, 0), new Pair(1, 1)));
        colors.put(10, Color.ORANGE);
        orientations.put(10, 1);
        //even smaller line
        structures.put(11, Arrays.asList(new Pair(0, 0), new Pair(0, 1)));
        colors.put(11, Color.magenta);
        orientations.put(11, 1);
        //t-shape without center
        structures.put(12, Arrays.asList(new Pair(-1, 0), new Pair(1, 0), new Pair(0, -1)));
        colors.put(12, Color.WHITE);
        orientations.put(12, 3);
        //simple square
        structures.put(13, Arrays.asList(new Pair(0, 0)));
        colors.put(13, Color.DARK_GRAY);
        orientations.put(13, 0);
        //diagonal line
        structures.put(14, Arrays.asList(new Pair(0, 0), new Pair(1, 1), new Pair(-1, -1)));
        colors.put(14, Color.CYAN);
        orientations.put(14, 1);
        //cross
        structures.put(15, Arrays.asList(new Pair(0, 0), new Pair(0, 1), new Pair(-1, 0), new Pair(1, 0), new Pair(0, -1)));
        colors.put(15, Color.YELLOW);
        orientations.put(15, 0);
    }

    public static Color getColor(int fragmentID) {
        return colors.get(fragmentID);
    }

    public static List<Pair> getStructure(int fragmentID) {
        return structures.get(fragmentID);
    }

    public static int getMaxOrientation(int fragmentID) {
        return orientations.get(fragmentID);
    }

}
