package com.rocket_autoskin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(50, 50, 50, 50);
        scroll.addView(ll);

        // --- Настройка 1: Количество слотов ---
        TextView tv1 = new TextView(this);
        tv1.setText("Количество слотов (наклеек):");
        tv1.setTextSize(18);
        ll.addView(tv1);

        final RadioGroup rgSlots = new RadioGroup(this);
        rgSlots.setOrientation(RadioGroup.HORIZONTAL);

        RadioButton rb2 = new RadioButton(this); rb2.setText("2"); rb2.setId(2);
        RadioButton rb3 = new RadioButton(this); rb3.setText("3"); rb3.setId(3);
        RadioButton rb4 = new RadioButton(this); rb4.setText("4"); rb4.setId(4);

        rgSlots.addView(rb2);
        rgSlots.addView(rb3);
        rgSlots.addView(rb4);

        int currentSlots = Prefs.getSlots(this);
        if (currentSlots == 2) rb2.setChecked(true);
        else if (currentSlots == 4) rb4.setChecked(true);
        else rb3.setChecked(true);

        ll.addView(rgSlots);

        // --- Настройка 2: Размер кнопки ---
        TextView tv2 = new TextView(this);
        tv2.setText("\nРазмер плавающей кнопки (в dp):");
        tv2.setTextSize(18);
        ll.addView(tv2);

        final RadioGroup rgSize = new RadioGroup(this);
        rgSize.setOrientation(RadioGroup.HORIZONTAL);

        RadioButton rbSmall = new RadioButton(this); rbSmall.setText("Маленькая"); rbSmall.setId(40);
        RadioButton rbMed = new RadioButton(this); rbMed.setText("Средняя"); rbMed.setId(60);
        RadioButton rbLarge = new RadioButton(this); rbLarge.setText("Большая"); rbLarge.setId(80);

        rgSize.addView(rbSmall);
        rgSize.addView(rbMed);
        rgSize.addView(rbLarge);

        int currentSize = Prefs.getIconSize(this);
        if (currentSize == 40) rbSmall.setChecked(true);
        else if (currentSize == 80) rbLarge.setChecked(true);
        else rbMed.setChecked(true);

        ll.addView(rgSize);

        // --- Кнопка Сохранить ---
        Button btnSave = new Button(this);
        btnSave.setText("СОХРАНИТЬ");
        btnSave.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int selectedSlot = rgSlots.getCheckedRadioButtonId();
					if (selectedSlot != -1) Prefs.setSlots(SettingsActivity.this, selectedSlot);

					int selectedSize = rgSize.getCheckedRadioButtonId();
					if (selectedSize != -1) Prefs.setIconSize(SettingsActivity.this, selectedSize);

					Toast.makeText(SettingsActivity.this, "Настройки сохранены!", Toast.LENGTH_SHORT).show();
				}
			});
        ll.addView(btnSave);

        // --- АДМИН-ПАНЕЛЬ: ВЫДАЧА ЛИЦЕНЗИИ ---
        TextView tvAdmin = new TextView(this);
        tvAdmin.setText("\n--- АДМИН: ВЫДАТЬ ЛИЦЕНЗИЮ ---\n(Действует до: " + SecurityManager.getExpireDate(this) + ")");
        tvAdmin.setTextColor(0xFFFF0000);
        tvAdmin.setTextSize(16);
        ll.addView(tvAdmin);

        Button btn1d = new Button(this); btn1d.setText("На 1 день");
        btn1d.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SecurityManager.setLicenseDays(SettingsActivity.this, 1);
					Toast.makeText(SettingsActivity.this, "Выдано на 1 день", Toast.LENGTH_SHORT).show();
				}
			});
        ll.addView(btn1d);

        Button btn7d = new Button(this); btn7d.setText("На 7 дней");
        btn7d.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SecurityManager.setLicenseDays(SettingsActivity.this, 7);
					Toast.makeText(SettingsActivity.this, "Выдано на 7 дней", Toast.LENGTH_SHORT).show();
				}
			});
        ll.addView(btn7d);

        Button btn30d = new Button(this); btn30d.setText("На 30 дней");
        btn30d.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SecurityManager.setLicenseDays(SettingsActivity.this, 30);
					Toast.makeText(SettingsActivity.this, "Выдано на 30 дней", Toast.LENGTH_SHORT).show();
				}
			});
        ll.addView(btn30d);

        setContentView(scroll);
    }
}
