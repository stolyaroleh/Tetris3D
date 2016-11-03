import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.awt.event.KeyEvent.VK_W;

public class Game {
    static int cellSize = 20;
    static int gameX = 10;
    static int gameY = 3;
    static int gameZ = 20;
    private static boolean playing = false, fastForward = false, showingOptions = false, paused = false;

    private static int removedLines = 0, currentLevelRemovedLines = 0;
    private static int score = 0;
    private static int level = 1;
    private static JFrame frame, options;
    private static Board3D board3d;
    private static Obj3D mesh;
    private static JButton pauseButton;
    private static Font labelFont = new Font("Sans Serif", Font.BOLD, 14);
    private static JLabel levelLabel, scoreLabel, lineLabel;
    private static Board board, smallBoard;
    private static Random random = new Random();
    private static JSlider sliderM, sliderN, sliderS, sliderSize, sliderGameX, sliderGameZ, sliderGameY;
    private static List<Integer> fragmentsInGame;

    private static Fragment current, next;
    private static Timer gameTimer;
    private static int N = 20, M = 1;
    private static double S = 0.2;
    private static KeyListener keyListener = new KeyListener() {
        //moving backward/forward
        @Override
        public void keyPressed(KeyEvent event) {
            if (!paused) {
                if (event.getKeyCode() == VK_W || event.getKeyCode() == KeyEvent.VK_S) {
                    int nextBoardIndex = (Board3D.currentBoardIndex + (event.getKeyCode() == VK_W ? 1 : -1) + gameY) % gameY;
                    board3d.getGameBoard(nextBoardIndex).currentPos = board.currentPos;
                    if (board3d.getGameBoard(nextBoardIndex).tryToReplaceCurrent(current.id)) {
                        board.deleteFragment(board.currentPos.x, board.currentPos.y);
                        board.current = null;
                        board.falling = false;
                        frame.remove(board);
                        board = board3d.getGameBoard(nextBoardIndex);
                        frame.add(board);
                        board.setBoardSizeAndMoveTo(40, 40);
                        board.falling = true;
                        Board3D.currentBoardIndex = nextBoardIndex;
                        board3d.updateMesh();
                        board3d.repaint();
                    } else {
                        board3d.getGameBoard(nextBoardIndex).currentPos = null;
                    }
                }
                if (event.getKeyCode() == KeyEvent.VK_A) {
                    board.move(-1);
                    board3d.updateMesh();
                    board3d.repaint();
                }

                if (event.getKeyCode() == KeyEvent.VK_D) {
                    board.move(1);
                    board3d.updateMesh();
                    board3d.repaint();
                }
                if (event.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (playing && !fastForward) {
                        fastForward = true;
                        gameTimer.setDelay(gameTimer.getDelay() / 20);
                    }
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent arg0) {
        }

        @Override
        public void keyTyped(KeyEvent arg0) {
        }
    };
    private static MenuItem eyeUp, eyeDown, eyeLeft, eyeRight, incrDist, decrDist;

    static {
        fragmentsInGame = new ArrayList<>();
        for (int i = 1; i < 8; i++)
            fragmentsInGame.add(i);
    }

    private static void initialize() {
        Font buttonFont = new Font("Sans Serif", Font.PLAIN, cellSize - 7);
        frame = new JFrame("Tetris 3D");
        JButton quitButton = new JButton("QUIT");
        pauseButton = new JButton("PAUSE");
        JButton optionsButton = new JButton("OPTIONS");
        levelLabel = new JLabel("Level: " + level);
        scoreLabel = new JLabel("Score: " + score);
        lineLabel = new JLabel("Lines: " + removedLines);

        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        board = new Board(gameX, gameZ, cellSize, Color.LIGHT_GRAY);
        frame.setVisible(true);


        pauseButton.setFocusPainted(false);
        pauseButton.setMargin(new Insets(0, 0, 0, 0));
        pauseButton.setContentAreaFilled(false);
        pauseButton.setBorderPainted(true);
        pauseButton.setBorder(BorderFactory.createLineBorder(Color.blue));
        pauseButton.setOpaque(true);
        pauseButton.setFont(buttonFont);
        pauseButton.setForeground(Color.blue);
        pauseButton.setBounds((board.getBoardWidth() * cellSize / 2) - 50, (board.getBoardHeight() * cellSize / 2), 100, 2 * cellSize);
        pauseButton.setVisible(false);

        quitButton.setBounds(80 + board.getBoardWidth() * cellSize, board.getBoardHeight() * cellSize + 40 - 2 * cellSize, 5 * cellSize, 2 * cellSize);
        quitButton.setBackground(new Color(230, 230, 230));
        quitButton.setFont(buttonFont);
        quitButton.addActionListener(e -> System.exit(0));

        optionsButton.setBounds(80 + board.getBoardWidth() * cellSize, board.getBoardHeight() * cellSize + 30 - 4
                * cellSize, 5 * cellSize, 2 * cellSize);
        optionsButton.setBackground(new Color(230, 230, 230));
        optionsButton.setFont(buttonFont);
        optionsButton.addActionListener(e -> {
            if (options == null || !options.isVisible()) {
                showOptions();
            } else {
                options.dispatchEvent(new WindowEvent(options, WindowEvent.WINDOW_CLOSING));
            }
        });

        smallBoard = new Board(5, 5, cellSize, Color.LIGHT_GRAY);
        smallBoard.setBoardSizeAndMoveTo(80 + board.getBoardWidth() * cellSize, 40);
        frame.add(smallBoard);

        levelLabel.setBounds(80 + board.getBoardWidth() * cellSize, 5 * cellSize + 45, 100, 40);
        scoreLabel.setBounds(80 + board.getBoardWidth() * cellSize - 2, 5 * cellSize + 75, 100, 40);
        lineLabel.setBounds(80 + board.getBoardWidth() * cellSize, 5 * cellSize + 105, 100, 40);

        levelLabel.setFont(labelFont);
        scoreLabel.setFont(labelFont);
        lineLabel.setFont(labelFont);

        frame.add(lineLabel);
        frame.add(scoreLabel);
        frame.add(levelLabel);
        frame.add(quitButton);
        frame.add(optionsButton);
        frame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (SwingUtilities.isLeftMouseButton(event)) {
                    board.move(-1);
                    board3d.updateMesh();
                    board3d.repaint();
                }
                if (SwingUtilities.isRightMouseButton(event)) {
                    board.move(1);
                    board3d.updateMesh();
                    board3d.repaint();
                }
                if (SwingUtilities.isMiddleMouseButton(event)) {
                    if (playing && !fastForward) {
                        fastForward = true;
                        gameTimer.setDelay(gameTimer.getDelay() / 20);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent event) {
            }

            @Override
            public void mouseExited(MouseEvent event) {
            }

            @Override
            public void mousePressed(MouseEvent event) {
            }

            @Override
            public void mouseReleased(MouseEvent event) {
            }
        });
        frame.addMouseWheelListener(event -> {
            if (event.getPreciseWheelRotation() > 0) {
                if (board.current.shape.maxOrientation == 1) {
                    if (board.current.orientation == 0)
                        board.rotateR();
                    else
                        board.rotateL();
                } else
                    board.rotateR();
                board3d.updateMesh();
                board3d.repaint();
            } else if (event.getPreciseWheelRotation() < 0) {
                if (board.current.shape.maxOrientation == 1) {
                    if (board.current.orientation == 0)
                        board.rotateR();
                    else
                        board.rotateL();
                } else
                    board.rotateL();
                board3d.updateMesh();
                board3d.repaint();
            }

        });

        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                if (!showingOptions)
                    System.exit(0);
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowOpened(WindowEvent e) {
            }
        });

