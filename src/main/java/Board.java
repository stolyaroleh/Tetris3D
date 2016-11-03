import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

class Board extends JPanel {
    public Fragment current;
    public boolean falling = true;

    public int dimX;
    public int dimY;

    public Color[][] colors;
    public static Color backgroundColor;
    private boolean[][] isEmpty;
    private BufferedImage image;

    public Pair currentPos;
    private final Cell cell;
    public boolean rotateReverse;
    public boolean gameOver = false;

    public Board(int dimX, int dimY, int cellSize, Color backgroundColor) {
        this.dimX = dimX;
        this.dimY = dimY + 4;        // four invisible rows to make some room in order for components descend from the ceiling gradually
        this.colors = new Color[this.dimX][this.dimY];
        this.isEmpty = new boolean[this.dimX][this.dimY];
        fillBooleanArray(isEmpty, true);
        Board.backgroundColor = backgroundColor;
        this.cell = new Cell(cellSize, cellSize);
        Cell.sizeX = Cell.sizeY = cellSize;
    }

    boolean enoughSpaceForMove(List<Pair> offsetList, int move) {
        if (current == null) return false;
        int x = currentPos.x;
        int y = currentPos.y;
        if (move != 0 && !falling) return false;
        for (Pair offset : offsetList) {
            if (!isWithinBoard(new Pair(offset.x + x + move, offset.y + y))) return false;
            if (!isEmpty[offset.x + x + move][offset.y + y] && !current.containsOffset(new Pair(offset.x + move, offset.y))) {
                //System.out.println("Collision.");
                return false;
            }
        }
        return true;
    }

    private boolean isWithinBoard(Pair coordinates) {
        return ((coordinates.x) >= 0 && (coordinates.x) < dimX) && ((coordinates.y) >= 0 && (coordinates.y) < dimY);
    }

    boolean enoughSpaceForRotation(List<Pair> rotatedStructure) {
        //System.out.println("EnoughSpaceForRotation called.");
        //deleteFragment(currentPos.x, currentPos.y);
        for (Pair rot : rotatedStructure) {
            if (!isWithinBoard(new Pair(currentPos.x + rot.x, currentPos.y + rot.y))) {
                return false;
            } else {
                if (!isEmpty[currentPos.x + rot.x][currentPos.y + rot.y]) {
                    return false;
                }
            }
        }
        //System.out.println("True returned.");
        return true;
    }


    boolean drawFragment(int x, int y) {
        if (current == null) return false;
        if (isWithinBoard(new Pair(x, y))) currentPos = new Pair(x, y);
        if (!enoughSpaceForMove(current.structure, 0)) {
            //System.out.println("Not enough space to move in draw");
            return false;
        }
        //System.out.println("Drawing a fragment");
        for (Pair p : current.structure) {
            colors[x + p.x][y + p.y] = current.color;
            isEmpty[x + p.x][y + p.y] = false;
            //isUpdated[x+p.x][y+p.y]=false;
        }
        update();
        return true;
    }

