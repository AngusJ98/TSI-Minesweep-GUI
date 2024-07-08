package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Minesweeper {
    private Tile[][] tiles;
    private int xSize;
    private int ySize;
    private int numBombs;
    private JFrame frame;
    private int moveNumber;
    private AudioPlayer audioPlayer;
    private final ActionListener tileActionListener = actionEvent -> {
        Object origin = actionEvent.getSource();
        Tile tile = (Tile) origin;
        if (!tile.isFlagged()) {
            if (this.moveNumber == 0) {
                this.firstMove(tile.getX(), tile.getY());
            }
            this.activateTileAtCoord(tile.getX(), tile.getY());
        }
        //System.out.println(tile.getX() + "-" + tile.getY());
    };


    public Minesweeper (int xSize, int ySize, int numBombs) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.moveNumber = 0;
        this.numBombs = numBombs;

        this.setUpFrame();
        this.setUpMenu();
        this.audioPlayer = new AudioPlayer();

        this.cleanUp();
        this.setupTiles();
        this.setupBombs();
        this.frame.setVisible(true);


    }

    private void setUpFrame() {
        this.frame = new JFrame();

        this.frame.setSize(1000,1000);
        this.frame.setResizable(false);
        this.frame.setLayout(new BorderLayout());
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setUpMenu() {
        JPanel menuPanel = new JPanel();
        JButton restart = new JButton("Restart");
        restart.addActionListener(actionEvent -> {
            this.cleanUp();
            this.setupBombs();
        });

        JButton changeDifficulty = new JButton("Change Difficulty");
        changeDifficulty.addActionListener(actionEvent ->
                this.changeDifficulty());

        JButton pauseMusic = new JButton("Music");
        pauseMusic.addActionListener(actionEvent -> this.audioPlayer.pause());

        menuPanel.add(restart);
        menuPanel.add(changeDifficulty);
        menuPanel.add(pauseMusic);
        this.frame.add(menuPanel, BorderLayout.NORTH);

    }

    private void cleanUp() {
        this.moveNumber = 0;
    }

    private void setupTiles() {
        this.tiles = new Tile[xSize][ySize];

        Container grid = new Container();
        grid.setLayout(new GridLayout(xSize, ySize));
        for (int i = 0; i < this.xSize; i++) {
            for (int j = 0; j < this.ySize; j++) {
                Tile tile = new Tile(i, j, this.tileActionListener);
                this.tiles[i][j] = tile;
                grid.add(tile);
            }
        }
        this.frame.add(grid);
    }


    private void setupBombs() {

        //Clear the board first
        for (int i = 0; i < this.xSize; i++) {
            for (int j = 0; j < this.ySize; j++) {
                this.tiles[i][j].resetTile();
            }
        }
        //randomly generate locations for mines
        HashSet<Tile> tileHash = new HashSet<Tile>();
        Random random = new Random();
        while (tileHash.size() < this.numBombs) {
            int xRandom = random.nextInt(this.xSize);
            int yRandom = random.nextInt(this.ySize);
            Tile testTile = this.tiles[xRandom][yRandom];
            tileHash.add(testTile);
        }

        for (Tile t : tileHash) {
            t.setBomb(true);
            for (Tile t2 : this.getAdjacentTiles(t)){
                t2.incrementBombCount();
            }
        }
    }



    private void activateTileAtCoord(int x, int y) {
        Tile tile = tiles[x][y];
        this.moveNumber++;
        if (tile.isBomb()) {
            tile.reveal();
            this.audioPlayer.playBombNoise();
            this.retryDialog("Oops, looks like you clicked on a mine. Would you like to try again?");

        } else if (tile.getNumBombsAdjacent() == 0) {
            tile.reveal();
            //then check adjacent tiles
            for (Tile t : this.getAdjacentTiles(tile)) {
                if (!t.isRevealed()) {
                    this.activateTileAtCoord(t.getX(), t.getY());
                }
            }

        } else {
            tile.reveal();
        }

        if (checkWin()) {
            this.retryDialog("Congratulations on your victory! Would you like to try again?");
        }
    }

    public void retryDialog(String message) {
        int option = (JOptionPane.showOptionDialog(this.frame, message, "Retry?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Yes", "Change Difficulty", "Quit"}, "No"));

        switch (option) {
            case 0:
                this.cleanUp();
                this.setupBombs();
                break;
            case 1:
                //joption pane to select new settings
                this.changeDifficulty();
                break;
            case 2:
                System.exit(0);
        }
    }

    private boolean checkWin() {
        for (int i = 0; i < this.xSize; i++) {
            for (int j = 0; j < this.ySize; j++) {
                Tile t = this.tiles[i][j];
                if (!t.isRevealed() && !t.isBomb()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void changeDifficulty() {
        String[] options = {"Easy","Normal","Hard","Very Hard","ISTQB difficulty"};
        String difficulty = (String)JOptionPane.showInputDialog(this.frame, "Choose a difficulty", "Difficulty Settings", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        System.out.println(difficulty);

        switch (difficulty) {
            case "Easy":
                this.xSize = 10;
                this.ySize = 10;
                this.numBombs = 7;
                break;
            case "Normal":
                this.xSize = 10;
                this.ySize = 10;
                this.numBombs = 10;
                break;
            case "Hard":
                this.xSize = 15;
                this.ySize = 15;
                this.numBombs = 20;
                break;
            case "Very Hard":
                this.xSize = 20;
                this.ySize = 20;
                this.numBombs = 40;
                break;
            case "ISTQB difficulty":
                this.xSize = 8;
                this.ySize = 5;
                this.numBombs = 26;
                break;
            default:
                //Should be impossible to get here but...
                JOptionPane.showMessageDialog(frame, "Please choose one of the valid options.");
        }
        this.frame.dispose();

        this.setUpFrame();
        this.setUpMenu();
        this.cleanUp();
        this.setupTiles();

        this.setupBombs();
        this.frame.setVisible(true);
    }

    //Regenerate map on first move until the player has clicked a space with no bombs
    private void firstMove(int x, int y) {
        Tile tile = this.tiles[x][y];
        while (tile.getNumBombsAdjacent() != 0 || tile.isBomb()) {
            //System.out.println("Regenerating");
            this.setupBombs();
            tile = this.tiles[x][y];
        }
    }

    private ArrayList<Tile> getAdjacentTiles (Tile tile) {
        ArrayList<Tile> tileList = new ArrayList<Tile>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int newX = tile.getX() + dx;
                int newY = tile.getY() + dy;
                if (newX >= 0 && newX < this.xSize && newY >= 0 && newY < this.ySize) {
                    //System.out.println("Success " + newX + "-" + newY);
                    tileList.add(this.tiles[newX][newY]);
                }
            }
        }
        return tileList;
    }
}

