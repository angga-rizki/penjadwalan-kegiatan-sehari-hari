package com.puyopuyo.penjadwalankegiatansehari_hari.tampilan;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.puyopuyo.penjadwalankegiatansehari_hari.R;

class JadwalCursorAdapter extends CursorAdapter {

    private TextView tvJam, tvNama, tvWaktuPengingat, tvPengulangan;
    private ImageView imageAktif;

    JadwalCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        tvJam = (TextView) view.findViewById(R.id.jam);
        tvNama = (TextView) view.findViewById(R.id.nama_jadwal);
        tvWaktuPengingat = (TextView) view.findViewById(R.id.waktu_pengingat);
        tvPengulangan = (TextView) view.findViewById(R.id.pengulangan);
        imageAktif = (ImageView) view.findViewById(R.id.alarm);

        //ambil data sharedPreferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean animasi = sharedPref.getBoolean("animasi", true);

        //menjalankan efek teks marquee
        if (animasi) {
            tvNama.setSelected(true);
            tvWaktuPengingat.setSelected(true);
            tvPengulangan.setSelected(true);
        } else {
            tvNama.setSelected(false);
            tvWaktuPengingat.setSelected(false);
            tvPengulangan.setSelected(false);
        }

        //ambil data dari database
        String jadwal = cursor.getString(cursor.getColumnIndexOrThrow("nama"));
        int jam = cursor.getInt(cursor.getColumnIndexOrThrow("jam"));
        int menit = cursor.getInt(cursor.getColumnIndexOrThrow("menit"));
        String waktuPengingat = cursor.getString(cursor.getColumnIndexOrThrow("waktu_pengingat"));
        String pengulangan = cursor.getString(cursor.getColumnIndexOrThrow("pengulangan"));
        String aktif = cursor.getString(cursor.getColumnIndexOrThrow("aktif"));

        //set item list
        tvJam.setText(convertWaktu(jam) + ":" + convertWaktu(menit));
        tvNama.setText(jadwal);
        setWaktuPengingat(waktuPengingat);
        setPengulangan(pengulangan);
        setImageAktif(aktif);
    }

    /**
     * ubah format waktu
     */
    private String convertWaktu(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }

    private void setWaktuPengingat(String waktuPengingat) {
        if (waktuPengingat.equals("0")) {
            tvWaktuPengingat.setText(R.string.ingat_sesuai_jadwal);
        } else {
            tvWaktuPengingat.setText("Diingatkan " + waktuPengingat + " menit sebelum jadwal");
        }
    }

    private void setPengulangan(String pengulangan) {
        if (pengulangan.equals("true")) {
            tvPengulangan.setText(R.string.pengulangan_aktif);
        } else {
            tvPengulangan.setText(R.string.pengulangan_mati);
        }
    }

    private void setImageAktif(String aktif) {
        if (aktif.equals("true")) {
            imageAktif.setImageResource(R.drawable.ic_notifications_active_black_24dp);
        } else if (aktif.equals("false")) {
            imageAktif.setImageResource(R.drawable.ic_notifications_off_black_24dp);
        }
    }
}
