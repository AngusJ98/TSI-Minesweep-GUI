package org.example;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Tile  extends JButton {
    private int x;
    private int y;
    private final String bombString = "\uD83D\uDCA3";
    private final String emptyTile = "";
    private boolean isBomb;
    private boolean isRevealed;
    private boolean isFlagged;
    private int numBombsAdjacent;

    public Tile(int x, int y, ActionListener action) {
        this.x = x;
        this.y = y;
        this.numBombsAdjacent = 0;
        this.isRevealed = false;
        this.isFlagged = false;
        this.setText("");
        this.addActionListener(action); //add left click functionality
        this.addMouseListener(new TileListener(this)); //add right click functionality
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void onRightClick() {
        if (this.isRevealed) {
            return;
        }
        if (this.isFlagged) {
            this.isFlagged = false;
            this.setText("");
        } else {
            this.isFlagged = true;
            this.setText("FLAG");
        }
    }

    //Reveals the tile. Uses information on the tile to determine how it should display
    public void reveal() {
        this.setEnabled(false); //disable the button
        this.isRevealed = true;
        if (this.isBomb) {
            this.setText(bombString);
        } else {
            if (this.numBombsAdjacent == 0) {
                this.setText(emptyTile);
            } else {
                this.setText(String.valueOf(this.numBombsAdjacent));
            }
        }
    }

    public void resetTile() {
        this.isRevealed = false;
        this.setEnabled(true);
        this.isBomb = false;
        this.isFlagged = false;
        this.numBombsAdjacent = 0;
        this.setText("");
    }

    public void incrementBombCount() {
        this.numBombsAdjacent++;
    }

    public boolean isBomb() {
        return isBomb;
    }

    public void setBomb(boolean bomb) {
        isBomb = bomb;
    }

    public int getNumBombsAdjacent() {
        return numBombsAdjacent;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Tile tile = (Tile) object;
        return x == tile.x && y == tile.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
