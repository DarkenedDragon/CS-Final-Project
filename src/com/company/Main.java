package com.company;

import processing.core.PApplet;
import g4p_controls.*;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class Main extends PApplet{

    //TODO remember to add credits

    private Maze maze;
    private int size;
    private long startTime, time;
    private final boolean VISUALIZE_MAZE = false;
    private final boolean VISUALIZE_A_STAR = false;
    private boolean show;
    private DateFormat format;
    private Player player;

    //GUI elements
    private GButton startButton, resignButton, playAgainButton;
    private GDropList sizeList;
    private enum ProgramStates {
        START, SETUP, MAZE_GENERATING, MAZE_DONE, PLAYER_ACTIVE, A_STAR_RUNNING, A_STAR_DONE
    }
    private ProgramStates state;

    /**
     * Class's main method. Running this will run the program
     * @param args
     */
    public static void main(String[] args) {
        String[] applet = new String[] {"com.company.Main"};
        PApplet.main(applet);
    }
    /**
     * Required method. Runs once, used to set properties of the window
     */
    @Override
    public void settings(){
        size(1200, 800);
    }
    /**
     * Required method. Runs once, used to initialize instance data.
     */
    @Override
    public void setup(){
        //frameRate(10);
        state = ProgramStates.START;

        format = new SimpleDateFormat("mm:ss:SSS");

        show = true;

        //GUI elements
        startButton = new GButton(this, width/2 - 100, height/2 - 50, 200, 100);
        startButton.setEnabled(true);
        startButton.setFont(new Font("Dialog", Font.PLAIN, 32));
        startButton.setText("START");
        startButton.setLocalColorScheme(G4P.GREEN_SCHEME);

        resignButton = new GButton(this, 50, height/2, 100, 50);
        resignButton.setFont(new Font("Dialog", Font.PLAIN, 16));
        resignButton.setText("RESIGN");
        resignButton.setLocalColorScheme(G4P.RED_SCHEME);
        resignButton.setEnabled(false);
        resignButton.setVisible(false);

        playAgainButton = new GButton(this, width/2-100, height/2-50, 200, 100, "PLAY AGAIN?");
        playAgainButton.setFont(new Font("Dialog", Font.PLAIN, 32));
        playAgainButton.setLocalColorScheme(G4P.GOLD_SCHEME);
        playAgainButton.setVisible(false);
        playAgainButton.setEnabled(false);

        sizeList = new GDropList(this, width/2 + 10, height/2 + 85, 50, 200);
        sizeList.setItems(new String[]{"16", "24", "32", "38"}, 0);
        sizeList.setFont(new Font("Dialog", Font.PLAIN, 32));
        sizeList.setLocalColorScheme(G4P.GREEN_SCHEME);

        size = Integer.parseInt(sizeList.getSelectedText());
    }

    /**
     * Required method. Loop that runs for every frame.
     */
    @Override
    public void draw(){
        //Resets the frame
        background(255);

        //Don't display the maze before it has been generated
        if(state != ProgramStates.START && state != ProgramStates.SETUP){
            maze.display();
        }

        //Control flow for the program
        switch(state){

            case START: //First thing the user sees. From here they can select size of the maze they wish to play through
                startButton.setEnabled(true);
                startButton.setVisible(true);
                sizeList.setEnabled(true);
                sizeList.setVisible(true);

                textAlign(CENTER, CENTER);
                textSize(48);
                fill(0);
                text("WELCOME TO AMAZING MAZEZ!", width/2, height/2 - 200);

                textSize(32);
                text("SIZE :", width/2 - textWidth("SIZE :")/2, height/2 + 100);
                break;

            case SETUP: //Sets up the maze
                maze = new Maze(this, size, 20);
                maze.setStart(maze.getCell(0,2));

                player = new Player(this, maze.getStart());
                state = ProgramStates.MAZE_GENERATING;
                break;

            case MAZE_GENERATING: //Generates the maze
                if(VISUALIZE_MAZE){
                    if(maze.generate(VISUALIZE_MAZE)){
                        state = ProgramStates.MAZE_DONE;
                    }
                }else{
                    maze.generate(VISUALIZE_MAZE);
                    state = ProgramStates.MAZE_DONE;
                }
                break;

            case MAZE_DONE: //Intermediary step between the maze being generated and the player given controls
                startTime = System.currentTimeMillis();
                state = ProgramStates.PLAYER_ACTIVE;

                break;

            case PLAYER_ACTIVE: //Here the player can control their character, trying to reach the end
                player.display();
                if(player.getCell() == maze.getEnd()){
                    //maze.resetGrid();
                    state = ProgramStates.A_STAR_RUNNING;
                }
                textAlign(LEFT, CENTER);
                textSize(16);
                text("Press to show solution", 10, height/2 - 25);
                text("Use the arrow keys to", 10, height/2 - 100);
                text("move around the maze", 10, height/2 - 80);

                textSize(20);
                textAlign(RIGHT, TOP);
                time = System.currentTimeMillis() - startTime;
                text("Time : " + format.format(time), width-10, 0);
                resignButton.setEnabled(true);
                resignButton.setVisible(true);
                break;

            case A_STAR_RUNNING: //State where the A* algorithm solves the maze
                resignButton.setVisible(false);
                resignButton.setEnabled(false);
                boolean done = maze.aStar(VISUALIZE_A_STAR);
                if (done){
                    state = ProgramStates.A_STAR_DONE;
                }
                break;
            case A_STAR_DONE: //Displays the solved maze according to A* Offers the player the option to play again
                fill(0);
                //textSize(16);
                textAlign(CENTER, TOP);
                text("SOLUTION:", width/2, 0);

                if(frameCount % 25 == 0)
                    show = !show;
                if(show){
                    textAlign(RIGHT, TOP);
                    text("Time : " + format.format(time), width-10, 0);
                }
                playAgainButton.setEnabled(true);
                playAgainButton.setVisible(true);
        }
    }

    /**
     * Event listener. Listens for a key to be pressed then runs. Must be included in a class with a draw method
     */
    public void keyPressed(){
        if(state == ProgramStates.PLAYER_ACTIVE) {
            if (keyCode == UP || key == 'w') {
                player.update(maze.up(player.getCell()));
            }
            if (keyCode == DOWN || key == 's') {
                player.update(maze.down(player.getCell()));
            }
            if (keyCode == RIGHT || key == 'a') {
                player.update(maze.right(player.getCell()));
            }
            if (keyCode == LEFT || key == 'd') {
                player.update(maze.left(player.getCell()));
            }
        }
    }

    /**
     * Event listener. Listens for the events fired by a button
     * @param button
     * @param event
     */
    public void handleButtonEvents(GButton button, GEvent event) {
        if(button == startButton) {
            startButton.setEnabled(false);
            startButton.setVisible(false);
            sizeList.setEnabled(false);
            sizeList.setVisible(false);
            background(255);
            state = ProgramStates.SETUP;
        }
        if(button == resignButton){
            //maze.resetGrid();
            state = ProgramStates.A_STAR_RUNNING;
        }
        if (button == playAgainButton){
            playAgainButton.setVisible(false);
            playAgainButton.setEnabled(false);
            sizeList.setSelected(0);
            size = Integer.parseInt(sizeList.getSelectedText());
            state = ProgramStates.START;
        }
    }

    /**
     * Event listener. Listens for events fired by a drop list.
     * @param list
     * @param event
     */
    public void handleDropListEvents(GDropList list, GEvent event){
        if(list == sizeList){
            size = Integer.parseInt(sizeList.getSelectedText());
            sizeList.forceBufferUpdate();
        }
    }
}