        MenuBar mBar = new MenuBar();
        frame.setMenuBar(mBar);
        Menu mV = new Menu("View");
        mBar.add(mV);

        eyeDown = new MenuItem("Viewpoint Down", new MenuShortcut(KeyEvent.VK_DOWN));
        eyeUp = new MenuItem("Viewpoint Up", new MenuShortcut(KeyEvent.VK_UP));
        eyeLeft = new MenuItem("Viewpoint to Left", new MenuShortcut(KeyEvent.VK_LEFT));
        eyeRight = new MenuItem("Viewpoint to Right", new MenuShortcut(KeyEvent.VK_RIGHT));

        incrDist = new MenuItem("Increase viewing distance", new MenuShortcut(KeyEvent.VK_INSERT));
        decrDist = new MenuItem("Decrease viewing distance", new MenuShortcut(KeyEvent.VK_DELETE));
        mV.add(eyeDown);
        mV.add(eyeUp);
        mV.add(eyeLeft);
        mV.add(eyeRight);
        mV.add(incrDist);
        mV.add(decrDist);
        MenuCommands mListener = new MenuCommands();

        eyeDown.addActionListener(mListener);
        eyeUp.addActionListener(mListener);
        eyeLeft.addActionListener(mListener);
        eyeRight.addActionListener(mListener);
        incrDist.addActionListener(mListener);
        decrDist.addActionListener(mListener);

