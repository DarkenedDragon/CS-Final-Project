package com.company;

import processing.core.PApplet;

import java.util.ArrayList;

public class Player {

    private Cell cell;
    private ArrayList<Cell> path;
    private int x;
    private int y;
    private PApplet applet;
    private final int RADIUS = 5;

    /**
     * Constructor to build Player objects
     * @param applet The PApplet being used for the program
     * @param cell The Cell the player starts on
     */
    public Player(PApplet applet , Cell cell){
        this.applet = applet;
        this.cell = cell;
        path = new ArrayList<>();
        path.add(cell);
        x = cell.getX();
        y = cell.getY();
    }

    /**
     * Method to display the player. Needs to be in a loop
     */
    public void display(){
        applet.stroke(0);
        applet.fill(0);
        applet.ellipseMode(applet.CENTER);

        applet.ellipse(x + cell.getWidth()/2, y + cell.getHeight()/2, RADIUS*2, RADIUS*2);
        applet.noFill();
    }

    /**
     * Method to update the position and properties of the player. Needs to be in a loop
     * @param cell Players updated position
     */
    public void update(Cell cell){
        if(cell != null) {
            if(cell != this.cell){
                path.add(cell);
            }
            this.cell = cell;
            x = cell.getX();
            y = cell.getY();
        }
        for (Cell c : path){
            c.setPath(true);//TODO make sure that a_star always resets every cell
        }
    }

    /**
     * Getter method for the Player's current position
     * @return The Players current cell
     */
    public Cell getCell(){
        return cell;
    }
}
