package com.company;

import processing.core.PApplet;

public class Cell{
    private PApplet applet;
    private int width;
    private int height;
    private int x;
    private int y;
    private boolean[] walls = {true, true, true, true}; // left, top, right, bottom
    private boolean visited, path, solution, end;
    private int fScore;
    private int gScore;
    private int hScore;
    private Cell cameFrom;

    enum Side {
        TOP, RIGHT, LEFT, BOTTOM
    }

    /**
     * Construction for a Cell object
     * @param applet The PApplet being used
     * @param x The x coordinate on the PApplet
     * @param y The y coordinate on the PApplet
     */
    public Cell(PApplet applet, int x, int y){
        this.applet = applet;
        this.x = x;
        this.y = y;
        width = 10;
        height = 10;
        visited = false;
        path = false;
        fScore = 0;
        gScore = 0;
        hScore = 0;

        cameFrom = null;
    }
    /**
     * Construction for a Cell object
     * @param applet The PApplet being used
     * @param x The x coordinate on the PApplet
     * @param y The y coordinate on the PApplet
     * @param width How wide the cell is
     * @param height How tall the cell is
     * @param walls  A boolean[] of which sides are walls
     */
    public Cell(PApplet applet, int x, int y, int width, int height,boolean[] walls){
        this.applet = applet;
        this.x = x;
        this.y = y;
        this.walls = walls;
        this.width = width;
        this.height = height;

        visited = false;
        path = false;
        fScore = 0;
        gScore = 0;
        hScore = 0;

        cameFrom = null;
        end = false;
        solution = false;
    }

    /**
     * Construction for a Cell object
     * @param applet The PApplet being used
     * @param x The x coordinate on the PApplet
     * @param y The y coordinate on the PApplet
     * @param width How wide the cell is
     * @param height How tall the cell is
     */
    public Cell(PApplet applet, int x, int y, int width, int height){
        this.applet = applet;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        visited = false;
        path = false;
        fScore = 0;
        gScore = 0;
        hScore = 0;

        cameFrom = null;
        end = false;
        solution = false;
    }

    /**
     * Method to display the cell. Needs to be run in a loop
     */
    public void display(){
        if(end) {
            applet.noStroke();
            applet.fill(255, 255, 0);
            applet.rect(x, y, width, height);
        }else if (solution) {
            applet.noStroke();
            applet.fill(0, 0, 255);
            applet.rect(x, y, width, height);
        } else if(path) {
            applet.noStroke();
            applet.fill(0, 255, 0);
            applet.rect(x, y, width, height);
        }else if(visited){
            applet.noStroke();
            applet.fill(255);
            applet.rect(x, y, width, height);
        }

        applet.stroke(0);

        if(walls[0]){
            applet.line(x, y, x, y+height);
        }
        if(walls[1]){
            applet.line(x, y, x+width, y);
        }
        if(walls[2]){
            applet.line(x+width, y, x+width, y+height);
        }
        if(walls[3]){
            applet.line(x, y+height, x+width, y+height);
        }
    }

    /**
     * Determines if there is a wall on the selected side
     * @param side Which side of the cell to be evaluated
     * @return true if the side has a wall, false otherwise
     */
    public boolean getWall(Side side){
        boolean out = true;
        switch(side) {
            case TOP:
                out =  walls[1];
                break;
            case BOTTOM:
                out =  walls[3];
                break;
            case LEFT:
                out =  walls[2];
                break;
            case RIGHT:
                out =  walls[0];
                break;
        }
        return out;
    }

    /**
     * Getter for the x variable
     * @return x
     */
    public int getX(){
        return x;
    }
    /**
     * Getter for the y variable
     * @return y
     */
    public int getY(){
        return y;
    }
    /**
     * Getter for the fScore variable
     * @return fScore
     */
    public int getFScore(){
        return fScore;
    }
    /**
     * Getter for the gScore variable
     * @return gScore
     */
    public int getGScore(){
        return gScore;
    }
    /**
     * Getter for the hScore variable
     * @return hScore
     */
    public int getHScore(){
        return hScore;
    }
    /**
     * Getter for the width variable
     * @return width
     */
    public int getWidth(){
        return width;
    }
    /**
     * Getter for the height variable
     * @return height
     */
    public int getHeight(){
        return height;
    }
    /**
     * Setter for the fScore variable
     * @param f The new value for fScore
     */
    public void setFScore(int f){
        fScore = f;
    }
    /**
     * Setter for the gScore variable
     * @param g The new value for gScore
     */
    public void setGScore(int g){
        gScore = g;
    }
    /**
     * Setter for the hScore variable
     * @param h The new value for hScore
     */
    public void setHScore(int h){
        hScore = h;
    }
    /**
     * Setter for the visited variable
     * @param visited The new value for visited
     */
    public void setVisited(boolean visited){
        this.visited = visited;
    }
    /**
     * Setter for the solution variable
     * @param solution The new value for solution
     */
    public void setSolution(boolean solution){
        this.solution = solution;
    }
    /**
     * Setter for the path variable
     * @param path The new value for path
     */
    public void setPath(boolean path){
        this.path = path;
    }
    /**
     * Setter for the end variable
     * @param end The new value for end
     */
    public void setEnd(boolean end){
        this.end = end;
    }
    /**
     * Determines if a cell has been visited
     */
    public boolean isVisited(){
        return visited;
    }

    /**
     * Removes a wall from a specified side of the cell
     * @param side The side of the cell to remove a wall from
     */
    public void removeWall(Side side){
        switch (side){
            case TOP:
                walls[1] = false;
                break;
            case BOTTOM:
                walls[3] = false;
                break;
            case LEFT:
                walls[2] = false;
                break;
            case RIGHT:
                walls[0] = false;
                break;
        }
    }

    /**
     * Getter for the cameFrom variable
     * @return cameFrom
     */
    public Cell getCameFrom(){
        return cameFrom;
    }

    /**
     * Setter for the cameFrom variable
     * @param cf new value for cameFrom
     */
    public void setCameFrom(Cell cf){
        cameFrom = cf;
    }

    /**
     * Method called when the object is printed
     * @return A string representation of the object
     */
    @Override
    public String toString(){
        String out = "";
        out += "x : " + x + "y : " + y + " ";
        return out;
    }
}