        board3d = new Board3D(board);
        mesh = new Obj3D();
        board3d.setObj(mesh);
        frame.add(board3d);
        board3d.setBackground(Color.DARK_GRAY);
        board3d.setBoardSizeAndMoveTo(40, 40);
        frame.add(pauseButton);
        pauseButton.setBounds(80 + board.getBoardWidth() * cellSize, board.getBoardHeight() * cellSize + 20 - 6
                * cellSize, 5 * cellSize, 2 * cellSize);
        int verticalSize = board.getBoardHeight() * cellSize + 140;
        frame.setSize((board.getBoardWidth() + smallBoard.getBoardWidth()) * cellSize + 135, verticalSize);
        showingOptions = false;
        board.update();
        smallBoard.update();
        gameTimer = new Timer(800, new timerListener());
        generateFragment();

        playing = true;

        gameTimer.start();
        board3d.addKeyListener(keyListener);
        board3d.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                if (playing) {
                    pauseButton.setVisible(true);
                    paused = true;
                    try {
                        gameTimer.stop();
                    } catch (Exception ignored) {
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                if (playing) {
                    pauseButton.setVisible(false);
                    paused = false;
                    try {
                        gameTimer.start();
                    } catch (Exception ignored) {
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent arg0) {
            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
            }
        });
        frame.setFocusable(false);
        board3d.requestFocus();
    }

    public static void main(String[] args) {
        initialize();
        mesh.vp(board3d, -1.5f, 0, 0); // setting the initial viewing angle.
    }

