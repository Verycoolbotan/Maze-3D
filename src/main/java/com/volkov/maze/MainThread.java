package com.volkov.maze;

import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class MainThread extends JFrame implements Runnable {
    private boolean isRunning;
    private Camera camera;

    private int screenWidth, screenHeight;

    private int[][] map = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 2, 0, 5, 0, 8, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 3, 0, 6, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 4, 0, 7, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    /*Если заливать стены сплошным цветом, то достаточно просто
    * рисовать на экране вертикальные линии нужной высоты, независимо
    * от способа отрисовки. С текстурированными стенами немного сложнее:
    * на экран выводится объект image, цвет каждой точки которого
    * задаётся набором целых чисел из массива buffer*/
    private BufferedImage image;
    public int[] buffer;

    public MainThread(int screenWidth, int screenHeight){
        super("Maze 3D");

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        //Я не использую альфа-канал, но это модель по умолчанию
        image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        buffer = ((DataBufferInt)(image.getData()).getDataBuffer()).getData();
        camera = new Camera(1.5, 1.5, screenWidth, screenHeight);
        isRunning = true;

        addKeyListener(camera);

        setSize(screenWidth, screenHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(java.awt.Color.black);
        setResizable(false);
        setVisible(true);

        createBufferStrategy(2);

        run();
    }

    /*Двойная буферизация.
    * Сначала мы рисуем в буфере, а затем этот буфер выводится на экран.
    * Это позволяет заметно увеличить плавность картинки, а если стены
    * текстурированы, то без буфера не обойтись*/
    private void update(){
        BufferStrategy strategy = getBufferStrategy();
        if(strategy == null){
            createBufferStrategy(2);
        }
        Graphics g = strategy.getDrawGraphics();
        camera.raycast(map, buffer);
        image.setRGB(0, 0, screenWidth, screenHeight, buffer, 0, screenWidth);
        g.drawImage(image, 0, 0, null);
        strategy.show();
    }

    public void run() {
        long lastTime = System.currentTimeMillis();
        final int frameTime = 1000 / 60;
        int difference;

        while(isRunning){
            long currentTime = System.currentTimeMillis();
            difference = (int)(currentTime - lastTime);
            if(difference < frameTime){
                try{
                    Thread.sleep(difference);
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            lastTime = currentTime;
            try{
                camera.update(map);
                update();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]){
        MainThread thread = new MainThread(1280, 720);
    }
}
