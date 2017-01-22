// Assignment 9
// Wisniowiecki Sebastian
// sebdwis
// Chauhan Tanisha
// chauhan12

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.Random;
import java.util.Iterator;
import java.util.ArrayList;

// Custom iterator for the IList<T> function
class IListIterator<T> implements Iterator<T> {
    IList<T> items;
    
    // Constructor for the iterator
    IListIterator(IList<T> items) {
        this.items = items;
    }

    // Does items have another Cons?
    public boolean hasNext() {
        return this.items.isCons();
    }

    // Obtain the next data member
    public T next() {
        Cons<T> itemsAsCons = this.items.asCons();
        T answer = itemsAsCons.first;
        this.items = itemsAsCons.rest;
        return answer;
    }

    public void remove() {
        throw new UnsupportedOperationException("Can't do this!");
    }
}

// Generic list interface
interface IList<T> extends Iterable<T> { 
    boolean isCons();
    Cons<T> asCons();
    int length();
    IList<T> remove(T t);
}

// To represent a generic Cons list
class Cons<T> implements IList<T> {
    T first; 
    IList<T> rest;

    Cons(T first, IList<T> rest) {
        this.first = first;
        this.rest = rest;
    }

    // Is this Cons a Cons? (Iterator)
    public boolean isCons() {
        return true;
    }

    // Treat this IList as a Cons
    public Cons<T> asCons() {
        return (Cons<T>)this;
    }

    // Create a new instance of the custom iterator
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    } 

    // Return the length of this list
    public int length() {
        return 1 + this.rest.length();
    }
    
    // Remove the element t from the list
    public IList<T> remove(T t) {
        if (this.first.equals(t)) {
            return this.rest;
        }
        else {
            return new Cons<T>(this.first, this.rest.remove(t));
        }
    }
}

// To represent a generic empty list
class Empty<T> implements IList<T> {

    // Is this Empty a Cons?
    public boolean isCons() {
        return false;
    }

    // Cannot cast an Empty as a Cons.
    public Cons<T> asCons() {
        throw new ClassCastException("Can't cast empty to Cons!");
    }

    // Create a new instance of the custom iterator
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }  

    // Return the length of this list
    public int length() {
        return 0;
    }
    
    // Remove the element t from the list
    public IList<T> remove(T t) {
        return this;
    }
}

// To represent and individual Cell (square) in the gamd
class Cell {

    // represents absolute height of this cell, in feet
    double height;
    // In logical coordinates, with the origin at the top-left corner of the screen
    int x;
    int y;
    // the four adjacent cells to this one
    Cell left; 
    Cell top;
    Cell right;
    Cell bottom;
    // reports whether this cell is flooded or not
    boolean isFlooded; 

    // Initializes the fields of Cell to given values OR to default values
    Cell(int x, int y, double height) {
        this.height = height;
        this.x = x;
        this.y = y;
        this.isFlooded = false;
        this.left = null;
        this.top = null;
        this.right = null;
        this.bottom = null;
    }


    // Determine the color of the cell by dividing the total possible range of
    // heights into 8 ranges, and assigning a color to each range.
    Color getColor() {
        double base = ((double)ForbiddenIslandWorld.ISLAND_SIZE / 2) / 12;

        if (this.height < 0) {
            return new Color(0, 119, 255); 
        }
        else if (this.height < (base * 1)) {
            return new Color(112, 36, 5);
        }
        else if (this.height < (base * 2)) {
            return new Color(41, 70, 0);
        }
        else if (this.height < (base * 3)) {
            return new Color(50, 85, 1);
        }
        else if (this.height < (base * 4)) {
            return new Color(76, 112, 2);
        }
        else if (this.height < (base * 5)) {
            return new Color(58, 142, 2);
        }
        else if (this.height < (base * 6)) {
            return new Color(64, 159, 0);
        }
        else if (this.height < (base * 7)) {
            return new Color(30, 186, 12);
        }
        else if (this.height < (base * 8)) {
            return new Color(64, 219, 55);
        }
        else if (this.height < (base * 9)) {
            return new Color(100, 225, 80);
        }
        else if (this.height < (base * 10)) {
            return new Color(120, 240, 120);
        }
        else if (this.height < (base * 11)) {
            return new Color(160, 245, 160);
        }
        else {
            return new Color(180, 255, 205);
        }

    } 

