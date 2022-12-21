package com.puyopuyo.penjadwalankegiatansehari_hari.tampilan;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.puyopuyo.penjadwalankegiatansehari_hari.R;
import com.puyopuyo.penjadwalankegiatansehari_hari.data.DatabaseHelper;

public class Kamis extends Fragment {

    DatabaseHelper db;
    JadwalCursorAdapter cursorAdapter;
    private ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.kamis, container, false);

        list = (ListView) rootView.findViewById(R.id.list);
        View noJadwal = rootView.findViewById(R.id.tampilan_kosong);
        list.setEmptyView(noJadwal); //set tampilan saat tidak ada jadwal

        tampilData(); //menampilkan data list dari database

        //klik item list untuk update
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), UpdateJadwal.class);
                intent.putExtra("_id", id);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void tampilData() {
        db = new DatabaseHelper(getContext().getApplicationContext());
        Cursor cursor = db.getJadwalHari("Kamis");
        cursorAdapter = new JadwalCursorAdapter(getContext().getApplicationContext(), cursor);
        list.setAdapter(cursorAdapter);
    }

    public void refresh() {
        db = new DatabaseHelper(getContext().getApplicationContext());
        Cursor cursor = db.getJadwalHari("Kamis");
        cursorAdapter.swapCursor(cursor);
        getActivity().startManagingCursor(cursor);
    }

    /**
     * refresh data setelah input
     */
    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
}
