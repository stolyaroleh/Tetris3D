import java.awt.*;
import java.util.List;

class Fragment extends Object {

    public static final int SQUARE_FIGURE = 1;
    public static final int LINE_FIGURE = 2;
    public static final int S_FIGURE = 3;
    public static final int Z_FIGURE = 4;
    public static final int RIGHT_ANGLE_FIGURE = 5;
    public static final int LEFT_ANGLE_FIGURE = 6;
    public static final int TRIANGLE_FIGURE = 7;
    private int type;
    public List<Pair> structure;
    public Color color;
    public int orientation = 0;
    public int maxOrientation = 3;

    public Fragment(int type) throws IllegalArgumentException {
        initialize(type);
        this.type = type;
    }

    public boolean containsOffset(Pair p) {
        for (Pair t : structure) {
            if (p.x == t.x && p.y == t.y) {
                //System.out.println("Contains offset.");
                return true;
            }
        }
        return false;
    }

    private void initialize(int type) throws IllegalArgumentException {
        structure = Configuration.getStructure(type);
        color = Configuration.getColor(type);
        maxOrientation = Configuration.getMaxOrientation(type);
    }

    public int getType() {
        return type;
    }

}