    // Return a Square WorldImage from the fields of this Cell
    public WorldImage draw() {
        return new RectangleImage(ForbiddenIslandWorld.CANVAS_MULT, 
                ForbiddenIslandWorld.CANVAS_MULT, 
                OutlineMode.SOLID, this.getColor());
    }
}

// To represent a Cell that is a part of the ocean
class OceanCell extends Cell {

    // Initialize fields to given values or default values.
    OceanCell(int x, int y, double height) {
        super(x, y, height);
        this.isFlooded = true;
        this.left = null;
        this.top = null;
        this.right = null;
        this.bottom = null;
    }
}

// NOTE FOR THE 3 CLASSES BELOW:
// For some reason, big-bang and abstract classes don't mix very well...

// To represent the player
class Player {
    int x;
    int y;
    WorldImage img;
    Cell currCell;

    // Constructor for the player
    Player(Cell currCell) {
        this.currCell = currCell;
        this.x = currCell.x;
        this.y = currCell.y;
        this.img = new FromFileImage("pilot-icon.png");
    }
    
    // Update the player's location according to the currCell
    // EFFECT: changes the x and y values to the currCell's
    public void updatePlayer() {
        this.x = currCell.x;
        this.y = currCell.y;
    }
}

// To represent a Helicopter part
class HeliPart {
    int x;
    int y;
    WorldImage img;
    Cell currCell;
    
    // Helicopter part constructor
    HeliPart(Cell currCell) {
        this.currCell = currCell;
        this.x = currCell.x;
        this.y = currCell.y;
        this.img = new CircleImage(ForbiddenIslandWorld.CANVAS_MULT / 2,
                                   OutlineMode.SOLID,
                                   Color.YELLOW);
    }
}

// To represent a Helicopter
class Helicopter {
    int x;
    int y;
    WorldImage img;
    Cell currCell;
    
    // Helicopter constructor
    Helicopter(Cell currCell) {
        this.currCell = currCell;
        this.x = currCell.x;
        this.y = currCell.y;
        this.img = new FromFileImage("helicopter.png");
    }
}

// To represent an instance of the Forbidden Island game
class ForbiddenIslandWorld extends World {

    // All the cells of the game, including the ocean
    IList<Cell> board;
    // the current height of the ocean
    int waterHeight;
    // the type of map that is being played on
    String type;
    // the player
    Player player;
    // the helicopter parts
    IList<HeliPart> parts;
    // the helicopter
    Helicopter heli;

    // Defines an int constant
    static final int ISLAND_SIZE = 65;
    // The multiplier that will be used to calculate the Canvas size
    static final int CANVAS_MULT = 10;
    // Defines the size of the Canvas that will be rendered on
    static final int CANVAS_SIZE = ISLAND_SIZE * CANVAS_MULT;
    // Counter for the water level
    int counter = 0;

    // Constructor checks type of game selected, and uses initBoard,
    // assignPointers, and arrayToIList to generate an IList of cells
    // with appropriate values
    ForbiddenIslandWorld(int waterHeight, String type) {
        if (type.equals("regular")) {
            ArrayList<ArrayList<Cell>> temp = initBoard("regular", ISLAND_SIZE);
            assignPointers(temp, ISLAND_SIZE);
            this.board = arrayToIList(temp, ISLAND_SIZE);

        }
        else if (type.equals("random heights")) {
            ArrayList<ArrayList<Cell>> temp = initBoard("random", ISLAND_SIZE);
            assignPointers(temp, ISLAND_SIZE);
            this.board = arrayToIList(temp, ISLAND_SIZE);
        }
        else if (type.equals("random terrain")) {
            //  initTerrain(ISLAND_SIZE);
        }
        else {
            throw new IllegalArgumentException("Invalid type of World!");
        }
        this.waterHeight = waterHeight;
        this.player = new Player(this.randCell());
        this.parts = spawnParts(5);
        this.heli = new Helicopter(maxHeightCell());
    }

    // Constructor that takes a custom size to allow for testing of methods
    ForbiddenIslandWorld(int waterHeight, String type, int size) {
        if (type.equals("regular")) {
            ArrayList<ArrayList<Cell>> temp = initBoard("regular", size);
            assignPointers(temp, size);
            this.board = arrayToIList(temp, size);

        }
        else if (type.equals("random heights")) {
            ArrayList<ArrayList<Cell>> temp = initBoard("random", size);
            assignPointers(temp, size);
            this.board = arrayToIList(temp, size);
        }
        else {
            throw new IllegalArgumentException("Invalid type of World!");
        }
        this.waterHeight = waterHeight;
        this.player = new Player(this.randCell());
        this.parts = spawnParts(5);
        this.heli = new Helicopter(maxHeightCell());
    }

