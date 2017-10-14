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

    public Cell(int y, int x, int type) {
        this.y = y;
        this.x = x;
        this.type = type;
        this.status = FREE;
    }

    @Override
    public String toString() {
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

    /*Возвращает список соседей в заданном радиусе. Может понадобиться для поиска пути.
    * Иначе убрать параметр rad*/
    public ArrayList<Cell> getNeighbours(Cell[][] maze, int rad) {
        ArrayList<Cell> neighbours = new ArrayList<Cell>();
        if (x > 1) neighbours.add(maze[y][x - rad]);
        if (x < (maze[0].length - (rad + 1))) neighbours.add(maze[y][x + rad]);
        if (y > 1) neighbours.add(maze[y - rad][x]);
        if (y < (maze.length - (rad + 1))) neighbours.add(maze[y + rad][x]);
        Collections.shuffle(neighbours);
        return neighbours;
    }
}