    private static void showOptions() {
        showingOptions = true;
        sliderSize = new JSlider(JSlider.HORIZONTAL, 15, 30, cellSize);
        sliderM = new JSlider(JSlider.HORIZONTAL, 1, 10, M);
        sliderN = new JSlider(JSlider.HORIZONTAL, 20, 50, N);
        sliderGameX = new JSlider(JSlider.HORIZONTAL, 5, 50, gameX);
        sliderGameZ = new JSlider(JSlider.HORIZONTAL, 15, 50, gameZ);
        sliderGameY = new JSlider(JSlider.HORIZONTAL, 1, 10, gameY);
        sliderS = new JSlider(JSlider.HORIZONTAL, 1, 10, (int) (S * 10));
        options = new JFrame("GameOptions");
        options.setLayout(null);
        JPanel sliders = new JPanel();
        sliders.setLayout(new BoxLayout(sliders, BoxLayout.Y_AXIS));
        JLabel labelM = new JLabel("Scoring factor: " + M);
        JLabel labelN = new JLabel("Rows needed to go up a level: " + N);
        JLabel labelS = new JLabel("Speed factor: " + S);
        JLabel labelSize = new JLabel("Size: " + cellSize);
        JLabel labelGameX = new JLabel("Columns: " + gameX);
        JLabel labelGameZ = new JLabel("Rows: " + gameZ);
        JLabel labelGameY = new JLabel("Depth: " + gameY);
        sliders.add(labelM);
        sliders.add(sliderM);
        sliderM.addChangeListener(event -> {
            M = sliderM.getValue();
            labelM.setText("Scoring factor: " + M);
        });
        sliders.add(labelN);
        sliders.add(sliderN);
        sliderN.addChangeListener(event -> {
            N = sliderN.getValue();
            labelN.setText("Rows needed to go up a level: " + N);
        });
        sliders.add(labelS);
        sliders.add(sliderS);
        sliderS.addChangeListener(event -> {
            S = sliderS.getValue() / 10.0;
            labelS.setText("Speed factor: " + S);
        });

        sliders.add(labelSize);
        sliders.add(sliderSize);
        sliderSize.addChangeListener(event -> {
            cellSize = sliderSize.getValue();
            labelSize.setText("Size: " + cellSize);
        });

        sliders.add(labelGameX);
        sliders.add(sliderGameX);
        sliderGameX.addChangeListener(event -> {
            gameX = sliderGameX.getValue();
            labelGameX.setText("Columns: " + gameX);
        });

        sliders.add(labelGameZ);
        sliders.add(sliderGameZ);
        sliderGameZ.addChangeListener(event -> {
            gameZ = sliderGameZ.getValue();
            labelGameZ.setText("Rows: " + gameZ);
        });

        sliders.add(labelGameY);
        sliders.add(sliderGameY);
        sliderGameY.addChangeListener(event -> {
            gameY = sliderGameY.getValue();
            labelGameY.setText("Depth: " + gameY);
        });

        JLabel fragmentsLabel = new JLabel("Additional shapes: ");
        options.add(fragmentsLabel);
        fragmentsLabel.setBounds(86, 280, cellSize * 20 + 200, 25);
        JPanel firstFragmentRow = new JPanel();
        firstFragmentRow.setLayout(null);
        firstFragmentRow.add(createBoardWithFragment(8, 40, 0));
        firstFragmentRow.add(createBoardWithFragment(9, cellSize * 5 + 80, 0));
        firstFragmentRow.add(createBoardWithFragment(10, cellSize * 10 + 120, 0));
        firstFragmentRow.add(createBoardWithFragment(11, cellSize * 15 + 160, 0));
        JPanel firstCheckboxRow = new JPanel();
        firstCheckboxRow.setLayout(null);
        firstCheckboxRow.add(createCheckboxForFragment(8, 20, 0));
        firstCheckboxRow.add(createCheckboxForFragment(9, cellSize * 5 + 60, 0));
        firstCheckboxRow.add(createCheckboxForFragment(10, cellSize * 10 + 100, 0));
        firstCheckboxRow.add(createCheckboxForFragment(11, cellSize * 15 + 140, 0));

        JPanel secondCheckboxRow = new JPanel();
        secondCheckboxRow.setLayout(null);
        secondCheckboxRow.add(createCheckboxForFragment(12, 20, 0));
        secondCheckboxRow.add(createCheckboxForFragment(13, cellSize * 5 + 60, 0));
        secondCheckboxRow.add(createCheckboxForFragment(14, cellSize * 10 + 100, 0));
        secondCheckboxRow.add(createCheckboxForFragment(15, cellSize * 15 + 140, 0));

        options.add(sliders);
        sliders.setBounds(40, 40, cellSize * 20 + 120, 240);
        options.add(firstFragmentRow);
        firstFragmentRow.setBounds(0, 310, cellSize * 20 + 200, cellSize * 5 + 1);
        firstFragmentRow.setOpaque(false);
        options.add(firstCheckboxRow);
        firstCheckboxRow.setBounds(0, 310, cellSize * 20 + 200, 15);
        JPanel secondFragmentRow = new JPanel();
        secondFragmentRow.setLayout(null);
        secondFragmentRow.add(createBoardWithFragment(12, 40, 0));
        secondFragmentRow.add(createBoardWithFragment(13, cellSize * 5 + 80, 0));
        secondFragmentRow.add(createBoardWithFragment(14, cellSize * 10 + 120, 0));
        secondFragmentRow.add(createBoardWithFragment(15, cellSize * 15 + 160, 0));

        options.add(secondFragmentRow);
        secondFragmentRow.setBounds(0, 310 + cellSize * 5, cellSize * 20 + 200, cellSize * 5 + 1);
        secondFragmentRow.setOpaque(false);
        options.add(secondCheckboxRow);
        secondCheckboxRow.setBounds(0, 310 + cellSize * 5, cellSize * 20 + 200, 15);
        options.setVisible(true);
        options.setSize(cellSize * 20 + 220, 10 * cellSize + 400);
        options.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        options.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent arg0) {

                frame.setVisible(false);
                frame = null;
                initialize();
                gameTimer.start();
                playing = true;
            }

