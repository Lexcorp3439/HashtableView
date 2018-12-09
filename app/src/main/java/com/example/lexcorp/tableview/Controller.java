package com.example.lexcorp.tableview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lexcorp.tableview.model.ListAdapter;
import com.example.lexcorp.tableview.model.Person;
import com.example.lexcorp.tableview.model.RandomString;

import java.util.Random;

import static com.example.lexcorp.tableview.activity.MainActivity.hashTable;

public class Controller {

    public static void run(Activity activity, ListAdapter adapter){
        new Controller(activity, adapter);
    }

    private EditText login;
    private EditText name;
    private EditText lastName;
    private EditText age;
    private EditText search;
    private RandomString randomString = new RandomString(6);
    private Random randomInt = new Random();

    private Controller(Activity activity, ListAdapter adapter) {

        login = activity.findViewById(R.id.email);
        name = activity.findViewById(R.id.name);
        lastName = activity.findViewById(R.id.lastname);
        age = activity.findViewById(R.id.age);
        search = activity.findViewById(R.id.search);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")){
                    adapter.setFilter(true, s.toString());
                } else {
                    adapter.setFilter(false, s.toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button add = activity.findViewById(R.id.add);
        add.setOnClickListener(e ->{
            String loginN = login.getText().toString();
            if (validation(loginN)){
                Person person = new Person(name.getText().toString(),
                        lastName.getText().toString(),
                        age.getText().toString());
                hashTable.put(login.getText().toString(), person);
                login.setText("");
                name.setText("");
                lastName.setText("");
                age.setText("");
                search.setText("");
                adapter.setFilter(false, "");
                showToast("" + loginN + " добавлен", activity.getApplicationContext());
                adapter.notifyDataSetChanged();
            }
        });

        Button generate = activity.findViewById(R.id.random);
        generate.setOnClickListener(e -> {
            String login = randomString.nextString();
            while (hashTable.containsKey(login)){
                login = randomString.nextString();
            }
            String name = randomString.nextString();
            String lastName = randomString.nextString();
            int age = 1 + randomInt.nextInt(99);
            Person person = new Person(name, lastName,
                    Integer.toString(age));
            hashTable.put(login, person);
            showToast("" + login + " сгенерирован", activity.getApplicationContext());
            search.setText("");
            adapter.setFilter(false, "");
            adapter.notifyDataSetChanged();
        });


        Button clearAll = activity.findViewById(R.id.clear);
        clearAll.setOnClickListener(e ->{
            hashTable.clear();
            login.setText("");
            name.setText("");
            lastName.setText("");
            age.setText("");
            search.setText("");
            adapter.setFilter(false, "");
            showToast("Все очищено", activity.getApplicationContext());
            adapter.notifyDataSetChanged();
        });
    }

    @SuppressLint("SetTextI18n")
    private boolean validation(String loginT){
        if (!isEmailValid(loginT)) {
            if (hashTable.containsKey(loginT)){
                login.setError("This person contain yet");
            } else {
                login.setError("login is empty");
            }
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email){
        return email.length() >= 1 && hashTable.contains(email) <= 0;
    }



    @SuppressWarnings("SameParameterValue")
    public static void showToast(String msg, Context context) {
        Toast toast = Toast.makeText(context,
                msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }
}
