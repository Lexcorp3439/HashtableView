package com.example.lexcorp.tableview.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lexcorp.tableview.R;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.lexcorp.tableview.Controller.showToast;
import static com.example.lexcorp.tableview.activity.MainActivity.hashTable;

public class ListAdapter extends BaseAdapter {
    private Activity activity;
    private Context context;
    private HashTable.Node[] entrys;
    private boolean[] deleted;

    private String textFilter;
    private boolean filter;

    private void setFilter() {
        setFilter(filter, textFilter);
    }

    public void setFilter(boolean filt, String textFilter) {
        this.textFilter = textFilter;
        this.filter = filt;
        this.entrys = hashTable.getNodes();
        this.deleted = hashTable.getDeleted();

        if (filt) {
            ArrayList<HashTable.Node> entrysList = new ArrayList<>();
            int i = -1;
            for (HashTable.Node entry : entrys) {
                i++;
                if (entry != null && deleted[i]
                        && ((String) entry.getKey()).contains(textFilter)) {
                    entrysList.add(entry);
                }
            }
            HashTable.Node[] newEntrys = new HashTable.Node[entrysList.size()];
            this.deleted = new boolean[entrysList.size()];
            for (int j = 0; j < deleted.length; j++) {
                deleted[j] = true;
            }
            this.entrys = entrysList.toArray(newEntrys);
        }
        notifyDataSetChanged();
    }

    public ListAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.entrys = hashTable.getNodes();
        this.deleted = hashTable.getDeleted();
    }

    @Override
    public int getCount() {
        return entrys.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SetData setData = new SetData();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert inflater != null;
        @SuppressLint({"ViewHolder", "InflateParams"})
        View view = inflater.inflate(R.layout.loginlist, null);

        setData.login = view.findViewById(R.id.login);
        String login;

        if (entrys[position] == null || !deleted[position]) {
            login = "Empty";
        } else {
            login = (String) entrys[position].getKey();
        }
        setData.login.setText(login);

        view.setOnClickListener(e -> {
            if (!login.equals("Empty")) {
                openQuitDialog(login,
                        Objects.requireNonNull(entrys[position].getValue()).toString(), position);
            } else {
                openEmptyDialog(position);
            }
        });
        return view;
    }

    class SetData {
        TextView login;
    }


    private void openQuitDialog(String login, String message, int index) {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(activity);
        quitDialog.setTitle("Login: " + login + "              " + "index = " + index);

        quitDialog.setMessage(message);

        quitDialog.setPositiveButton("Все понятно", (dialog, which) -> {
        });

        quitDialog.setNegativeButton("Удалить", (dialog, which) -> {
            hashTable.remove(login);
            setFilter();
            showToast("" + login + " удален", context);
            notifyDataSetChanged();
        });

        quitDialog.show();
    }

    private void openEmptyDialog(int index) {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(activity);
        quitDialog.setTitle("EMPTY"  + "                 " + "index = " + index);

        quitDialog.setPositiveButton("Все понятно", (dialog, which) -> {
        });


        quitDialog.show();
    }
}


//        quitDialog.setPositiveButton("Изменить", (dialog, which) -> {
//                Person prsn = hashTable.get(login);
//                MainActivity.Items.login.setText(login);
//                assert prsn != null;
//                MainActivity.Items.name.setText(prsn.getName());
//                MainActivity.Items.lastName.setText(prsn.getLastName());
//                MainActivity.Items.age.setText(prsn.getAge());
//                hashTable.remove(login);
//                setFilter();
//                notifyDataSetChanged();
//                });