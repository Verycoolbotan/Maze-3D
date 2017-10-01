package com.volkov.maze;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Camera implements KeyListener {
    //TODO: Сделать скорости зависимыми от времени отрисовки
    private final double MOVE_SPEED = 0.05;
    private final double ROTATION_SPEED = 5;
    ArrayList<int[]> textures;
    private double posX, posY, dirX, dirY, planeX, planeY;
    private int screenWidth, screenHeight;
    private boolean forward, backward, left, right;

    public Camera(double posX, double posY, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.posX = posX;
        this.posY = posY;
        dirX = 1;
        dirY = 0;
        planeX = 0;
        planeY = 1;
        textures = Texture.getTextures("wolftextures.png");
    }

    private int scale(int value, int oldMin, int oldMax, int newMax) {
        return newMax * (value - oldMin) / (oldMax - oldMin);
    }

    /*Начинается мясо, ой, то есть волшебство. Собственно здесь и происходит
    * трассировка луча и обновление значений в экранном буфере. Меня конкретно
    * смущает объём получившегося метода*/
    public void raycast(int[][] map, int[] buffer) throws ArrayIndexOutOfBoundsException {

        //Чистим буфер перед отрисовкой кадра
        for (int i = 0; i < buffer.length; i++) {
            /*Цвет кодируется 32-х битным int, где первые 8 бит - прозрачность.
            * Если я ничего не перепутал, то первые 8 бит двоичного дополнительного
            * кода этого числа - единицы, остальные - нули. Получаем непрозрачный чёрный.*/
            buffer[i] = -16777216;
        }

        for (int i = 0; i < screenWidth; i++) {
            double distance;
            //X-координата в пространстве камеры. Понадобится для изменения направления луча
            double cameraX = 2 * (double) (i) / screenWidth - 1;
            double rayPosX = posX;
            double rayPosY = posY;
            double rayDirX = dirX + planeX * cameraX;
            double rayDirY = dirY + planeY * cameraX;

            /*Следующие две строки вычисляют тангенс и котангенс угла между полложительным
            * направлением оси OX и направлением взгляда. В теории можно напрямую работать
            * с углами, однако реализация через векторы проще - нет проблем с тангенсами.*/

            //Какое расстояние должен пройти луч от текущей точки до пересечения с вертикальной линией сетки
            double deltaDistX = Math.sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
            //Какое расстояние должен пройти луч от текущей точки до пересечения с горизонтальной линией сетки
            double deltaDistY = Math.sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));

            //В каком направлении по оси трассировать луч (положительном или отрицательном)
            int stepX, stepY;
            //Расстояния от позиции камеры до ПЕРВЫХ пересечений с сеткой
            double sideDistX, sideDistY;

            //С какой стороны луч пересечёт клетку: 0 - СЮ, 1 - ВЗ
            int side = 0;

            int mapX = (int) (rayPosX);
            int mapY = (int) (rayPosY);

            if (rayDirX < 0) {
                stepX = -1;
                sideDistX = (rayPosX - mapX) * deltaDistX;
            } else {
                stepX = 1;
                //Добавляем 1, т.к. дробная часть попросту отбрасывается
                sideDistX = (mapX + 1.0 - rayPosX) * deltaDistX;
            }

            if (rayDirY < 0) {
                stepY = -1;
                sideDistY = (rayPosY - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1.0 - rayPosY) * deltaDistY;
            }

            //Трассируем луч до тех пор, пока не встретим стену.
            while (map[mapX][mapY] == 0) {
                //Прыгаем к следующему квадрату только В ОДНОМ из направлений
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                } else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }
            }
            /*Считаем проекцию расстояния на направление взгляда для избавления от искажений
            * Добавляем 1 в зависимости от того, с какой стороны мы пересекли клетку (см. выше)
            * map_ - rayPos_ + (1 - step_) / 2 - число пройденных квадратов
            * Деление на компоненту вектора - то же, что и домножение на косинус угла между осью и лучом*/
            if (side == 0) distance = (mapX - rayPosX + (1 - stepX) / 2) / rayDirX;
            else distance = (mapY - rayPosY + (1 - stepY) / 2) / rayDirY;

            int lineHeight = (int) (screenHeight / distance);

            //Считаем верхнюю и нижнюю границы столбца
            int drawStart = -lineHeight / 2 + screenHeight / 2;
            int startOffset = 0;
            if (drawStart < 0) {
                startOffset = drawStart;
                drawStart = 0;
            }
            int drawEnd = lineHeight / 2 + screenHeight / 2;
            int endOffset = 0;
            if (drawEnd >= screenHeight) {
                endOffset = drawEnd;
                drawEnd = screenHeight - 1;
            }

            //Теперь можно использовать текстуру из нулевой ячейки массива
            int texNum = map[mapX][mapY] - 1;
            int[] texture = textures.get(texNum);

            /*Точная координата пересечения луча и стены.
            * Вернее, смещение относительно линии сетки.
            * Умножение на компоненту направления равносильно умножению на косинус угла*/
            double wallX;
            if (side == 0) wallX = rayPosY + distance * rayDirY;
            else wallX = rayPosX + distance * rayDirX;
            wallX -= Math.floor(wallX);

            int texWH = (int) (Math.sqrt(texture.length));
            //Какой столбец текстуры рисуем
            int texX = (int) (wallX * (double) (texWH));
            if (side == 0 && rayDirX > 0) texX = texWH - texX - 1;
            if (side == 1 && rayDirY < 0) texX = texWH - texX - 1;

            for (int y = drawStart; y < drawEnd; y++) {
                /*Сопоставляем каждой точке столбца экранв точку текстуры
                * Существуют более компактные целочисленные реализации,
                * но как они работают - загадка (по крайней мере для меня)*/
                int texY;
                if (startOffset == 0) texY = scale(y, drawStart, drawEnd, texWH - 1);
                else texY = scale(y, startOffset, endOffset, texWH - 1);
                int color = texture[texWH * texY + texX];
                /*Усилим эффект трёхмерности окружения: сделаем цвета на "горизонтальных"
                * или "вертикальных" сторонах темнее.
                * В такие моменты отсутствие двоичных литералов начинает бесить.
                * Самый простой способ сделать цвет темнее - разделить каждую компоненту
                * на 2 битовым сдвигом. Старшие биты необходимо обнулить:
                * 8355711 это 01111111 01111111 01111111 01111111 в двоичном представлении.
                * Помним, что первый байт - альфа-канал, все цвета должны быть непрозрачными.
                * -16777216: первый байт - единицы, остальные - нули*/
                if(side == 0) color = (color >> 1) & 8355711 | -16777216;
                buffer[screenWidth * y + i] = color;
            }
        }
    }

    /*KeyListener - вещь довольно низкоуровневая, в Java 8 появляется
    * более предпочтительный механизм -  Key Bindings API, но т.к. я
    * рисую прямо на JFrame, который наследуется не от JComponent,
    * то использовать его не получится*/

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_W))
            forward = true;
        if ((e.getKeyCode() == KeyEvent.VK_A))
            left = true;
        if ((e.getKeyCode() == KeyEvent.VK_S))
            backward = true;
        if ((e.getKeyCode() == KeyEvent.VK_D))
            right = true;
    }

    public void keyReleased(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_W))
            forward = false;
        if ((e.getKeyCode() == KeyEvent.VK_A))
            left = false;
        if ((e.getKeyCode() == KeyEvent.VK_S))
            backward = false;
        if ((e.getKeyCode() == KeyEvent.VK_D))
            right = false;
    }

    public void update(int[][] map) throws ArrayIndexOutOfBoundsException{
        //Я долго не мог понять, почему же не получается повернуть камеру
        double oldDirX = dirX;
        double oldDirY = dirY;
        double oldPlaneX = planeX;
        double oldPlaneY = planeY;

        if(forward){
            //Перемещаемся, если в заданном направлении нет стен
            if(map[(int)(posX + dirX * MOVE_SPEED)][(int)(posY)] == 0) posX += dirX * MOVE_SPEED;
            if(map[(int)(posX)][(int)(posY + dirY * MOVE_SPEED)] == 0) posY += dirY * MOVE_SPEED;
        }
        if(backward){
            if(map[(int)(posX - dirX * MOVE_SPEED)][(int)(posY)] == 0) posX -= dirX * MOVE_SPEED;
            if(map[(int)(posX)][(int)(posY - dirY * MOVE_SPEED)] == 0) posY -= dirY * MOVE_SPEED;
        }

        if(right){
            dirX = oldDirX * Math.cos(Math.toRadians(ROTATION_SPEED)) - oldDirY * Math.sin(Math.toRadians(ROTATION_SPEED));
            dirY = oldDirX * Math.sin(Math.toRadians(ROTATION_SPEED)) + oldDirY * Math.cos(Math.toRadians(ROTATION_SPEED));
            planeX = oldPlaneX * Math.cos(Math.toRadians(ROTATION_SPEED)) - oldPlaneY * Math.sin(Math.toRadians(ROTATION_SPEED));
            planeY = oldPlaneX * Math.sin(Math.toRadians(ROTATION_SPEED)) + oldPlaneY * Math.cos(Math.toRadians(ROTATION_SPEED));
        }
        if(left){
            dirX = oldDirX * Math.cos(Math.toRadians(-ROTATION_SPEED)) - oldDirY * Math.sin(Math.toRadians(-ROTATION_SPEED));
            dirY = oldDirX * Math.sin(Math.toRadians(-ROTATION_SPEED)) + oldDirY * Math.cos(Math.toRadians(-ROTATION_SPEED));
            planeX = oldPlaneX * Math.cos(Math.toRadians(-ROTATION_SPEED)) - oldPlaneY * Math.sin(Math.toRadians(-ROTATION_SPEED));
            planeY = oldPlaneX * Math.sin(Math.toRadians(-ROTATION_SPEED)) + oldPlaneY * Math.cos(Math.toRadians(-ROTATION_SPEED));
        }
    }
}

