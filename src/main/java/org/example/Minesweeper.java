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

    //We need the action listener here so we can use this classes methods
    private final ActionListener tileActionListener = actionEvent -> {
        Object origin = actionEvent.getSource();
        Tile tile = (Tile) origin; //Only tiles will have this method so this is safe.
        if (!tile.isFlagged()) { //Only do move if tile is not flagged
            if (this.moveNumber == 0) { //First move has special code to ensure a 0 bomb tile is hit.
                this.firstMove(tile.getX(), tile.getY());
            }
            this.activateTileAtCoord(tile.getX(), tile.getY());
        }
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

    //sets up the container for everything graphical
    private void setUpFrame() {
        this.frame = new JFrame("Minesweeper");

        this.frame.setSize(1000,1000);
        this.frame.setResizable(false);
        this.frame.setLayout(new BorderLayout());
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //adds a menu to the frame
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

        JButton pauseMusic = new JButton("Music Toggle");
        pauseMusic.addActionListener(actionEvent -> this.audioPlayer.pause());

        menuPanel.add(restart);
        menuPanel.add(changeDifficulty);
        menuPanel.add(pauseMusic);
        this.frame.add(menuPanel, BorderLayout.NORTH);

    }

    //a collection of various methods (currently 1) called at cleanup
    private void cleanUp() {
        this.moveNumber = 0;
    }


    //sets up the tiles for both graphics and functionality
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

    //adds bombs to the board for a new level
    private void setupBombs() {

        //Clear the board first
        for (int i = 0; i < this.xSize; i++) {
            for (int j = 0; j < this.ySize; j++) {
                this.tiles[i][j].resetTile();
            }
        }
        //randomly generate locations for mines
        //uses a hash map to check for doubles
        HashSet<Tile> tileHash = new HashSet<Tile>();
        Random random = new Random();
        while (tileHash.size() < this.numBombs) {
            int xRandom = random.nextInt(this.xSize);
            int yRandom = random.nextInt(this.ySize);
            Tile testTile = this.tiles[xRandom][yRandom];
            tileHash.add(testTile);
        }

        //for the tiles adjacent to bombs, add 1 to their count
        for (Tile t : tileHash) {
            t.setBomb(true);
            for (Tile t2 : this.getAdjacentTiles(t)){
                t2.incrementBombCount();
            }
        }
    }


    //Code to activate a tile at coord
    private void activateTileAtCoord(int x, int y) {
        Tile tile = tiles[x][y];
        this.moveNumber++;
        if (tile.isBomb()) { //you lose
            tile.reveal();
            this.audioPlayer.playBombNoise();
            this.retryDialog("Oops, looks like you clicked on a mine. Would you like to try again?");

        } else if (tile.getNumBombsAdjacent() == 0) { // if a 0 adjacent tile, cascade
            tile.reveal();
            //then check adjacent tiles
            for (Tile t : this.getAdjacentTiles(tile)) {
                if (!t.isRevealed()) {
                    this.activateTileAtCoord(t.getX(), t.getY());
                }
            }

        } else { //default behaviour
            tile.reveal();
        }

        if (checkWin()) {
            this.retryDialog("Congratulations on your victory! Would you like to try again?");
        }
    }

    //Dialog box to restart the game
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

    //checks for victory conditions
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

    //various options for choosing difficulty. TODO: Move this to another class with other user input
    public void changeDifficulty() {
        String[] options = {"Easy","Normal","Hard","Very Hard"};
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
            default:
                //Should be impossible to get here but...
                JOptionPane.showMessageDialog(frame, "Please choose one of the valid options.");
        }

        //Starts a new game with a new frame based on the difficulty settings
        //TODO: There should be a way to do this without deleting the frame
        this.frame.dispose();

        this.setUpFrame();
        this.setUpMenu();
        this.cleanUp();
        this.setupTiles();

        this.setupBombs();
        this.frame.setVisible(true);
    }

    //Regenerate bombs on first move until the player has clicked a space with no bombs
    //TODO: Rewrite bomb generation method to take a coordinate to ignore as a value
    private void firstMove(int x, int y) {
        Tile tile = this.tiles[x][y];
        while (tile.getNumBombsAdjacent() != 0 || tile.isBomb()) {
            //System.out.println("Regenerating");
            this.setupBombs();
            tile = this.tiles[x][y];
        }
    }

    //Used to get all tiles adjacent to a tile
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

