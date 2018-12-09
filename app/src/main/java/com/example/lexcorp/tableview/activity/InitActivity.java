package com.example.lexcorp.tableview.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.lexcorp.tableview.R;

public class InitActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.init_activity);

        Intent intent = new Intent(this, MainActivity.class);

        EditText hashSize = findViewById(R.id.hash_size);

        Button start = findViewById(R.id.init_start);
        start.setOnClickListener(e -> {
            int size;
            String sizeS = hashSize.getText().toString();
            if (sizeS.matches("\\d+")){
                size = Integer.parseInt(sizeS);
                if (size < 3){
                    hashSize.setError("Размер должен быть больше 3");
                } else {
                    if (size > 16) {
                        hashSize.setError("Размер должен быть меньше 16");
                    } else {
                        intent.putExtra("size", size);
                        startActivity(intent);
                        finish();
                    }
                }
            } else {
                hashSize.setError("Это должна быть цифра");
                hashSize.setText("");
            }
        });

        Button exit = findViewById(R.id.init_exit);
        exit.setOnClickListener(e -> {
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                InitActivity.this);
        quitDialog.setTitle("Вы хотите выйти?");

        quitDialog.setPositiveButton("Таки да!", (dialog, which) -> finish());

        quitDialog.setNegativeButton("Нет", (dialog, which) -> {});

        quitDialog.show();
    }
}
