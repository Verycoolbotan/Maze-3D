package com.volkov.maze;

import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

//Получаем список текстур стен из файла. Текстуры квадратные, зачем-то склеены в одну большую
public class Texture {
    public static int[] load(BufferedImage image, int offset) {
        int[] data = null;

        try {
            int wh = image.getHeight();
            data = (image.getSubimage(offset * wh, 0, wh, wh)).getRGB(0, 0, wh, wh, data, 0, wh);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static ArrayList<int[]> getTextures(String path) {
        BufferedImage source = null;
        ArrayList<int[]> textures = new ArrayList<int[]>();
        try {
            source = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int width = source.getWidth();
        int height = source.getHeight();

        for (int i = 0; i < (width / height); i++) {
            textures.add(load(source, i));
        }
        return textures;
    }
}
