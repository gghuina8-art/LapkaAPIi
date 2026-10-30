package com.rocket_autoskin;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int REQ_CAL = 1;
    private static final int REQ_PROJ = 2;
    private static final int REQ_OVERLAY = 3;

    private PipetteOverlay pipette;
    private FloatingPanel floatPanel;

    @Override 
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.main);

        pipette = new PipetteOverlay(this);
        floatPanel = new FloatingPanel(this);

        // --- СЕКРЕТНЫЙ ВХОД В НАСТРОЙКИ (5 кликов по заголовку) ---
        TextView title = (TextView) findViewById(R.id.tvTitle);
        final int[] clicks = {0};
        final long[] lastClickTime = {0};

        if (title != null) {
            title.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						long now = System.currentTimeMillis();
						if (now - lastClickTime[0] < 1000) {
							clicks[0]++;
							if (clicks[0] >= 5) {
								startActivity(new Intent(MainActivity.this, SettingsActivity.class));
								clicks[0] = 0;
							}
						} else {
							clicks[0] = 1;
						}
						lastClickTime[0] = now;
					}
				});
        }

        Button btnRun = (Button) findViewById(R.id.btnRun);
        Button btnCal = (Button) findViewById(R.id.btnCal);
        Button btnPipette = (Button) findViewById(R.id.btnPipette);
        Button btnAcc = (Button) findViewById(R.id.btnAcc);
        Button btnStop = (Button) findViewById(R.id.btnStop);

        // 1. Запуск
        btnRun.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ProfileManager.load(MainActivity.this) == null) {
						Toast.makeText(MainActivity.this, "Сначала сделайте калибровку!", Toast.LENGTH_SHORT).show();
						return;
					}
					if (!ClickerService.ready()) {
						Toast.makeText(MainActivity.this, "Включите Спец. возможности!", Toast.LENGTH_SHORT).show();
						return;
					}
					MediaProjectionManager mpm = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
					startActivityForResult(mpm.createScreenCaptureIntent(), REQ_PROJ);
				}
			});

        // 2. Калибровка
        btnCal.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivityForResult(new Intent(MainActivity.this, CalibrationActivity.class), REQ_CAL);
				}
			});

        // 3. Пипетка
        btnPipette.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
						Toast.makeText(MainActivity.this, "Разрешите показ поверх других окон", Toast.LENGTH_LONG).show();
						Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
						startActivityForResult(intent, REQ_OVERLAY);
					} else {
						pipette.show();
						Toast.makeText(MainActivity.this, "Запусти игру! Пипетка покажет координаты.", Toast.LENGTH_LONG).show();
						moveTaskToBack(true);
					}
				}
			});

        // 4. Спец. возможности
        btnAcc.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
				}
			});

        // 5. Стоп
        btnStop.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					stopService(new Intent(MainActivity.this, AutoBuyService.class));
					pipette.hide();
					floatPanel.hide();
					Toast.makeText(MainActivity.this, "Остановлено", Toast.LENGTH_SHORT).show();
				}
			});
    }

    @Override 
    protected void onActivityResult(int req, int code, Intent data) {
        super.onActivityResult(req, code, data);

        if (req == REQ_PROJ && code == RESULT_OK) {
            Intent i = new Intent(this, AutoBuyService.class);
            i.putExtra("proj_code", code);
            i.putExtra("proj_data", data);
            startForegroundService(i);

            floatPanel.show();
            moveTaskToBack(true);

            Toast.makeText(this, "Скрипт запущен!", Toast.LENGTH_SHORT).show();
        }

        if (req == REQ_OVERLAY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                pipette.show();
                moveTaskToBack(true);
            } else {
                Toast.makeText(this, "Без разрешения пипетка не работает", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
