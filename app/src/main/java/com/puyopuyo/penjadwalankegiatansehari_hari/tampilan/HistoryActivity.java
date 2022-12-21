package com.puyopuyo.penjadwalankegiatansehari_hari.tampilan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.puyopuyo.penjadwalankegiatansehari_hari.R;
import com.puyopuyo.penjadwalankegiatansehari_hari.data.DatabaseHelper;

import java.util.ArrayList;


public class HistoryActivity extends AppCompatActivity {
    DatabaseHelper db;
    HistoryCursorAdapter cursorAdapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        list = (ListView) findViewById(R.id.list_history);
        View noHistory = findViewById(R.id.tampilan_kosong);
        list.setEmptyView(noHistory);

        db.updateHistory24();

        tampilData();
        pemberitahuan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_hapus_history) {
            konfirmasiHapus(); //konfirmasi hapus apabila di klik
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void tampilData() {
        db = new DatabaseHelper(getApplicationContext());
        Cursor cursor = db.getAllHistory();
        cursorAdapter = new HistoryCursorAdapter(getApplicationContext(), cursor);
        list.setAdapter(cursorAdapter);
    }

    private void konfirmasiHapus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Hapus semua history?");
        builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                hapus();
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void hapus() {
        db = new DatabaseHelper(getApplicationContext());

        ArrayList<Long> idHistory = db.getAllIdHistory();
        if (idHistory.size() != 0) {
            db.deleteAllHistory();
            refresh();
            Toast.makeText(this, getString(R.string.hapus_history), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.no_history), Toast.LENGTH_SHORT).show();
        }
    }

    private void refresh() {
        db = new DatabaseHelper(getApplicationContext());
        Cursor cursor = db.getAllHistory();
        cursorAdapter.swapCursor(cursor);
        startManagingCursor(cursor);
    }

    private void pemberitahuan() {
        //buat dialog yang berisi textview
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView textView = new TextView(this);
        textView.setPadding(60, 60, 60, 0);
        textView.setText(getText(R.string.pemberitahuan));
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setLineSpacing(0, 1.5F);
        builder.setView(textView);
        builder.setPositiveButton("OK",null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
