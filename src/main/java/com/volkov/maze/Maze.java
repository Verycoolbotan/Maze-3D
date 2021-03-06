package com.volkov.maze;

import java.util.Collections;
import java.util.HashMap;
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
    }

    //Может использоваться для выбора начала обхода при генерации. Возвращает случайное целое на отрезке [a, b]
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
    * преимущественно вертикальных/горизонтальных коридоров)*/
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
            boolean connected = false;
            //В данном случае нас интересуют ТОЛЬКО СТЕНЫ
            for (Cell cell : current.getNeighbours(maze, 2, false)) {
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

        maze[height - 1][width - 2].setType(2);
        maze[height - 2][width - 1].setType(2);

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

    /*Поиск пути
    * Я долго не мог выбрать между А* и обходом в ширину,
    * но таки выбрал обход в ширину*/
    public static LinkedList<Cell> findPath(Cell start, Cell end) {
        LinkedList<Cell> front = new LinkedList<Cell>();
        front.add(start);
        //Ключ - текущая клетка. Значение - предыдущая клетка
        HashMap<Cell, Cell> cameFrom = new HashMap<Cell, Cell>();
        //Логично. Начало же
        cameFrom.put(start, start);
        Cell current;

        /*Очередь cameFrom хранит направление к стартовой клетке,
        * т.е. пути ещё нет, но его можно получить, двигаясь "по стрелкам"
        * от выхода к началу*/
        while (!front.isEmpty()) {
            current = front.poll();
            //А тут нам нужны ТОЛЬКО те клетки, по которым МОЖНО ПРОЙТИ
            for (Cell next : current.getNeighbours(maze, 1, true)) {
                if (!cameFrom.containsKey(next)) {
                    front.add(next);
                    cameFrom.put(next, current);
                }
            }
        }

        current = end;
        LinkedList<Cell> path = new LinkedList<Cell>();
        path.add(current);

        while (!current.equals(start)) {
            current = cameFrom.get(current);
            path.add(current);
        }
        if (!path.isEmpty()) path.pollLast();

        for (Cell cell : path){
            for (Cell c : cell.getNeighbours(maze, 1, false)){
                if (c.getType() != 0) c.setType(3);
            }
        }

        return path;
    }
}