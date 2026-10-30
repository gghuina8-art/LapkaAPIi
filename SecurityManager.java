package com.rocket_autoskin;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

public class SecurityManager {
    private static final String PREF_NAME = "lapka_secure";

    // Мастер-пароль для входа (хранится в коде, юзер его не увидит)
    // Для смены пароля просто поменяй слово "LAPKA2026" на свое.
    private static final String MASTER_PASSWORD = "topik090";

    // Проверка пароля
    public static boolean checkPassword(String input) {
        return input.equals(MASTER_PASSWORD);
    }

    // Установка срока действия (в днях)
    public static void setLicenseDays(Context c, int days) {
        SharedPreferences sp = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, days);

        long expireTime = cal.getTimeInMillis();
        sp.edit().putLong("expire_time", expireTime).apply();
    }

    // Проверка: не истек ли срок
    public static boolean isLicenseValid(Context c) {
        SharedPreferences sp = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long expireTime = sp.getLong("expire_time", 0);

        if (expireTime == 0) return false; // Лицензия не выдана
        return System.currentTimeMillis() < expireTime;
    }

    // Получить красивую дату окончания
    public static String getExpireDate(Context c) {
        SharedPreferences sp = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long expireTime = sp.getLong("expire_time", 0);
        if (expireTime == 0) return "Нет лицензии";

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(expireTime));
    }
}
