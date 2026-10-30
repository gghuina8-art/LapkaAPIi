package com.rocket_autoskin;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ScreenAnalyzer {

    private Bitmap screen;

    // Передаем сюда свежий скриншот
    public void setFrame(Bitmap bmp) { 
        this.screen = bmp; 
    }

    // Аналог pfc.getColor(x,y) - возвращает цвет в формате 0xRRGGBB
    public int getColor(int x, int y) {
        if (screen == null || x < 0 || y < 0 
			|| x >= screen.getWidth() || y >= screen.getHeight())
            return 0xFFFFFF; // если мимо экрана, возвращаем белый
        return screen.getPixel(x, y) & 0xFFFFFF;
    }

    // Аналог pfc.getContoursCount(...) - считает перепады яркости (контуры)
    public int getContoursCount(int lx, int ly, int rx, int ry) {
        if (screen == null) return 0;
        int count = 0;
        int prev = -1;

        // Проходим по области и ищем резкие изменения цвета (границы наклейки)
        for (int y = ly; y < ry; y++) {
            for (int x = lx; x < rx; x++) {
                int c = screen.getPixel(x, y) & 0xFFFFFF;
                int lum = (Color.red(c) + Color.green(c) + Color.blue(c)) / 3;
                if (prev != -1 && Math.abs(lum - prev) > 60) count++;
                prev = lum;
            }
        }
        return count;
    }
}
