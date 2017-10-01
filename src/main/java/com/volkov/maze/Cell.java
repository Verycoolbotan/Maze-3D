package com.volkov.maze;

import java.util.ArrayList;

public class Cell {
    private int x, y;
    private boolean rightWall;
    private boolean downWall;

    public Cell(int x, int y){
        this.x = x;
        this.y = y;
        this.rightWall = true;
        this.downWall = true;
    }

    public ArrayList<Cell> getNeighbours(Cell[][] map){
        //У крайних и угловых клеток меньше соседей
        ArrayList<Cell> neighbours = new ArrayList<Cell>();

        if(this.x > 0) neighbours.add(map[this.y][this.x - 1]);
        if(this.y > 0) neighbours.add(map[this.y - 1][this.x]);
        if(this.x < map[0].length - 1) neighbours.add(map[this.y][this.x + 1]);
        if(this.x < map.length - 1) neighbours.add(map[this.y + 1][this.x]);

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
}
