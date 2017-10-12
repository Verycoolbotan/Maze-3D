package com.volkov.maze;

import java.util.ArrayList;
import java.util.LinkedList;

public class Maze {
    private static Cell[][] maze;

    private static void connect(Cell one, Cell two, Cell[][] maze){
        if(one.getY() == two.getY()){
            if (one.getX() < two.getX()) {
                maze[one.getY()][one.getX() + 1].setType(0);
            } else {
                maze[one.getY()][one.getX() - 1].setType(0);
            }
        }
        if(one.getX() == two.getX()){
            if (one.getY() < two.getY()) {
                maze[one.getY() + 1][one.getX()].setType(0);
            } else {
                maze[one.getY() - 1][one.getX()].setType(0);
            }
        }
        System.out.println("Соединены: "  + one.toString() + ", " + two.toString());
    }

    public static Cell[][] generate(int width, int height){
        maze = new Cell[height][width];

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                if((i % 2 == 0) || (j % 2 == 0)) maze[i][j] = new Cell(i, j, 1, Cell.VISITED);
                else maze[i][j] = new Cell(i, j, 0, Cell.UNVISITED);
            }
        }

        LinkedList<Cell> processing = new LinkedList<Cell>();
        Cell current;
        processing.push(maze[1][1]);

        while(!processing.isEmpty()){
            current = processing.pop();
            System.out.println("Текущая: " + current.toString());
            System.out.println("Клеток в списке: " + processing.size());
            current.setStatus(Cell.VISITED);
            boolean connected = false;
            for(Cell cell:current.getNeighbours(maze)){
                if(!connected){
                    connect(current, cell, maze);
                    connected = true;
                } else if(cell.getStatus() == Cell.UNVISITED){
                    System.out.println("Добавлена в список: " + cell.toString());
                    processing.push(cell);
                }
            }
        }

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                System.out.print(maze[i][j].getType());
            }
            System.out.println();
        }

        return maze;
    }
}
