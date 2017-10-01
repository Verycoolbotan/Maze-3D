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
    * 3.4. Убираем её из списка*/
    private static void generate(int xSize, int ySize){
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

        }

    }


}