            @Override
            public void windowActivated(WindowEvent arg0) {
                playing = false;
                gameTimer.stop();
                pauseButton.setVisible(true);
            }

            @Override
            public void windowClosed(WindowEvent arg0) {
            }

            @Override
            public void windowDeactivated(WindowEvent arg0) {
            }

            @Override
            public void windowDeiconified(WindowEvent arg0) {
            }

            @Override
            public void windowIconified(WindowEvent arg0) {
            }

            @Override
            public void windowOpened(WindowEvent arg0) {
            }
        });
    }

    private static void generateFragment() {
        if (next == null) {
            // System.out.println("next is null.");
            int randIndex = random.nextInt(fragmentsInGame.size());
            next = new Fragment(fragmentsInGame.get(randIndex));
            randIndex = random.nextInt(fragmentsInGame.size());
            current = new Fragment(fragmentsInGame.get(randIndex));
        } else {
            // System.out.println("current is updated, next is generated.");
            current = next;
            int randIndex = random.nextInt(fragmentsInGame.size());
            next = new Fragment(fragmentsInGame.get(randIndex));
        }
        board.current = current;
        board.falling = true;
        board.drawFragment(board.dimX / 2 - 1, 1);
        smallBoard.clear();
        smallBoard.current = next;
        smallBoard.drawFragment(2, 6);
    }

    private static JCheckBox createCheckboxForFragment(int fragmentIndex, int x, int y) {
        JCheckBox checkbox = new JCheckBox("");
        checkbox.setBounds(x, y, 17, 15);
        if (fragmentsInGame.contains(fragmentIndex))
            checkbox.setSelected(true);
        else
            checkbox.setSelected(false);
        checkbox.addActionListener(arg0 -> {
            if (checkbox.isSelected()) {
                fragmentsInGame.add(fragmentIndex);
            } else {
                fragmentsInGame.remove(new Integer(fragmentIndex));
            }
        });
        return checkbox;
    }

    private static Board createBoardWithFragment(int fragmentIndex, int x, int y) {
        Board board = new Board(5, 5, cellSize, Color.LIGHT_GRAY);
        board.current = new Fragment(fragmentIndex);
        board.setBoardSizeAndMoveTo(x, y);
        board.drawFragment(2, 6);
        return board;
    }

    private static void viewpoint(float dTheta, float dPhi, float fRho) {
        Obj3D obj = board3d.getObj();
        if (obj == null || !obj.vp(board3d, dTheta, dPhi, fRho))
            Toolkit.getDefaultToolkit().beep();
    }

    private static class timerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            board3d.updateMesh();
            board3d.repaint();
            if (board.gameOver) {
                JOptionPane.showMessageDialog(null, "Game Over! Score: " + score);
                System.exit(0);
                return;
            }
            if (!board.falling && fastForward) {
                gameTimer.setDelay(gameTimer.getDelay() * 20);
                fastForward = false;
            }
            if (!board.falling)
                generateFragment();
            board.timerTick();
            int removed = board.removeFullLines(board.dimY - 1);
            if (removed != 0) {
                currentLevelRemovedLines += removed;
                currentLevelRemovedLines += removed;
                if (currentLevelRemovedLines >= N) {
                    level++;
                    levelLabel.setText("Level: " + level);
                    gameTimer.setDelay((int) (gameTimer.getDelay() * (1 / (1 + level * S))));
                    currentLevelRemovedLines -= N;
                }
                removedLines += removed;
                score += (int) ((10 * level * M) * (Math.pow(1.5, removed)));
                lineLabel.setText("Lines: " + removedLines);
                scoreLabel.setText("Score: " + score);
            }
        }
    }

    private static class MenuCommands implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            if (ae.getSource() instanceof MenuItem) {
                MenuItem mi = (MenuItem) ae.getSource();
                if (mi == eyeDown)
                    viewpoint(0, .1F, 1);
                else if (mi == eyeUp)
                    viewpoint(0, -.1F, 1);
                else if (mi == eyeLeft)
                    viewpoint(-.1F, 0, 1);
                else if (mi == eyeRight)
                    viewpoint(.1F, 0, 1);
                else if (mi == incrDist)
                    viewpoint(0, 0, 2);
                else if (mi == decrDist)
                    viewpoint(0, 0, .5F);
            }
        }
    }
}
