package com.volkov.maze;

import java.util.Collections;
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
                if((i % 2 == 0) || (j % 2 == 0)) maze[i][j] = new Cell(i, j, 1);
                else maze[i][j] = new Cell(i, j, 0);
            }
        }

        LinkedList<Cell> processing = new LinkedList<Cell>();
        Cell current = maze[1][1];
        current.setStatus(Cell.ADDED);
        processing.push(current);

        while(!processing.isEmpty()){
            current = processing.pop();
            System.out.println("Текущая: " + current.toString());
            System.out.println("Клеток в списке: " + processing.size());
            boolean connected = false;
            for(Cell cell:current.getNeighbours(maze)){
                if(!connected && cell.getStatus() == Cell.CONNECTED){
                    connect(current, cell, maze);
                    connected = true;
                } else if(cell.getStatus() == Cell.FREE){
                    cell.setStatus(Cell.ADDED);
                    processing.push(cell);
                }
            }
            current.setStatus(Cell.CONNECTED);
            Collections.shuffle(processing);
        }

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                if(maze[i][j].getType() == 0) System.out.print(" ");
                else System.out.print("#");
            }
            System.out.println();
        }

        return maze;
    }
}
