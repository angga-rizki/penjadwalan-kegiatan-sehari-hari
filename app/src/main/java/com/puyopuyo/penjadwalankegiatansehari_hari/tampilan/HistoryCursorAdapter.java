package com.puyopuyo.penjadwalankegiatansehari_hari.tampilan;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.puyopuyo.penjadwalankegiatansehari_hari.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class HistoryCursorAdapter extends CursorAdapter {

    HistoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvTanggal = (TextView) view.findViewById(R.id.tanggal);
        TextView tvAksi = (TextView) view.findViewById(R.id.aksi);

        String nama = cursor.getString(cursor.getColumnIndexOrThrow("nama"));
        String hari = cursor.getString(cursor.getColumnIndexOrThrow("hari"));
        long tanggal = cursor.getLong(cursor.getColumnIndexOrThrow("tanggal"));
        String aksi = cursor.getString(cursor.getColumnIndexOrThrow("aksi"));

        Date date = new Date(tanggal);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm", new Locale("id", "ID"));

        tvTanggal.setText(simpleDateFormat.format(date));

        String membuat = context.getResources().getString(R.string.buat);
        String menghapus = context.getResources().getString(R.string.hapus);
        String mengedit = context.getResources().getString(R.string.edit);
        String tambah = membuat + nama + " (" + hari + ")";
        String hapus = menghapus + nama + " (" + hari + ")";
        String edit = mengedit + nama + " (" + hari + ")";

        SpannableStringBuilder strTambah = new SpannableStringBuilder(tambah);
        SpannableStringBuilder strHapus = new SpannableStringBuilder(hapus);
        SpannableStringBuilder strEdit = new SpannableStringBuilder(edit);
        strTambah.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), membuat.length(), membuat.length() + nama.length(), 0);
        strHapus.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), menghapus.length(), menghapus.length() + nama.length(), 0);
        strEdit.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), mengedit.length(), mengedit.length() + nama.length(), 0);

        switch (aksi) {
            case "tambah":
                tvAksi.setText(strTambah);
                break;
            case "hapus":
                tvAksi.setText(strHapus);
                break;
            case "edit":
                tvAksi.setText(strEdit);
                break;
        }
    }
}
