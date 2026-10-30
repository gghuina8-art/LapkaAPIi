package com.rocket_autoskin;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CalibrationActivity extends Activity implements View.OnTouchListener {

    private final float[] xs = new float[7];
    private final float[] ys = new float[7];
    private int step = 0;
    private final String[] names = {
        "1/7: КУПИТЬ (кнопка)",
        "2/7: ПОДТВЕРДИТЬ (кнопка)",
        "3/7: ЛЕВЫЙ ВЕРХНИЙ УГОЛ НАКЛЕЙКИ",
        "4/7: ПРАВЫЙ НИЖНИЙ УГОЛ НАКЛЕЙКИ",
        "5/7: ГАЛОЧКА (фильтр)",
        "6/7: ОК (фильтр)",
        "7/7: НАЗАД (кнопка)",
    };
    private TextView hint;

    @Override 
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        FrameLayout fl = new FrameLayout(this);
        hint = new TextView(this);
        hint.setText(names[0]);
        hint.setTextSize(22);
        hint.setTextColor(0xFFFF0000); // Красный цвет для видимости
        hint.setPadding(40, 80, 40, 0);
        fl.addView(hint);
        fl.setOnTouchListener(this);
        setContentView(fl);
    }

    @Override 
    public boolean onTouch(View v, MotionEvent e) {
        if (e.getAction() != MotionEvent.ACTION_DOWN) return true;

        if (step < 7) {
            xs[step] = e.getX(); 
            ys[step] = e.getY();
            step++;
            if (step < 7) {
                hint.setText(names[step]);
            } else {
                saveProfile();
                finish();
            }
        }
        return true;
    }

    private void saveProfile() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Profile p = new Profile();
        p.screenW = dm.widthPixels; 
        p.screenH = dm.heightPixels;

        p.buyX = xs[0]/p.screenW; p.buyY = ys[0]/p.screenH;
        p.confirmX = xs[1]/p.screenW; p.confirmY = ys[1]/p.screenH;
        p.stickerLx = xs[2]/p.screenW; p.stickerLy = ys[2]/p.screenH;
        p.stickerRx = xs[3]/p.screenW; p.stickerRy = ys[3]/p.screenH;
        p.checkX = xs[4]/p.screenW; p.checkY = ys[4]/p.screenH;
        p.okX = xs[5]/p.screenW; p.okY = ys[5]/p.screenH;
        p.backX = xs[6]/p.screenW; p.backY = ys[6]/p.screenH;

        p.slotHeightPct = 76f / p.screenH; // 76 пикселей из твоего скрипта
        ProfileManager.save(this, p);
    }
}
