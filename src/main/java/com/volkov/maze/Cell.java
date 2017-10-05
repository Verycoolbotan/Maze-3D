package com.volkov.maze;

import java.util.ArrayList;
import java.util.Collections;

public class Cell {
    public static final int FREE = 0;
    public static final int CONNECTED = 1;
    public static final int ADDED = 2;

    private int x, y;
    private boolean rightWall;
    private boolean downWall;
    private int status;

    public Cell(int x, int y){
        this.x = x;
        this.y = y;
        this.rightWall = true;
        this.downWall = true;
        this.status = FREE;
    }

    public ArrayList<Cell> getNeighbours(Cell[][] map){
        //У крайних и угловых клеток меньше соседей
        ArrayList<Cell> neighbours = new ArrayList<Cell>();

        if(this.x > 0) neighbours.add(map[this.y][this.x - 1]);
        if(this.y > 0) neighbours.add(map[this.y - 1][this.x]);
        if(this.x < map[0].length - 1) neighbours.add(map[this.y][this.x + 1]);
        if(this.y < map.length - 1) neighbours.add(map[this.y + 1][this.x]);


        Collections.shuffle(neighbours);

        return neighbours;
    }

    public boolean isDownWall() {
        return downWall;
    }

    public boolean isRightWall() {
        return rightWall;
    }

    public void setDownWall(boolean downWall) {
        this.downWall = downWall;
    }

    public void setRightWall(boolean rightWall) {
        this.rightWall = rightWall;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
