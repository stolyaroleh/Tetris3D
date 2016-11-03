import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Configuration {
    public static class Shape {
        public List<Pos> structure;
        public Color color;
        public int maxOrientation;
    }

    private static HashMap<Integer, Shape> shapes = new HashMap<>();

    static {
        Shape cube = new Shape();
        cube.structure = Arrays.asList(new Pos(0, -1), new Pos(0, 0), new Pos(1, 0), new Pos(1, -1));
        cube.color = Color.GREEN;
        cube.maxOrientation = 0;
        shapes.put(1, cube);

        Shape line = new Shape();
        line.structure = Arrays.asList(new Pos(0, 1), new Pos(0, 0), new Pos(0, 2), new Pos(0, -1));
        line.color = Color.CYAN;
        line.maxOrientation = 1;
        shapes.put(2, line);

        Shape s = new Shape();
        s.structure = Arrays.asList(new Pos(0, 0), new Pos(1, 0), new Pos(0, -1), new Pos(-1, -1));
        s.color = Color.YELLOW;
        s.maxOrientation = 1;
        shapes.put(3, s);

        Shape z = new Shape();
        z.structure = Arrays.asList(new Pos(0, 0), new Pos(1, 0), new Pos(0, 1), new Pos(-1, 1));
        z.color = new Color(111, 0, 255); // purple
        z.maxOrientation = 1;
        shapes.put(4, z);

        Shape T = new Shape();
        T.structure = Arrays.asList(new Pos(0, 0), new Pos(-1, 0), new Pos(1, 0), new Pos(0, -1));
        T.color = Color.ORANGE;
        T.maxOrientation = 3;
        shapes.put(5, T);

        Shape L = new Shape();
        L.structure = Arrays.asList(new Pos(0, 0), new Pos(-1, 0), new Pos(1, 0), new Pos(-1, -1));
        L.color = Color.BLUE;
        L.maxOrientation = 3;
        shapes.put(6, L);

        Shape reverseL = new Shape();
        reverseL.structure = Arrays.asList(new Pos(0, 0), new Pos(-1, 0), new Pos(1, 0), new Pos(1, -1));
        reverseL.color = Color.RED;
        reverseL.maxOrientation = 3;
        shapes.put(7, reverseL);

        Shape smallL = new Shape();
        smallL.structure = Arrays.asList(new Pos(0, 0), new Pos(0, 1), new Pos(0, -1));
        smallL.color = Color.GRAY;
        smallL.maxOrientation = 3;
        shapes.put(8, smallL);

        // small green additional shape (really, how do you call this thing?)
        Shape weirdGreenShape = new Shape();
        weirdGreenShape.structure = Arrays.asList(new Pos(0, 0), new Pos(1, 0), new Pos(-1, -1));
        weirdGreenShape.color = Color.GREEN;
        weirdGreenShape.maxOrientation = 3;
        shapes.put(9, weirdGreenShape);

        Shape smallDiagonalLine = new Shape();
        smallDiagonalLine.structure = Arrays.asList(new Pos(0, 0), new Pos(1, 1));
        smallDiagonalLine.color = Color.ORANGE;
        smallDiagonalLine.maxOrientation = 1;
        shapes.put(10, smallDiagonalLine);

        Shape smallLine = new Shape();
        smallLine.structure = Arrays.asList(new Pos(0, 0), new Pos(0, 1));
        smallLine.color = Color.magenta;
        smallLine.maxOrientation = 1;
        shapes.put(11, smallLine);

        // T-shape without center
        Shape holeyT = new Shape();
        holeyT.structure = Arrays.asList(new Pos(-1, 0), new Pos(1, 0), new Pos(0, -1));
        holeyT.color = Color.WHITE;
        holeyT.maxOrientation = 3;
        shapes.put(12, holeyT);

        Shape pixel = new Shape();
        pixel.structure = Arrays.asList(new Pos(0, 0));
        pixel.color = Color.DARK_GRAY;
        pixel.maxOrientation = 0;
        shapes.put(13, pixel);

        Shape diagonal = new Shape();
        diagonal.structure = Arrays.asList(new Pos(0, 0), new Pos(1, 1), new Pos(-1, -1));
        diagonal.color = Color.CYAN;
        diagonal.maxOrientation = 1;
        shapes.put(14, diagonal);

        Shape cross = new Shape();
        cross.structure = Arrays.asList(new Pos(0, 0), new Pos(0, 1), new Pos(-1, 0), new Pos(1, 0), new Pos(0, -1));
        cross.color = Color.YELLOW;
        cross.maxOrientation = 0;
        shapes.put(15, cross);
    }

    public static Shape getShape(int fragmentID) {
        return shapes.get(fragmentID);
    }
}
