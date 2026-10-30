package com.rocket_autoskin;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static final String NAME = "rocket_prefs";
    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    // Настройка: сколько слотов (наклеек) искать
    public static int getSlots(Context c) {
        return sp(c).getInt("slots", 3); // по умолчанию 3 (как в скрипте)
    }
    public static void setSlots(Context c, int v) {
        sp(c).edit().putInt("slots", v).apply();
    }

    // Настройка: размер плавающей кнопки
    public static int getIconSize(Context c) {
        return sp(c).getInt("icon_size", 60); // в dp
    }
    public static void setIconSize(Context c, int v) {
        sp(c).edit().putInt("icon_size", v).apply();
    }
}