    void deleteFragment(int x, int y) {
        if (current == null || !enoughSpaceForMove(current.structure, 0)) return;
        //System.out.println("Deleting a fragment");
        for (Pair p : current.structure) {
            colors[x + p.x][y + p.y] = backgroundColor;
            isEmpty[x + p.x][y + p.y] = true;
            //isUpdated[x+p.x][y+p.y]=false;
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
        //System.out.println("Update called.");
        image = new BufferedImage(Cell.sizeX * getBoardWidth() + 1, Cell.sizeY * getBoardHeight() + 1,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < dimX; x++) {
            for (int y = 4; y < dimY; y++) {
                int xPos = getDrawPosX(x);
                int yPos = getDrawPosY(y - 4);
                if (!isEmpty[x][y]) {
                    //System.out.println("Drawing at: " + xPos + ", " + yPos);
                    image = cell.paintAt(xPos, yPos, colors[x][y], image);
                } else {
                    //System.out.println("Erasing at: " + xPos + ", " + yPos);
                    image = cell.paintAt(xPos, yPos, backgroundColor, image);
                }
                //isUpdated[x][y] = true;
            }
        }
        //repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        //System.out.println("Board paint called.");
        g.drawImage(image, 0, 0, this);
    }

    public void timerTick() {
        update();
        if (falling) {
            checkIfTimeToStop();
            if (!falling) return;
            if (currentPos != null) {
                deleteFragment(currentPos.x, currentPos.y);
            } else currentPos = new Pair(dimX / 2 - 1, 1);
            currentPos.y = currentPos.y + 1;
            drawFragment(currentPos.x, currentPos.y);
        }
    }

    public void setBoardSizeAndMoveTo(int x, int y) {
        setBounds(x, y, Cell.sizeX * getBoardWidth() + 1, Cell.sizeY * getBoardHeight() + 1);
    }

    public void checkIfTimeToStop() {
        if (current == null) return;
        //if (currentPos.y<3) return;
        for (Pair p : current.structure) {
            if (p.y + currentPos.y == dimY - 1) {
                falling = false;
                return;
            }
        }
        for (Pair p : current.structure) {
            if (isWithinBoard(new Pair(currentPos.x + p.x, currentPos.y + p.y))) {
                if (!isEmpty[p.x + currentPos.x][p.y + currentPos.y + 1]) {
                    if (!current.containsOffset(new Pair(p.x, p.y + 1))) {
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

    //90 degrees clockwise
    public void rotateR() {
        if (!falling || currentPos == null || !isWithinBoard(currentPos) || current.maxOrientation == 0) return;
        Fragment rotated = new Fragment(current.getType());
        rotated.orientation = current.orientation;
        deleteFragment(currentPos.x, currentPos.y);
        for (Pair p : rotated.structure) {
            int temp = -p.x;
            p.x = p.y;
            p.y = temp;
        }
        if (enoughSpaceForRotation(rotated.structure)) {
            current.structure = new ArrayList<Pair>(rotated.structure);
            current.orientation = (current.orientation + 1) % (current.maxOrientation + 1);
            drawFragment(currentPos.x, currentPos.y);
        } else {
            for (Pair p : rotated.structure) {
                int temp = -p.y;
                p.y = p.x;
                p.x = temp;
            }
            drawFragment(currentPos.x, currentPos.y);
        }
        update();
    }

    //90 degrees anti-clockwise
    public void rotateL() {
        if (!falling || currentPos == null || !isWithinBoard(currentPos) || current.maxOrientation == 0) return;
        Fragment rotated = current;
        deleteFragment(currentPos.x, currentPos.y);
        for (Pair p : rotated.structure) {
            int temp = -p.y;
            p.y = p.x;
            p.x = temp;
        }
        if (enoughSpaceForRotation(rotated.structure)) {
            deleteFragment(currentPos.x, currentPos.y);
            current = rotated;
            current.orientation = (current.orientation - 1) % (current.maxOrientation + 1);
            drawFragment(currentPos.x, currentPos.y);
        } else {
            for (Pair p : rotated.structure) {
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
        //update();
        return linesToRemove;
    }

    public boolean isActiveFragment(int x, int y) {
        return current.containsOffset(new Pair(x - currentPos.x, y - currentPos.y + 4));
    }

    public boolean tryToReplaceCurrent(int randIndex) {
        Fragment different = new Fragment(randIndex);
        Fragment currentFragment = this.current;
        if (currentFragment != null) deleteFragment(currentPos.x, currentPos.y);
        current = different;
        if (enoughSpaceForMove(current.structure, 0)) {
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
                //this.isUpdated[x][y] = false;
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
        Pair newPos = new Pair(currentPos.x + i, currentPos.y);
        if (enoughSpaceForMove(current.structure, i)) {
            deleteFragment(currentPos.x, currentPos.y);
            currentPos = newPos;
            drawFragment(currentPos.x, currentPos.y);
        }
        update();
    }

    public boolean isEmpty(Pair c) {
        if (isWithinBoard(c)) {
            return isEmpty[c.x][c.y];
        } else
            return true;
    }
}