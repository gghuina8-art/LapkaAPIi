package com.rocket_autoskin;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class PipetteOverlay {
    private Context context;
    private WindowManager wm;
    private TextView tvInfo;
    private boolean isShowing = false;

    public PipetteOverlay(Context c) {
        this.context = c;
        this.wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
    }

    public void show() {
        if (isShowing) return;

        tvInfo = new TextView(context);
        tvInfo.setTextColor(Color.WHITE);
        tvInfo.setTextSize(18);
        tvInfo.setPadding(30, 30, 30, 30);
        tvInfo.setText("Води пальцем по экрану\n(Нажми ДОЛГО, чтобы закрыть)");

        // Красивый полупрозрачный фон для окошка
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#CC000000")); 
        gd.setCornerRadius(20);
        tvInfo.setBackground(gd);

        // Настройки окна (поверх других приложений)
        int layoutType;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            layoutType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutType = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			layoutType,
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
			PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 50;
        params.y = 150;

        // Обработка нажатий
        tvInfo.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						int x = (int) event.getRawX();
						int y = (int) event.getRawY();
						tvInfo.setText("X: " + x + "  Y: " + y + "\n\n(Нажми ДОЛГО, чтобы закрыть)");

						// Двигаем окошко за пальцем, чтобы палец не перекрывал текст
						params.x = x - 100;
						params.y = y - 100;
						wm.updateViewLayout(tvInfo, params);
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						// Возвращаем окошко в левый верхний угол
						params.x = 50;
						params.y = 150;
						wm.updateViewLayout(tvInfo, params);
					}
					return true;
				}
			});

        // Долгое нажатие для закрытия
        tvInfo.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					hide();
					return true;
				}
			});

        try {
            wm.addView(tvInfo, params);
            isShowing = true;
        } catch (Exception e) {
            isShowing = false;
        }
    }

    public void hide() {
        if (!isShowing) return;
        try {
            wm.removeView(tvInfo);
        } catch (Exception ignored) {}
        isShowing = false;
    }
}
