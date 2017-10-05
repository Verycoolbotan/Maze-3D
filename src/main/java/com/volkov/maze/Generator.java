package com.volkov.maze;

import java.util.LinkedList;
import java.util.ArrayList;

public class Generator {
    private static int xSize, ySize;
    static Cell[][] map;

    /*Алгоритм Прима, адаптированный под генерацию лабиринта.
    * Всё просто:
    * 1. Сначала все клетки разделены стенками
    * 2. Выбираем какую-нибудь клетку и добавляем её в список
    * 3. Пока список не пуст:
    * 3.1. Убираем текущую клетку из списка (уже обработана)
    * 3.2. Добавляем в список её соседей
    * 3.3. Соединяем случайную клетку из списка с предыдущей
    * 3.4. Убираем её из списка
    *
    * LinkedList позволяет настроить генерацию:
    * добавление в начало/конец списка меняет вид лабиринта,
    * можно перемешивать список обрабатываемых клеток*/
    public static void generate(int xSize, int ySize){
        //Генерация поля из клеток
        Generator.xSize = xSize;
        Generator.ySize = ySize;
        map = new Cell[ySize][xSize];

        for(int i = 0; i < ySize; i++){
            for(int j = 0; j < xSize; j++){
                map[i][j] = new Cell(j, i);
            }
        }

        LinkedList<Cell> processing = new LinkedList<Cell>();
        processing.add(map[0][0]);

        while(!processing.isEmpty()){
            Cell current = processing.pop();
            System.out.println("Клеток в списке: " + processing.size());
            System.out.println("Текущая клетка: " + current.getY() + ", " + current.getX());
            ArrayList<Cell> neighbours = current.getNeighbours(map);

            boolean connected = false;
            for(Cell cell : neighbours){
                //Соединяем соседа с текущей клеткой:
                if(!connected && cell.getStatus() == Cell.CONNECTED){
                    if(cell.getX() < current.getX()){
                        cell.setRightWall(false);
                    }
                    if(cell.getX() > current.getX()){
                        map[current.getY()][current.getX()].setRightWall(false);
                    }
                    if(cell.getY() < current.getY()){
                        cell.setDownWall(false);
                    }
                    if(cell.getY() > current.getY()){
                        map[current.getY()][current.getX()].setDownWall(false);
                    }
                    connected = true;
                } else if(cell.getStatus() == Cell.FREE){
                    //Запихиваем в список оставшихся соседей
                    cell.setStatus(Cell.ADDED);
                    processing.push(cell);
                    System.out.println("Добавлена: " + cell.getY() + ", " + cell.getX());
                }
            }
            current.setStatus(Cell.CONNECTED);
        }
        System.out.println("Успешная генерация");
    }

    //TODO: Эта хрень забагована, и её надо переписать
    public static int[][] toArray(){
        //Помним, что стенки у нас толстые
        int[][] result = new int[2 * ySize + 1][2 * xSize + 1];

        //Сначала клетки разделены стенками
        for(int i = 0; i < result.length; i++){
            for(int j = 0; j < result[0].length; j++){
                if(i % 2 == 0) result[i][j] = 1;
                if(j % 2 == 0) result[i][j] = 1;
            }
        }

        //Теперь убираем стенки в соответствии с тем, что мы нагенерировали
        for(int i = 0; i < ySize; i++){
            for(int j = 0; j < xSize; j++){
                if(!map[i][j].isRightWall()){
                    result[i + 1][j + 2] = 0;
                }
                if(!map[i][j].isDownWall()){
                    result[i + 2][j + 1] = 0;
                }
            }
        }
        System.out.println("Успешный перевод в массив:");
        for(int i = 0; i < result.length; i++){
            for(int j = 0; j < result[0].length; j++){
                System.out.print(result[i][j]);
            }
            System.out.println();
        }
        return result;
    }
}