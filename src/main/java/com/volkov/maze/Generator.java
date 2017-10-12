package com.volkov.maze;

import java.util.ArrayList;
import java.util.LinkedList;

public class Generator {
    private static Cell[][] maze;

    private static void connect(Cell one, Cell two, Cell[][] maze){
        if(one.getY() == two.getY()) maze[one.getY()][(int)((one.getX() + two.getX())/2)].setType(0);
        if(one.getX() == two.getX()) maze[one.getX()][(int)((one.getY() + two.getY())/2)].setType(0);
        System.out.println("Соединены: " + "(" + one.getY() + ", " + one.getX() + ")" + ", " + "(" + two.getY() + ", " + one.getX() + ")");
    }

    public static Cell[][] generate(int width, int height){
        maze = new Cell[height][width];

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                if((i % 2 == 0) || (j % 2 == 0)) maze[i][j] = new Cell(i, j, 1);
                else maze[i][j] = new Cell(i, j, 0);
            }
        }

        /*for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                if((i % 2 != 0  && j % 2 != 0) && (i < height-1 && j < width-1))
                    maze[i][j] = new Cell(i, j, 0);
                else maze[i][j] = new Cell(i, j, 1);
            }
        }*/

        LinkedList<Cell> processing = new LinkedList<Cell>();
        Cell current;
        processing.add(maze[1][1]);

        while(!processing.isEmpty()){
            current = processing.pop();

            System.out.println("Текущая клетка: " + current.getY() + ", " + current.getX());
            System.out.println(processing.size());

            current.setStatus(Cell.VISITED);
            ArrayList<Cell> neighbours = current.getNeighbours(maze);
            boolean connected = false;
            for(Cell cell : neighbours){
                if(!connected){
                    connect(current, cell, maze);
                    connected = true;
                } else if (cell.getStatus() == Cell.NOT_VISITED){
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
