package com.volkov.maze;

import java.util.ArrayList;
import java.util.Collections;

//Класс упрощает обмен данными о состоянии клетки в лабиринте
public class Cell {
    public static final int FREE = 0;
    public static final int CONNECTED = 1;
    public static final int ADDED = 2;


    private int y, x;
    private int type;
    private int status;

    public Cell(int y, int x, int type){
        this.y = y;
        this.x = x;
        this.type = type;
        this.status = FREE;
    }

    @Override
    public String toString(){
        return "(" + y + ", " + x + ")";
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<Cell> getNeighbours(Cell[][] maze){
        ArrayList<Cell> neighbours = new ArrayList<Cell>();
        if(x > 1) neighbours.add(maze[y][x-2]);
        if(x < (maze[0].length - 2)) neighbours.add(maze[y][x + 2]);
        if(y > 1) neighbours.add(maze[y - 1][x]);
        if(y < (maze.length - 3)) neighbours.add(maze[y + 2][x]);
        Collections.shuffle(neighbours);

        for(Cell cell:neighbours) System.out.println(cell.toString());
        return neighbours;
    }
}
