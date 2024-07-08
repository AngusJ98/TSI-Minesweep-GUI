package org.example;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TileListener extends MouseAdapter {
    private Tile tile;
    public TileListener (Tile t) {
        super();

        this.tile = t;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        //System.out.println(e.getButton() + "-" + this.tile.isFlagged() );
        if (SwingUtilities.isRightMouseButton(e)) {
            this.tile.onRightClick();
        }
    }
}