    /*** CONSTRUCTOR METHODS ***/
    // Initialize the board by creating an array of an array of cells, each of
    // which represent a row in the game. Makes height calculations based on 
    // game mode.
    ArrayList<ArrayList<Cell>> initBoard(String mode, int size) {
        Random rand = new Random();
        ArrayList<ArrayList<Cell>> board = new ArrayList<ArrayList<Cell>>();

        int yVal = 0;

        for (int i = 0; i <= size; i += 1) {
            ArrayList<Cell> row = new ArrayList<Cell>();
            for (int xVal = 0; xVal <= size; xVal += 1) { 
                double middle = size / 2;
                double manhDist = (middle - (Math.abs(middle - xVal) + Math.abs(middle - yVal)));
                if (manhDist < 0) {
                    row.add(new OceanCell(xVal, yVal, -1.0));
                }
                else if (mode.equals("regular")) {
                    row.add(new Cell(xVal, yVal, manhDist));
                }
                else {
                    row.add(new Cell(xVal, yVal, rand.nextInt((int)middle)));
                }
            }
            board.add(row);
            yVal += 1;
        }

        return board;
    }
    
    // Assign the Cell fields of every Cell to its appropriate neighbors
    // Cells on a border will be referred to themselves when attempting to
    // go outside the border
    // EFFECT: Initialize the left, right, top, and bottom fields of every
    // cell to the appropriate values.
    void assignPointers(ArrayList<ArrayList<Cell>> arr, int size) {
        for (int i = 0; i < size; i += 1) {
            for (int j = 0; j < size; j += 1) {
                ArrayList<Cell> row = arr.get(i);
                Cell currCell = row.get(j);
                if (i == 0) {
                    currCell.top = currCell;
                }
                else {
                    currCell.top = arr.get(i - 1).get(j);
                }
                if (j == 0) {
                    currCell.left = currCell;      
                }
                else {
                    currCell.left = row.get(j - 1);
                }
                if (i == (size - 1)) {
                    currCell.bottom = currCell;
                }
                else {
                    currCell.bottom = arr.get(i + 1).get(j);
                }
                if (j == (size - 1)) {
                    currCell.right = currCell;
                }
                else {
                    currCell.right = row.get(j + 1);
                }
            }
        }
    }

    // Converts the ArrayList of ArrayList of Cells to an ordered IList 
    // structure.
    IList<Cell> arrayToIList(ArrayList<ArrayList<Cell>> arr, int size) {
        IList<Cell> list = new Empty<Cell>();
        for (int i = 0; i < size; i += 1) {
            ArrayList<Cell> row = arr.get(i);
            for (int j = 0; j < size; j += 1) {
                list = new Cons<Cell>((row.get(j)), list);
            }
        }

        return list;
    }

    /*** GENERAL PURPOSE METHODS ***/
    // Returns a random cell that is NOT an ocean cell
    Cell randCell() {
        Random rand = new Random();
        int cellNo = rand.nextInt(this.board.length() / 2);
        for (Cell c : this.board) {
            if (!(c.isFlooded)) {
                cellNo -= 1;
            }
            if (!(c.isFlooded) && cellNo <= 0) {
                return c;
            }
        }
        return new Cell(0, 0, 0.0);
    }  
    
    // Uses randCell to create the given amount of Helicopter Parts at random
    // cells on the map.
    IList<HeliPart> spawnParts(int amount) {
        IList<HeliPart> parts = new Empty<HeliPart>();
        for (int i = amount; i > 0; i -= 1) {
            parts = new Cons<HeliPart>(new HeliPart(this.randCell()), parts);
        }
        return parts;
    }
    
    // Returns the cell on the board with the maximum height
    Cell maxHeightCell() {
        Cell maxCell = new Cell(0, 0, 0.0);
        for (Cell c : this.board) {
            if (c.height > maxCell.height) {
                maxCell = c;
            }
        }
        return maxCell;
    }

