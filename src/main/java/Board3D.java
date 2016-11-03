import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Board3D extends CvZBuf {
    private Board[] gameBoards;
    private Color bgColor;
    public static float offset;
    public static int indexV = 1, currentBoardIndex = 0;
    public static List<Color> colorArray;
    public static double lightX, lightY, lightZ, lightMaxDist;

    public Board3D(Board firstBoard) {
        bgColor = Board.backgroundColor;
        gameBoards = new Board[Game.gameY];
        gameBoards[0] = firstBoard;
        for (int i = 1; i < Game.gameY; i++) {
            gameBoards[i] = new Board(Game.gameX, Game.gameZ, Game.cellSize, bgColor);
        }
        currentBoardIndex = 0;
    }

    private void drawBoundingBox() {
        colorArray = new ArrayList<Color>();
        colorArray.add(bgColor.darker());
        colorArray.add(bgColor.darker());
        colorArray.add(bgColor.darker());
        colorArray.add(bgColor.darker());
        colorArray.add(bgColor.darker());
        colorArray.add(bgColor.darker());
        offset = 2f;
        float h, d, w; // height, depth, width, or z, y, x
        h = (gameBoards[0].getBoardHeight() + 1) * Cell.sizeX + 2 * offset;
        w = gameBoards[0].getBoardWidth() * Cell.sizeX + 2 * offset;
        d = Game.gameY * Cell.sizeX + 2 * offset;
        lightZ = h;
        lightY = d;
        lightX = 0;
        lightMaxDist = Math.sqrt(h * h + w * w + d * d);
        Obj3D mesh = getObj();
        mesh.addVertex(1, 0, d, 0);
        mesh.addVertex(2, 0, 0, 0);
        mesh.addVertex(3, w, 0, 0);
        mesh.addVertex(4, w, d, 0);
        mesh.addVertex(5, 0, d, h);
        mesh.addVertex(6, 0, 0, h);
        mesh.addVertex(7, w, 0, h);
        mesh.addVertex(8, w, d, h);
        Vector<Integer> v1 = new Vector<Integer>();
        v1.add(1);
        v1.add(4);
        v1.add(8);
        v1.add(5);
        mesh.addPolygon(v1);
        Vector<Integer> v2 = new Vector<Integer>();
        v2.add(4);
        v2.add(3);
        v2.add(7);
        v2.add(8);
        mesh.addPolygon(v2);
        Vector<Integer> v3 = new Vector<Integer>();
        v3.add(3);
        v3.add(2);
        v3.add(6);
        v3.add(7);
        mesh.addPolygon(v3);
        Vector<Integer> v4 = new Vector<Integer>();
        v4.add(2);
        v4.add(1);
        v4.add(5);
        v4.add(6);
        mesh.addPolygon(v4);
        Vector<Integer> v5 = new Vector<Integer>();
        v5.add(8);
        v5.add(7);
        v5.add(6);
        v5.add(5);
        mesh.addPolygon(v5);
        Vector<Integer> v6 = new Vector<Integer>();
        v6.add(4);
        v6.add(1);
        v6.add(2);
        v6.add(3);
        mesh.addPolygon(v6);
        indexV = 9;
    }

    public void updateMesh() {
        setObj(new Obj3D());
        drawBoundingBox();
        int xDim = gameBoards[0].getBoardWidth();
        int zDim = gameBoards[0].getBoardHeight();
        int yDim = gameBoards.length;
        float height, depth, width;
        for (int d = 0; d < yDim; d++) {
            for (int x = 0; x < xDim; x++) {
                for (int z = 0; z < zDim + 2; z++) {
                    height = (float) (offset + Cell.sizeX * (z - 0.5));
                    width = (float) (offset + Cell.sizeX * (x + 0.5));
                    depth = (float) (offset + Cell.sizeX * (d + 0.5));
                    if (!gameBoards[d].isEmpty(new Pair(x, zDim + 4 - z))) {
                        Cell.addVertexDataToObj(indexV, width, depth, height, getObj());
                        Cell.addPolyData(indexV, getObj());
                        //calculate shading
                        double deductionCoeff = ((Math.sqrt(height * height + width * width + depth * depth) / lightMaxDist) + 1) / 2;
                        Color originalColor = gameBoards[d].colors[x][zDim + 4 - z];
                        originalColor = new Color((int) (originalColor.getRed() * deductionCoeff), (int) (originalColor.getGreen() * deductionCoeff), (int) (originalColor.getBlue() * deductionCoeff));
                        Cell.addColors(colorArray, originalColor);
                        indexV += 8;
                    }
                }
            }
        }
        getObj().shiftToOrigin();
    }

    public void setBoardSizeAndMoveTo(int x, int y) {
        setBounds(x, y, Cell.sizeX * Game.gameX, Cell.sizeY * Game.gameZ);
    }

    public Board getGameBoard(int index) {
        return gameBoards[index];
    }

    public void setGameBoard(Board gameBoard, int index) {
        this.gameBoards[index] = gameBoard;
    }
}
