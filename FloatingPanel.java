package com.rocket_autoskin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.media.projection.MediaProjectionManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FloatingPanel {
    private Context context;
    private WindowManager wm;
    private LinearLayout panel;
    private boolean isShowing = false;

    public FloatingPanel(Context c) {
        this.context = c;
        this.wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
    }

    public void show() {
        if (isShowing) return;

        panel = new LinearLayout(context);
        panel.setOrientation(LinearLayout.HORIZONTAL);
        panel.setGravity(Gravity.CENTER_VERTICAL);
        panel.setPadding(20, 20, 20, 20);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#228B22"));
        gd.setCornerRadius(30);
        panel.setBackground(gd);

        int size = Prefs.getIconSize(context);
        int pxSize = (int) (size * context.getResources().getDisplayMetrics().density);

        ImageView btnPlay = new ImageView(context);
        btnPlay.setImageResource(android.R.drawable.ic_media_play);
        btnPlay.setColorFilter(Color.WHITE);
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(pxSize / 2, pxSize / 2);
        p1.setMargins(0, 0, 20, 0);
        btnPlay.setLayoutParams(p1);

        btnPlay.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ProfileManager.load(context) == null) {
						Toast.makeText(context, "Сначала калибровка!", Toast.LENGTH_SHORT).show();
						return;
					}
					if (!ClickerService.ready()) {
						Toast.makeText(context, "Включите Спец. возможности!", Toast.LENGTH_SHORT).show();
						return;
					}
					MediaProjectionManager mpm = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
					if (context instanceof Activity) {
						((Activity) context).startActivityForResult(mpm.createScreenCaptureIntent(), 2);
					}
				}
			});

        ImageView btnStop = new ImageView(context);
        btnStop.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        btnStop.setColorFilter(Color.WHITE);
        btnStop.setLayoutParams(new LinearLayout.LayoutParams(pxSize / 2, pxSize / 2));

        btnStop.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					context.stopService(new Intent(context, AutoBuyService.class));
					hide();
					Toast.makeText(context, "Остановлено", Toast.LENGTH_SHORT).show();
				}
			});

        panel.addView(btnPlay);
        panel.addView(btnStop);

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
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
			PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 200;
        params.y = 400;

        panel.setOnTouchListener(new View.OnTouchListener() {
				private int initialX;
				private int initialY;
				private float initialTouchX;
				private float initialTouchY;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							initialX = params.x;
							initialY = params.y;
							initialTouchX = event.getRawX();
							initialTouchY = event.getRawY();
							return true;
						case MotionEvent.ACTION_UP:
							return true;
						case MotionEvent.ACTION_MOVE:
							params.x = initialX + (int) (event.getRawX() - initialTouchX);
							params.y = initialY + (int) (event.getRawY() - initialTouchY);
							wm.updateViewLayout(panel, params);
							return true;
					}
					return false;
				}
			});

        try {
            wm.addView(panel, params);
            isShowing = true;
        } catch (Exception e) {
            isShowing = false;
        }
    }

    public void hide() {
        if (!isShowing) return;
        try {
            wm.removeView(panel);
        } catch (Exception ignored) {}
        isShowing = false;
    }
}
