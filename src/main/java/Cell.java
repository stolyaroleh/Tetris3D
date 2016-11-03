import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Vector;

public class Cell extends JComponent {
    public static int sizeX, sizeY;
    public static float offset3D;

    public Cell(int sizeX, int sizeY) {
        Cell.sizeX = sizeX;
        Cell.sizeY = sizeY;
        offset3D = sizeX / 10;
    }

    public boolean isDoubleBuffered() {
        return true;
    }

    public BufferedImage paintAt(int x, int y, Color color, BufferedImage i) {
        Graphics g = i.getGraphics();
        g.setColor(color);
        g.fillRect(x, y, sizeX, sizeY);
        g.setColor(color.darker());
        g.drawRect(x, y, sizeX, sizeY);
        return i;
    }

    public static String generateVertexData(int startIndex, float x, float y, float z) {
        String output = "";
        // x, y and z define the center of a cube being drawn. size depends on
        // sizeX and offset3D (1/10 sizeX by default)
        float s = (sizeX - offset3D) / 2;
        float[] vx = new float[8];
        float[] vy = new float[8];
        float[] vz = new float[8];
        vx[0] = vx[1] = vx[4] = vx[5] = x - s;
        vx[2] = vx[3] = vx[6] = vx[7] = x + s;
        vy[0] = vy[3] = vy[4] = vy[7] = y - s;
        vy[1] = vy[2] = vy[5] = vy[6] = y + s;
        vz[0] = vz[1] = vz[2] = vz[3] = z - s;
        vz[4] = vz[5] = vz[6] = vz[7] = z + s;
        for (int i = startIndex; i < startIndex + 8; i++) {
            output += Integer.toString(i) + " " + Double.toString(vx[i - startIndex]) + " "
                    + Double.toString(vy[i - startIndex]) + " " + Double.toString(vz[i - startIndex]) + "\n";
        }
        return output;
    }

    public static void addVertexDataToObj(int startIndex, float x, float y, float z, Obj3D obj) {
        float s = (sizeX - offset3D) / 2;
        float[] vx = new float[8];
        float[] vy = new float[8];
        float[] vz = new float[8];
        vx[0] = vx[1] = vx[4] = vx[5] = x - s;
        vx[2] = vx[3] = vx[6] = vx[7] = x + s;
        vy[0] = vy[3] = vy[4] = vy[7] = y - s;
        vy[1] = vy[2] = vy[5] = vy[6] = y + s;
        vz[0] = vz[1] = vz[2] = vz[3] = z - s;
        vz[4] = vz[5] = vz[6] = vz[7] = z + s;
        for (int i = startIndex; i < startIndex + 8; i++) {
            obj.addVertex(i, vx[i - startIndex], vy[i - startIndex], vz[i - startIndex]);
        }
    }

    public static void addPolyData(int startIndex, Obj3D obj) {
        Vector<Integer> v1 = new Vector<Integer>();
        v1.add(startIndex + 0);
        v1.add(startIndex + 3);
        v1.add(startIndex + 7);
        v1.add(startIndex + 4);
        obj.addPolygon(v1);
        Vector<Integer> v2 = new Vector<Integer>();
        v2.add(startIndex + 3);
        v2.add(startIndex + 2);
        v2.add(startIndex + 6);
        v2.add(startIndex + 7);
        obj.addPolygon(v2);
        Vector<Integer> v3 = new Vector<Integer>();
        v3.add(startIndex + 2);
        v3.add(startIndex + 1);
        v3.add(startIndex + 5);
        v3.add(startIndex + 6);
        obj.addPolygon(v3);
        Vector<Integer> v4 = new Vector<Integer>();
        v4.add(startIndex + 1);
        v4.add(startIndex + 0);
        v4.add(startIndex + 4);
        v4.add(startIndex + 5);
        obj.addPolygon(v4);
        Vector<Integer> v5 = new Vector<Integer>();
        v5.add(startIndex + 7);
        v5.add(startIndex + 6);
        v5.add(startIndex + 5);
        v5.add(startIndex + 4);
        obj.addPolygon(v5);
        Vector<Integer> v6 = new Vector<Integer>();
        v6.add(startIndex + 0);
        v6.add(startIndex + 1);
        v6.add(startIndex + 2);
        v6.add(startIndex + 3);
        obj.addPolygon(v6);
    }

    public static String generatePolyData(int startIndex) {
        String output = "";
        output += Integer.toString(startIndex + 0) + " " + Integer.toString(startIndex + 3) + " " + Integer.toString(startIndex + 7) + " " + Integer.toString(startIndex + 4) + ".\n";
        output += Integer.toString(startIndex + 3) + " " + Integer.toString(startIndex + 2) + " " + Integer.toString(startIndex + 6) + " " + Integer.toString(startIndex + 7) + ".\n";
        output += Integer.toString(startIndex + 2) + " " + Integer.toString(startIndex + 1) + " " + Integer.toString(startIndex + 5) + " " + Integer.toString(startIndex + 6) + ".\n";
        output += Integer.toString(startIndex + 1) + " " + Integer.toString(startIndex + 0) + " " + Integer.toString(startIndex + 4) + " " + Integer.toString(startIndex + 5) + ".\n";
        output += Integer.toString(startIndex + 7) + " " + Integer.toString(startIndex + 6) + " " + Integer.toString(startIndex + 5) + " " + Integer.toString(startIndex + 4) + ".\n";
        output += Integer.toString(startIndex + 0) + " " + Integer.toString(startIndex + 1) + " " + Integer.toString(startIndex + 2) + " " + Integer.toString(startIndex + 3) + ".\n";
        return output;
    }

    public static void addColors(List<Color> colors, Color color) {
        // 6 faces per cube, each cube has the same colour
        colors.add(color);
        colors.add(color);
        colors.add(color);
        colors.add(color);
        colors.add(color);
        colors.add(color);
    }
}
