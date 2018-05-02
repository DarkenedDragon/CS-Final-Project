package com.company;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class Maze {
    private PApplet applet;
    private Cell[][] grid;
    private Stack<Cell> path;
    private ArrayList<Cell> openSet, closedSet;
    private int gridWidth;
    private int gridHeight;
    private Cell current, start, end;

    /**
     * Constructor for Maze objects.
     * @param applet The PApplet being used
     * @param size Forms a dim x dim sized maze
     * @param dimensions How large each cell in the maze should be
     */
    public Maze(PApplet applet, int size, int dimensions) {
        this.applet = applet;
        grid = new Cell[size][size];
        path = new Stack<>();

        gridWidth = dimensions;
        gridHeight = dimensions;

        resetMaze();

        start = grid[0][0];
        start.setVisited(true);
        current = start;

        openSet = new ArrayList<>();
        closedSet = new ArrayList<>();
        openSet.add(start);

        end = grid[grid.length-1][grid[0].length-1];
        end.setEnd(true);

    }

    /**
     * Constructor for Maze objects.
     * @param applet The PApplet being used
     * @param size Forms a dim x dim sized maze
     * @param dimensions How large each cell in the maze should be
     * @param start Cell from which the player starts
     */
    public Maze(PApplet applet, int size, int dimensions, Cell start) {
        this.applet = applet;
        grid = new Cell[size][size];
        path = new Stack<>();

        gridWidth = dimensions;
        gridHeight = dimensions;

        resetMaze();

        this.start = start;
        start.setVisited(true);
        current = start;

        openSet = new ArrayList<>();
        closedSet = new ArrayList<>();
        openSet.add(start);

        end = grid[grid.length-1][grid[0].length-1];
        end.setEnd(true);

    }

    /**
     * Generates a maze of size specified in the constructor
     *
     * @param visualize whether to visualize the process or not. If true, must be run in a draw method.
     */
    public boolean generate(boolean visualize){
        //Maze generator
        if(visualize){
            return generate();
        }else{
            while (!generate()){
                //System.out.println("Gen ran returning : " + generate());
            }
        }
        return true;
    }

    /**
     * Method to display the maze. Needs to be called every frame
     */
    public void display(){
        for (int i = 0; i< grid.length; i++){
            for (int j = 0; j< grid[0].length; j++){
                grid[i][j].display();
            }
        }
    }

    /**
     * Getter for a specific cell in the maze
     * @param y Y coordinate of the cell in the maze
     * @param x X coordinate of the cell in the maze
     * @return
     */
    public Cell getCell(int y, int x){
        return grid[y][x];
    }

    /**
     * Sets the cell considered the start of the maze.
     * @param start The Cell to be considered the start
     */
    public void setStart(Cell start){
        openSet.remove(this.start);
        this.start = start;
        openSet.add(start);
        start.setPath(true);
    }

    /**
     * Resets the properties of the cells in the grid.
     */
    public void resetGrid(){
        for(int i = 0;i<grid.length;i++){
            for(int j = 0; j<grid[0].length;j++){
                grid[i][j].setPath(false);
                grid[i][j].setCameFrom(null);
            }
        }
    }

    /**
     * Finds the coordinates of a cell in the grid
     * @param cell the cell whose coordinates needs finding
     * @return an int[] with the coordinate in the form of y, x
     */
    public int[] getCoordinates(Cell cell){
        int x = -1;
        int y = -1;
        for (int i = 0; i<grid.length;i++){
            for (int j = 0;j<grid[0].length;j++){
                if(grid[i][j] == cell){
                    y = i;
                    x = j;
                }
            }
        }
        return new int[]{y, x};
    }
    public Cell getStart(){
        return start;
    }
    public Cell getEnd(){
        return end;
    }

    public Cell up(Cell currentCell){
        ArrayList<Cell> possible = new ArrayList<>(Arrays.asList(getAvailableNeighbors(currentCell)));

        int x = getCoordinates(currentCell)[1];
        int y = getCoordinates(currentCell)[0];

        if(x > 0 && possible.contains(grid[y][x-1])){//UP
            return grid[y][x-1];
        }
        return null;
    }
    public Cell down(Cell currentCell){
        ArrayList<Cell> possible = new ArrayList<>(Arrays.asList(getAvailableNeighbors(currentCell)));

        int x = getCoordinates(currentCell)[1];
        int y = getCoordinates(currentCell)[0];

        if(x < grid[0].length-1 && possible.contains(grid[y][x+1])){//DOWN
            return grid[y][x+1];
        }
        return null;
    }
    public Cell left(Cell currentCell){
        ArrayList<Cell> possible = new ArrayList<>(Arrays.asList(getAvailableNeighbors(currentCell)));

        int x = getCoordinates(currentCell)[1];
        int y = getCoordinates(currentCell)[0];

        if(y > 0 && possible.contains(grid[y-1][x])){//LEFT
            return grid[y-1][x];
        }
        return null;
    }
    public Cell right(Cell currentCell){
        ArrayList<Cell> possible = new ArrayList<>(Arrays.asList(getAvailableNeighbors(currentCell)));

        int x = getCoordinates(currentCell)[1];
        int y = getCoordinates(currentCell)[0];

        if(y < grid[0].length-1 && possible.contains(grid[y+1][x])){//RIGHT
            return grid[y+1][x];
        }
        return null;
    }

    /**
     * Generates the maze. Needs to be inside a loop
     * @return Returns if the maze has finished generating
     */
    private boolean generate(){
        boolean done = false;
        try {
            if (unvisitedCells(grid)) {
                if (!neighborsVisited(getNeighbors(current))) {
                    Cell neighbor = getRandomNeighbor(getNeighbors(current));
                    path.push(current);
                    //remove walls between cells
                    current.removeWall(findDirection(current, neighbor)); // not working correctly
                    neighbor.removeWall(findDirection(neighbor, current));
                    neighbor.setVisited(true);
                    current = neighbor;
                } else if (!path.empty()) {
                    current = path.pop();
                }

            } else {
                done = true;
                //end = getCell(grid.length, (int)(0.75 * grid[0].length));
                //end.removeWall(Cell.Side.RIGHT);
            }
        }catch (NullPointerException e){
            System.out.println("Current is null. Please set current to the start before calling this method");
            System.out.println(e.getMessage());
        }
        return done;
    }

    /**
     * Runs the A* pathfinder algorithm.
     * @param visualize Whether to visualize the algorithm. If true, needs to be run in the draw loop
     * @return Returns whether the algorithm has finished. true if it has, false otherwise
     */
    public boolean aStar(boolean visualize){
        boolean done = false;
        if(visualize){
            done = aStar();
        }else{
            while(!done){
                done = aStar();
            }
        }
        return done;
    }

    /**
     * Runs the A* pathfinder algorithm. Needs to be run in a loop
     * @return Returns whether the algorithm has finished. true if it has, false otherwise
     */
    private boolean aStar(){
        if(!openSet.isEmpty()){

            int lowestF = 0;
            for (int i = 0;i<openSet.size();i++){
                if(openSet.get(i).getFScore() < openSet.get(lowestF).getFScore()){
                    lowestF = i;
                }
            }

            Cell current = openSet.get(lowestF);

            if(current == end){
                //We're done!
                recreatePath(end);
                return true;
            }

            openSet.remove(current);
            closedSet.add(current);
            Cell[] neighbors = getAvailableNeighbors(current);
            //Cell[] neighbors = getNeighbors(current);
            for(Cell neighbor : neighbors){
                if(!closedSet.contains(neighbor)){ // && neighbor is not a obstacle

                    int tempGScore = current.getGScore() + 1;

                    boolean betterPath = false;
                    if(openSet.contains(neighbor)){
                        if(tempGScore < neighbor.getGScore()){
                            neighbor.setGScore(tempGScore);
                            betterPath = true;
                        }
                    }else{
                        neighbor.setGScore(tempGScore);
                        openSet.add(neighbor);
                        betterPath = true;
                    }
                    if(betterPath){
                        neighbor.setHScore((int) heuristic(neighbor, end));
                        neighbor.setFScore(neighbor.getGScore() + neighbor.getHScore()); //f(x) = g(x) + h(x)
                        //path.add(neighbor);
                        neighbor.setCameFrom(current);
                    }
                }
            }
        }else{
            return true;
        }
        return false;


    }//

    /**
     * Resets the grid entirely. Will need to be generated after running this method
     */
    private void resetMaze(){
        int xOffset = applet.width/2 - (grid.length/2 * gridWidth);
        int yOffset = applet.height/2 - (grid[0].length/2 * gridHeight);

        path = new Stack<>();
        openSet = new ArrayList<>();
        closedSet = new ArrayList<>();
        for (int i = 0;i<grid.length;i++){
            for (int j = 0;j<grid[0].length;j++){
                grid[i][j] = new Cell(applet, i*gridWidth + xOffset, j*gridHeight + yOffset, gridWidth, gridHeight);
            }
        }
    }

    /**
     * Determines if there are any unvisited cells in the grid
     * @param cells the grid of cells to scan
     * @return Whether there are any unvisited cells. true if there are, false otherwise
     */
    private boolean unvisitedCells(Cell[][] cells){
        boolean unvisited = false;
        for (Cell[] row : cells){
            for (Cell cell : row){
                if(!cell.isVisited()){
                    unvisited = true;
                }
            }
        }
        return unvisited;
    }

    /**
     * Finds the neighbors of a selected cell in the grid
     * @param cell The cell whose neighbors need to be found
     * @return A Cell[] of the neighbors in no particular order
     */
    private Cell[] getNeighbors(Cell cell){
        int x = -1;
        int y = -1;
        for (int i = 0; i<grid.length;i++){
            for (int j = 0;j<grid[0].length;j++){
                if(grid[i][j] == cell){
                    y = i;
                    x = j;
                }
            }
        }
        //System.out.println("x : " + x + " y : " + y);

        ArrayList<Cell> output = new ArrayList<>();
        if(x != -1 && y != -1) {
            if (x > 0) {
                output.add(grid[y][x - 1]);
            }
            if (x < grid[0].length - 1) {
                output.add(grid[y][x + 1]);
            }
            if (y > 0) {
                output.add(grid[y - 1][x]);
            }
            if (y < grid.length - 1) {
                output.add(grid[y + 1][x]);
            }

        }
        /*
        if(state != Main.ProgramStates.MAZE_GENERATING && cell.getX() + cellSize >= end.getX() && cell.getY() == end.getY()){
            output.add(end);
        }
        */
        return output.toArray(new Cell[output.size()]);
        //return new Cell[]{grid[][]};
    }

    /**
     * Finds the neighbors who can be moved to from the current cell. Used in A* and to check players movements
     * @param cell The Cell whose neighbors need finding
     * @return A Cell[] of available neighbors
     */
    private Cell[] getAvailableNeighbors(Cell cell){
        Cell[] neighbors = getNeighbors(cell);
        ArrayList<Cell> available = new ArrayList<>();

        for (Cell u : neighbors){
            Cell.Side dir = findDirection(cell, u);
            if (dir != null && !cell.getWall(dir)){
                available.add(u);
            }
        }
        return available.toArray(new Cell[available.size()]);

    }

    /**
     * Recreates the path the A* algorithm took from the end cell until it reaches the beginning
     * @param end The last cell in the chain
     */
    private void recreatePath(Cell end){
        //retrace path
        Cell temp = end;
        ArrayList<Cell> path = new ArrayList<>();
        while(temp.getCameFrom() != null){
            path.add(temp);
            temp = temp.getCameFrom();
        }

        for(Cell u : path){
            u.setSolution(true);
        }
    }

    /**
     * Calculates the h(x) for the A* algorithm. Uses a manhattan taxicab heuristic
     * @param start The start (current) cell
     * @param end The end cell
     * @return The h(x)
     */
    private double heuristic(Cell start, Cell end){
        return Math.abs(start.getX() - end.getX()) * Math.abs(start.getY() - end.getY());
    }

    /**
     * Checks if all of a cell's neighbors have been visited
     * @param neighbors A Cell[] of a cell's neighbors
     * @return true if all neighbors have been visited, false otherwise
     */
    private boolean neighborsVisited(Cell[] neighbors){
        boolean visited = true;
        for (Cell cell : neighbors){
            if(!cell.isVisited()){
                visited = false;
            }
        }
        return visited;
    }

    /**
     * Gets a random, unvisited, neighbor from a Cell[] of neighbors.
     * Precondition : At least one of the neighbors must be unvisited
     * @param neighbors The Cell[] of neighbors to select a unvisited neighbor from
     * @return
     */
    private Cell getRandomNeighbor(Cell[] neighbors){
        Cell choice = null;
        while(choice == null || choice.isVisited()){
            int rand = (int)(Math.random() * neighbors.length);
            choice = neighbors[rand];
        }
        return choice;
    }

    /**
     * Finds the direction another cell is in relation to a first. For example if neighbor is above current,
     * the method would return TOP
     * @param current The current cell
     * @param neighbor The neighbor, in which the direction from the current is being evaluated
     * @return The direction the neighbor has in relation to the current cell.
     */
    private Cell.Side findDirection(Cell current, Cell neighbor) {
        int currentX = -1;
        int currentY = -1;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == current) {
                    currentY = i;
                    currentX = j;
                }
            }
        }
        int neighborX = -1;
        int neighborY = -1;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == neighbor) {
                    neighborY = i;
                    neighborX = j;
                }
            }
        }

        int side = currentY - neighborY;
        int up = currentX - neighborX;

        //{left, top, right, bottom}
        if (up > 0) {
            //Top
            return Cell.Side.TOP;
        }
        if (up < 0) {
            //bottom
            return Cell.Side.BOTTOM;
        }
        if (side > 0) {
            //right
            return Cell.Side.RIGHT;
        }
        if (side < 0) {
            //left
            return Cell.Side.LEFT;
        }

        return null;
    }

}

