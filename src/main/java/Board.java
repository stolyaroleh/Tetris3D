import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

class Board extends JPanel {
    Fragment current;
    boolean falling = true;

    int dimX;
    int dimY;

    Color[][] colors;
    static Color backgroundColor;
    private boolean[][] isEmpty;
    private BufferedImage image;

    Pos currentPos;
    private final Cell cell;
    boolean gameOver = false;

    Board(int dimX, int dimY, int cellSize, Color backgroundColor) {
        this.dimX = dimX;
        this.dimY = dimY + 4; // four invisible rows to make some room in order for components descend from the ceiling gradually
        this.colors = new Color[this.dimX][this.dimY];
        this.isEmpty = new boolean[this.dimX][this.dimY];
        fillBooleanArray(isEmpty, true);
        Board.backgroundColor = backgroundColor;
        this.cell = new Cell(cellSize, cellSize);
        Cell.sizeX = Cell.sizeY = cellSize;
    }

    private boolean enoughSpaceForMove(List<Pos> offsetList, int move) {
        if (current == null) return false;
        int x = currentPos.x;
        int y = currentPos.y;
        if (move != 0 && !falling) return false;
        for (Pos offset : offsetList) {
            if (!isWithinBoard(new Pos(offset.x + x + move, offset.y + y))) return false;
            if (!isEmpty[offset.x + x + move][offset.y + y] && !current.containsOffset(new Pos(offset.x + move, offset.y))) {
                return false;
            }
        }
        return true;
    }

    private boolean isWithinBoard(Pos coordinates) {
        return ((coordinates.x) >= 0 && (coordinates.x) < dimX) && ((coordinates.y) >= 0 && (coordinates.y) < dimY);
    }

    private boolean enoughSpaceFor(List<Pos> structure) {
        for (Pos offset : structure) {
            Pos absolute = new Pos(currentPos.x + offset.x, currentPos.y + offset.y);
            if (!isWithinBoard(absolute) || !isEmpty(absolute)) return false;
        }
        return true;
    }


    boolean drawFragment(int x, int y) {
        if (current == null) return false;
        if (isWithinBoard(new Pos(x, y))) currentPos = new Pos(x, y);
        if (!enoughSpaceForMove(current.shape.structure, 0)) {
            return false;
        }
        for (Pos p : current.shape.structure) {
            colors[x + p.x][y + p.y] = current.shape.color;
            isEmpty[x + p.x][y + p.y] = false;
        }
        update();
        return true;
    }

    void deleteFragment(int x, int y) {
        if (current == null || !enoughSpaceForMove(current.shape.structure, 0)) return;
        for (Pos p : current.shape.structure) {
            colors[x + p.x][y + p.y] = backgroundColor;
            isEmpty[x + p.x][y + p.y] = true;
        }
        update();
    }

    void fillBooleanArray(boolean[][] array, boolean value) {
        for (int x = 0; x < array.length; x++) {
            for (int y = 0; y < array[x].length; y++) {
                array[x][y] = value;
            }
        }
    }

