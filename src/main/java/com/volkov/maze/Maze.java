package com.volkov.maze;

import java.util.Collections;
import java.util.LinkedList;

public class Maze {
    private static Cell[][] maze;

    //Соединяем клетки one и two, т.е. удаляем стенку между ними
    private static void connect(Cell one, Cell two, Cell[][] maze) {
        if (one.getY() == two.getY()) {
            if (one.getX() < two.getX()) {
                maze[one.getY()][one.getX() + 1].setType(0);
            } else {
                maze[one.getY()][one.getX() - 1].setType(0);
            }
        }
        if (one.getX() == two.getX()) {
            if (one.getY() < two.getY()) {
                maze[one.getY() + 1][one.getX()].setType(0);
            } else {
                maze[one.getY() - 1][one.getX()].setType(0);
            }
        }
        System.out.println("Соединены: " + one.toString() + ", " + two.toString());
    }

    private static int random(int a, int b) {
        return (int) (Math.random() * (b - a + 1) + a);
    }

    /*Алгоритм Прима, адаптированный под генерацию лабиринта.
    * Всё просто:
    * 1. Сначала все клетки разделены стенками
    * 2. Выбираем какую-нибудь клетку и добавляем её в список
    * 3. Пока список не пуст:
    * 3.1. Убираем текущую клетку из списка (уже обработана)
    * 3.2. Добавляем в список её соседей
    * 3.3. Соединяем случайную клетку из списка с предыдущей
    * 3.4. Убираем её из списка
    * LinkedList позволяет настроить генерацию:
    * добавление в начало/конец списка меняет вид лабиринта,
    * можно перемешивать список обрабатываемых клеток или список соседей
    * (включено по умолчанию, т.к. иначе наблюдаем "предвзятость" - генерацию
    * неприлично длинных вертикальных/горизонтальных коридоров)*/
    public static Cell[][] generate(int width, int height) {
        maze = new Cell[height][width];

        //Сначала клетки разделены стенами
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if ((i % 2 == 0) || (j % 2 == 0)) maze[i][j] = new Cell(i, j, 1);
                else maze[i][j] = new Cell(i, j, 0);
            }
        }

        //Создаём список и начинаем обход со случайной клетки
        LinkedList<Cell> processing = new LinkedList<Cell>();
        Cell current = maze[1][1];
        current.setStatus(Cell.ADDED);
        processing.push(current);

        while (!processing.isEmpty()) {
            current = processing.pop();
            System.out.println("Текущая: " + current.toString());
            System.out.println("Клеток в списке: " + processing.size());
            boolean connected = false;
            for (Cell cell : current.getNeighbours(maze, 2)) {
                if (!connected && cell.getStatus() == Cell.CONNECTED) {
                    connect(current, cell, maze);
                    connected = true;
                } else if (cell.getStatus() == Cell.FREE) {
                    cell.setStatus(Cell.ADDED);
                    processing.push(cell);
                }
            }
            current.setStatus(Cell.CONNECTED);
            Collections.shuffle(processing);
        }

        //Отладочная печать: план лабиринта при взгляде "сверху"
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (maze[i][j].getType() == 0) System.out.print(" ");
                else System.out.print("#");
            }
            System.out.println();
        }

        return maze;
    }

    //Поиск пути обходом списка. Принцип похож на генерацию
    public static LinkedList<Cell> findPath(Cell start, Cell end) {
        //Делаем клетки непосещёнными
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                maze[i][j].setStatus(Cell.FREE);
            }
        }

        System.out.println("Ищу путь");

        LinkedList<Cell> path = new LinkedList<Cell>();
        Cell current = start;
        current.setStatus(Cell.ADDED);

        int sum = 0;

        while (!current.equals(end)) {
            System.out.println("Текущая: " + current.toString());
            System.out.println("Пройдено: " + sum);
            for (Cell cell : current.getNeighbours(maze, 1)) {
                if (cell.getType() == 0 && cell.getStatus() == Cell.FREE) {
                    path.push(current);
                    current = cell;
                    current.setStatus(Cell.ADDED);
                    sum++;
                } else if (!path.isEmpty()) {
                    current = path.pop();
                }
            }
        }

        return path;
    }
}