    // Resets the world with the given type
    // EFFECT: Re-initiates the fields of the World to reset the game with
    // the given type
    public void resetWorld(String type) {
        ArrayList<ArrayList<Cell>> temp = initBoard(type, ISLAND_SIZE);
        assignPointers(temp, ISLAND_SIZE);
        this.board = arrayToIList(temp, ISLAND_SIZE);
        this.player = new Player(randCell());
        this.parts = spawnParts(5);
        this.heli = new Helicopter(maxHeightCell());
    }

    /*** STANDARD WORLD METHODS ***/
    // World function - OnTick
    public void onTick() {  
        counter += 1;
        
        // Increase water height and adjust all cells accordingly
        if ((counter % 10) == 0) {
            this.waterHeight += 1;

            for (Cell c : this.board) {
                c.height -= 1;
                if (c.height < 0) {
                    c.isFlooded = true;
                }
            }
        }
        // If the player is on a part, remove it
        for (HeliPart p : this.parts) {
            if (p.currCell.equals(this.player.currCell)) {
                this.parts = this.parts.remove(p);
            }
        }
    }

    // World function - OnKeyEvent
    public void onKeyEvent(String key) {
        // Move the player with the arrow keys
        // I'm so sorry for these fields of fields of fields of fie...
        // It looks atrocious, doesn't it?
        if (key.equals("up") && !(this.player.currCell.top.isFlooded)) {
            this.player.currCell = this.player.currCell.top;
        }
        if (key.equals("down") && !(this.player.currCell.bottom.isFlooded)) {
            this.player.currCell = this.player.currCell.bottom;
        }
        if (key.equals("left") && !(this.player.currCell.left.isFlooded)) {
            this.player.currCell = this.player.currCell.left;
        }
        if (key.equals("right") && !(this.player.currCell.right.isFlooded)) {
            this.player.currCell = this.player.currCell.right;
        }
        // Update the player's position according to changes made above
        this.player.updatePlayer();
        // Reset the world with random heights mode
        if (key.equals("r")) {
            this.resetWorld("random heights");
        }
        // Reset the world with mountain mode
        if (key.equals("m")) {
            this.resetWorld("regular");
        }

    }


    // Draws every cell with its generated image, in the respective X and Y
    // coordinates, multiplied by CANVAS_MULT to scale to CANVAS_SIZE and
    // shifted over by a constant of 5 to improve general visibility.
    public WorldScene makeScene() {
        WorldScene scene = new WorldScene(CANVAS_SIZE, CANVAS_SIZE);
        
        // Draw the cells
        for (Cell c : this.board) {
            scene.placeImageXY(c.draw(), (CANVAS_MULT / 2) + c.x * CANVAS_MULT, 
                    (CANVAS_MULT / 2) + c.y * CANVAS_MULT);
        }
        // Draw the helicopter parts
        for (HeliPart p : this.parts) {
            scene.placeImageXY(p.img, (CANVAS_MULT / 2) + p.x * CANVAS_MULT,
                               (CANVAS_MULT / 2) + p.y * CANVAS_MULT);
        }
        // Draw the helicopter
        scene.placeImageXY(heli.img, (heli.x * CANVAS_MULT) + (CANVAS_MULT / 2), 
                heli.y * CANVAS_MULT);
        
        // Draw the player
        scene.placeImageXY(player.img, (player.x * CANVAS_MULT) + (CANVAS_MULT / 2), 
                player.y * CANVAS_MULT);
        return scene;
    }

    // Has the player won the game? (Parts list is empty and player is at helicopter)
    public boolean gameWin() {
        return (this.parts.length() == 0) && (this.player.currCell.equals(this.heli.currCell));
    }
    
    // Has the player lost the game? (Did your poor guy drown by sitting on a tile too long?)
    public boolean gameOver() {
        return this.player.currCell.isFlooded;
    }

    // Generate the end state message depending on whether the player won or lost
    public WorldEnd worldEnds() {
        WorldScene finalScene = this.makeScene();
        if (this.gameWin()) {
            finalScene.placeImageXY(new TextImage("You win!", 60, Color.YELLOW),
                    (ISLAND_SIZE * CANVAS_MULT) / 2, (ISLAND_SIZE * CANVAS_MULT) / 2);
        }
        if (this.gameOver()) {
            finalScene.placeImageXY(new TextImage("Game Over!", 60, Color.YELLOW), 
                    (ISLAND_SIZE * CANVAS_MULT) / 2, (ISLAND_SIZE * CANVAS_MULT) / 2);
        }
        return new WorldEnd((this.gameOver() || this.gameWin()), finalScene);

    }
}