    void update() {
        image = new BufferedImage(Cell.sizeX * getBoardWidth() + 1,
                Cell.sizeY * getBoardHeight() + 1,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < dimX; x++) {
            for (int y = 4; y < dimY; y++) {
                int xPos = getDrawPosX(x);
                int yPos = getDrawPosY(y - 4);
                if (!isEmpty[x][y]) {
                    image = cell.paintAt(xPos, yPos, colors[x][y], image);
                } else {
                    image = cell.paintAt(xPos, yPos, backgroundColor, image);
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }

    public void timerTick() {
        update();
        if (falling) {
            checkIfTimeToStop();
            if (!falling) return;
            if (currentPos != null) {
                deleteFragment(currentPos.x, currentPos.y);
            } else currentPos = new Pos(dimX / 2 - 1, 1);
            currentPos.y = currentPos.y + 1;
            drawFragment(currentPos.x, currentPos.y);
        }
    }

    public void setBoardSizeAndMoveTo(int x, int y) {
        setBounds(x, y, Cell.sizeX * getBoardWidth() + 1, Cell.sizeY * getBoardHeight() + 1);
    }

    public void checkIfTimeToStop() {
        if (current == null) return;
        for (Pos p : current.shape.structure) {
            if (p.y + currentPos.y == dimY - 1) {
                falling = false;
                return;
            }
        }
        for (Pos p : current.shape.structure) {
            if (isWithinBoard(new Pos(currentPos.x + p.x, currentPos.y + p.y))) {
                if (!isEmpty[p.x + currentPos.x][p.y + currentPos.y + 1]) {
                    if (!current.containsOffset(new Pos(p.x, p.y + 1))) {
                        falling = false;
                        if (currentPos.y <= 3) {
                            gameOver = true;
                        }
                        return;
                    }
                }
            }
        }
        falling = true;
    }

    // 90 degrees clockwise
    public void rotateR() {
        if (!falling || currentPos == null || !isWithinBoard(currentPos) || current.shape.maxOrientation == 0) return;
        Fragment rotated = new Fragment(current.id);
        rotated.orientation = current.orientation;
        deleteFragment(currentPos.x, currentPos.y);
        for (Pos p : rotated.shape.structure) {
            int temp = -p.x;
            p.x = p.y;
            p.y = temp;
        }
        if (enoughSpaceFor(rotated.shape.structure)) {
            current.shape.structure = new ArrayList<>(rotated.shape.structure);
            current.orientation = (current.orientation + 1) % (current.shape.maxOrientation + 1);
            drawFragment(currentPos.x, currentPos.y);
        } else {
            for (Pos p : rotated.shape.structure) {
                int temp = -p.y;
                p.y = p.x;
                p.x = temp;
            }
            drawFragment(currentPos.x, currentPos.y);
        }
        update();
    }

    // 90 degrees anti-clockwise
    public void rotateL() {
        if (!falling || currentPos == null || !isWithinBoard(currentPos) || current.shape.maxOrientation == 0) return;
        Fragment rotated = current;
        deleteFragment(currentPos.x, currentPos.y);
        for (Pos p : rotated.shape.structure) {
            int temp = -p.y;
            p.y = p.x;
            p.x = temp;
        }
        if (enoughSpaceFor(rotated.shape.structure)) {
            deleteFragment(currentPos.x, currentPos.y);
            current = rotated;
            current.orientation = (current.orientation - 1) % (current.shape.maxOrientation + 1);
            drawFragment(currentPos.x, currentPos.y);
        } else {
            for (Pos p : rotated.shape.structure) {
                int temp = -p.x;
                p.x = p.y;
                p.y = temp;
            }
            drawFragment(currentPos.x, currentPos.y);
        }
        update();
    }

    public int removeFullLines(int startFrom) {
        if (falling) return 0;
        int line = startFrom;
        boolean flag = true;
        int linesToRemove = 0;
        while (line >= 0) {
            for (int x = 0; x < dimX; x++) {
                if (isEmpty[x][line]) flag = false;
            }
            if (flag) {
                linesToRemove++;
                linesToRemove += removeFullLines(line - 1);
                for (int y = line; y >= linesToRemove; y--) {
                    for (int x = 0; x < dimX; x++) {
                        colors[x][y] = colors[x][y - linesToRemove];
                        isEmpty[x][y] = isEmpty[x][y - linesToRemove];
                    }
                }
                break;
            }
            line--;
            flag = true;
        }
        if (linesToRemove == 0) return 0;
        falling = false;
        return linesToRemove;
    }

    public boolean tryToReplaceCurrent(int randIndex) {
        Fragment different = new Fragment(randIndex);
        Fragment currentFragment = this.current;
        if (currentFragment != null) deleteFragment(currentPos.x, currentPos.y);
        current = different;
        if (enoughSpaceForMove(current.shape.structure, 0)) {
            drawFragment(currentPos.x, currentPos.y);
            return true;
        } else {
            current = currentFragment;
            return false;
        }
    }


    public int getBoardHeight() {
        return dimY - 4;
    }

    public int getBoardWidth() {
        return dimX;
    }

    public void clear() {
        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                this.isEmpty[x][y] = true;
            }
        }
        update();
    }

    private int getDrawPosX(int x) {
        int panelWidth = this.getWidth();
        return (panelWidth / dimX) * x;
    }

    private int getDrawPosY(int y) {
        int panelHeight = this.getHeight();
        return (panelHeight / (dimY - 4) * (y));
    }

    public void move(int i) {
        if (current == null) return;
        Pos newPos = new Pos(currentPos.x + i, currentPos.y);
        if (enoughSpaceForMove(current.shape.structure, i)) {
            deleteFragment(currentPos.x, currentPos.y);
            currentPos = newPos;
            drawFragment(currentPos.x, currentPos.y);
        }
        update();
    }

    public boolean isEmpty(Pos c) {
        if (isWithinBoard(c)) {
            return isEmpty[c.x][c.y];
        } else
            return true;
    }
}