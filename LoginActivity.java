package com.rocket_autoskin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setTitle("Вход в LAPKAAPI");

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(50, 100, 50, 50);

        TextView tv = new TextView(this);
        tv.setText("Введите пароль:");
        tv.setTextSize(18);
        ll.addView(tv);

        final EditText etPass = new EditText(this);
        etPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ll.addView(etPass);

        Button btnLogin = new Button(this);
        btnLogin.setText("ВОЙТИ");

        btnLogin.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String pass = etPass.getText().toString();
					if (SecurityManager.checkPassword(pass)) {
						if (SecurityManager.isLicenseValid(LoginActivity.this)) {
							startActivity(new Intent(LoginActivity.this, MainActivity.class));
							finish();
						} else {
							Toast.makeText(LoginActivity.this, "Срок действия истек или не выдан!\nДата: " + SecurityManager.getExpireDate(LoginActivity.this), Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(LoginActivity.this, "Неверный пароль!", Toast.LENGTH_SHORT).show();
					}
				}
			});
        ll.addView(btnLogin);

        setContentView(ll);
    }
}