// To execute the Forbidden Island game and test its methods
class ExamplesWorld {


    ForbiddenIslandWorld w1;

    // Starting World, to change map mode, change "regular" to "random heights"
    void initWorld() {
        this.w1 = new ForbiddenIslandWorld(0, "regular");
    }

    // To launch the game
    void testBigBang(Tester t) {
        initWorld();
        this.w1.bigBang(ForbiddenIslandWorld.CANVAS_SIZE, 
                        ForbiddenIslandWorld.CANVAS_SIZE, 0.5);    
    }



    // Test World
    ForbiddenIslandWorld testw = new ForbiddenIslandWorld(100, "regular", 3);

    // To test the assignPointers method
    void testAssignPointers(Tester t) {
        ArrayList<ArrayList<Cell>> arr = testw.initBoard("regular", 3);
        testw.assignPointers(arr, 3);

        t.checkExpect(arr.get(0).get(0).left, arr.get(0).get(0));
        t.checkExpect(arr.get(0).get(0).right, arr.get(0).get(1));
        t.checkExpect(arr.get(0).get(0).bottom, arr.get(1).get(0));
        t.checkExpect(arr.get(0).get(0).top, arr.get(0).get(0));
        t.checkExpect(arr.get(2).get(0).left, arr.get(2).get(0));
        t.checkExpect(arr.get(2).get(0).bottom, arr.get(2).get(0));
        t.checkExpect(arr.get(2).get(0).top, arr.get(1).get(0));
        t.checkExpect(arr.get(2).get(0).right, arr.get(2).get(1));
        t.checkExpect(arr.get(1).get(1).left, arr.get(1).get(0));
        t.checkExpect(arr.get(1).get(1).right, arr.get(1).get(2));
        t.checkExpect(arr.get(1).get(1).bottom, arr.get(2).get(1));
        t.checkExpect(arr.get(1).get(1).top, arr.get(0).get(1));       
    }

    // To test the arrayToIList method
    void testArrayToIList(Tester t) {
        ArrayList<ArrayList<Cell>> arr = testw.initBoard("regular", 3);
        ArrayList<ArrayList<Cell>> mt = new ArrayList<ArrayList<Cell>>();

        t.checkExpect(testw.arrayToIList(arr, 3),
            new Cons<Cell>(arr.get(2).get(2),
                new Cons<Cell>(arr.get(2).get(1),
                    new Cons<Cell>(arr.get(2).get(0),
                        new Cons<Cell>(arr.get(1).get(2),
                            new Cons<Cell>(arr.get(1).get(1),
                                new Cons<Cell>(arr.get(1).get(0),
                                    new Cons<Cell>(arr.get(0).get(2),
                                        new Cons<Cell>(arr.get(0).get(1),
                                            new Cons<Cell>(arr.get(0).get(0),
                                                new Empty<Cell>()))))))))));
        t.checkExpect(testw.arrayToIList(mt, 0), new Empty<Cell>());
    }

    // To test the drawing methods in cell, getColor and draw
    void testDrawing(Tester t) {
        t.checkExpect(new Cell(0, 0, 11.0).draw(), 
                      new RectangleImage(10, 10, OutlineMode.SOLID, new Color(58, 142, 2)));
        t.checkExpect(new Cell(0, 0, 5.0).draw(), 
                      new RectangleImage(10, 10, OutlineMode.SOLID, new Color(41, 70, 0)));
        t.checkExpect(new Cell(0, 0, 11.0).getColor(), 
                      new Color(58, 142, 2));
        t.checkExpect(new Cell(0, 0, 8.0).getColor(), 
                      new Color(50, 85, 1));
        t.checkExpect(new Cell(0, 0, 5.0).getColor(), 
                      new Color(41, 70, 0));
        t.checkExpect(new Cell(0, 0, 2.0).getColor(), 
                      new Color(112, 36, 5));
        t.checkExpect(new Cell(0, 0, 0).getColor(), 
                      new Color(112, 36, 5));
        t.checkExpect(new Cell(0, 0, -1.0).getColor(), 
                      new Color(0, 119, 255));
    }
    
}