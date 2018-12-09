package com.example.lexcorp.tableview.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.ListView;

import com.example.lexcorp.tableview.Controller;
import com.example.lexcorp.tableview.R;
import com.example.lexcorp.tableview.model.HashTable;
import com.example.lexcorp.tableview.model.ListAdapter;
import com.example.lexcorp.tableview.model.Person;
import com.example.lexcorp.tableview.model.SecondHash;


public class MainActivity extends Activity {

    private static SecondHash<String> secondHash = value -> {
        int h = 0;
        int length = value.length() >> 1;
        for (int i = 0; i < length; i++) {
            h = 33 * h + value.charAt(i);
        }
        return h;
    };

    public static HashTable<String, Person> hashTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        int size = getIntent().getIntExtra("size", 0);
        System.out.println(size);
        hashTable = new HashTable<>(size , secondHash);

        ListAdapter adapter = new ListAdapter(getApplicationContext(), this);

        Controller.run(this, adapter);
        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                MainActivity.this);
        quitDialog.setTitle("Вы хотите выйти?");

        quitDialog.setPositiveButton("Таки да!", (dialog, which) -> finish());

        quitDialog.setNegativeButton("Нет", (dialog, which) -> {});

        quitDialog.show();
    }
}
