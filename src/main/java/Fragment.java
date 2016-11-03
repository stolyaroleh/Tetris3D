class Fragment extends Object {
    public static final int SQUARE_FIGURE = 1;
    public static final int LINE_FIGURE = 2;
    public static final int S_FIGURE = 3;
    public static final int Z_FIGURE = 4;
    public static final int RIGHT_ANGLE_FIGURE = 5;
    public static final int LEFT_ANGLE_FIGURE = 6;
    public static final int TRIANGLE_FIGURE = 7;

    public final Configuration.Shape shape;
    public final int id;
    public int orientation = 0;

    public Fragment(int id) throws IllegalArgumentException {
        this.id = id;
        shape = Configuration.getShape(id);
    }

    public boolean containsOffset(Pos o) {
        for (Pos cell : shape.structure) {
            if (o.x == cell.x && o.y == cell.y) return true;
        }
        return false;
    }
}