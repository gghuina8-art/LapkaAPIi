package com.rocket_autoskin;

public class Profile {
    public int screenW, screenH;

    // Координаты в процентах (от 0.0 до 1.0)
    public float buyX, buyY;
    public float confirmX, confirmY;
    public float stickerLx, stickerLy, stickerRx, stickerRy;
    public float checkX, checkY;
    public float okX, okY;
    public float backX, backY;
    public float slotHeightPct;
    public int slots = 3;

    // Вспомогательный метод: переводит процент в пиксели
    public int px(float pct, int dim) { 
        return Math.round(pct * dim); 
    }

    public int buyPxX() { 
        return px(buyX, screenW); 
    }

    public int buyPxY(int i) { 
        return px(buyY + i * slotHeightPct, screenH); 
    }
}
