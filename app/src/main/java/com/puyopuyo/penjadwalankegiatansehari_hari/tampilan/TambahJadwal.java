package com.puyopuyo.penjadwalankegiatansehari_hari.tampilan;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.puyopuyo.penjadwalankegiatansehari_hari.R;
import com.puyopuyo.penjadwalankegiatansehari_hari.alarm.SetAlarm;
import com.puyopuyo.penjadwalankegiatansehari_hari.data.DatabaseHelper;

import java.util.Calendar;

public class TambahJadwal extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    DatabaseHelper db;

    private FloatingActionButton alarmAktif, alarmOff;
    private EditText namaJadwal, catatanEditText;
    private TextView hariText, waktuText, ulangText, waktuPengingatText;

    private String nama, aktif, hari, waktu, pengulangan, catatan;
    private int jam, menit, waktuPengingat;
    private long tanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tambah_jadwal);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        db = new DatabaseHelper(getApplicationContext());

        //deklarasi objek
        alarmAktif = (FloatingActionButton) findViewById(R.id.alarm_aktif);
        alarmOff = (FloatingActionButton) findViewById(R.id.alarm_off);
        namaJadwal = (EditText) findViewById(R.id.nama_jadwal);
        hariText = (TextView) findViewById(R.id.set_hari);
        waktuText = (TextView) findViewById(R.id.set_waktu);
        ulangText = (TextView) findViewById(R.id.set_ulang);
        waktuPengingatText = (TextView) findViewById(R.id.set_waktupengingat);
        catatanEditText = (EditText) findViewById(R.id.edit_text_catatan);

        //nilai variabel default
        Calendar calendar = Calendar.getInstance();
        jam = calendar.get(Calendar.HOUR_OF_DAY);
        menit = calendar.get(Calendar.MINUTE);

        aktif = "true";
        hari = getIntent().getStringExtra("hari");
        pengulangan = "true";
        waktuPengingat = 10;
        waktu = convertWaktu(jam) + ":" + convertWaktu(menit);

        //set text default dari variable default
        hariText.setText(hari);
        waktuText.setText(waktu);
        waktuPengingatText.setText(String.valueOf(waktuPengingat) + " menit sebelum jadwal");
        if (pengulangan.equals("true")) {
            ulangText.setText(R.string.ya);
        } else {
            ulangText.setText(R.string.tidak);
        }

        //setting tombol aktif
        if (aktif.equals("true")) {
            alarmAktif.setVisibility(View.VISIBLE);
            alarmOff.setVisibility(View.GONE);
        } else if (aktif.equals("false")) {
            alarmAktif.setVisibility(View.GONE);
            alarmOff.setVisibility(View.VISIBLE);
        }
    }

    public void setAlarmAktif(View v) {
        alarmAktif.setVisibility(View.VISIBLE);
        alarmOff.setVisibility(View.GONE);
        aktif = "true";
    }

    public void setAlarmOff(View v) {
        alarmAktif.setVisibility(View.GONE);
        alarmOff.setVisibility(View.VISIBLE);
        aktif = "false";
    }

    public void setHari(View v) {
        final String[] hariItem = new String[7];

        hariItem[0] = "Senin";
        hariItem[1] = "Selasa";
        hariItem[2] = "Rabu";
        hariItem[3] = "Kamis";
        hariItem[4] = "Jumat";
        hariItem[5] = "Sabtu";
        hariItem[6] = "Minggu";

        //dialog daftar hari
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Hari");
        builder.setItems(hariItem, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                hari = hariItem[item];
                hariText.setText(hari);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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

    public void setWaktu(View v) {
        DialogFragment timePicker = new TimePickerDialogFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        jam = hourOfDay;
        menit = minute;
        waktu = convertWaktu(hourOfDay) + ":" + convertWaktu(minute);
        waktuText.setText(waktu);
    }

    public void setUlangSwitch(View v) {
        boolean on = ((Switch) v).isChecked();
        if (on) {
            pengulangan = "true";
            ulangText.setText(R.string.ulang_on);
        } else {
            pengulangan = "false";
            ulangText.setText(R.string.ulang_off);
        }
    }

    public void setWaktuPengingat(View v) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Masukkan Angka (Menit)");

        //buat editText
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialog.setView(input);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (input.getText().toString().length() == 0 || Integer.parseInt(input.getText().toString()) <= 360) {
                    //apabila nilai waktu pengingat 0
                    if (input.getText().toString().length() == 0 || Integer.parseInt(input.getText().toString()) == 0) {
                        waktuPengingat = 0;
                        waktuPengingatText.setText(R.string.sesuai_jadwal);
                    } else {
                        waktuPengingat = Integer.parseInt(input.getText().toString().trim());
                        waktuPengingatText.setText(String.valueOf(waktuPengingat) + " menit sebelum jadwal");
                    }
                } else {
                    Toast.makeText(TambahJadwal.this, "Waktu maksimal 360 menit!", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });
        dialog.show();
    }

    /**
     * simpan database
     */
    private void simpanJadwal() {
        Calendar calendar = Calendar.getInstance();
        tanggal = calendar.getTimeInMillis();
        long id = db.insertJadwal(hari, nama, jam, menit, pengulangan, waktuPengingat, aktif, catatan, tanggal, "false"); //ambil nilai id dari data baru
        if (id != -1) { //cek id dari data baru berhasil dibuat atau tidak
            Toast.makeText(this, getString(R.string.berhasil_simpan), Toast.LENGTH_SHORT).show();
            new SetAlarm().buatAlarm(getApplicationContext(), id);
            db.insertHistory(nama, hari, db.getJadwal(id).getTanggal(), "tambah", "false");
        } else {
            Toast.makeText(this, getString(R.string.gagal_simpan), Toast.LENGTH_SHORT).show();
        }
    }

    public void simpan(View v) {
        //fungsi trim() untuk menghilangan whitespace pada awal dan akhir kalimat
        nama = namaJadwal.getText().toString().trim();
        catatan = catatanEditText.getText().toString().trim();

        if (nama.length() == 0) {
            namaJadwal.setError("Nama Jadwal tidak boleh kosong!");
        } else {
            simpanJadwal();
            finish();
        }
    }

    /**
     * simpan data agar data yang sudah diset tidak hilang saat rotasi
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("Aktif", aktif);
        savedInstanceState.putString("Hari", hari);
        savedInstanceState.putInt("Jam", jam);
        savedInstanceState.putInt("Menit", menit);
        savedInstanceState.putString("Pengulangan", pengulangan);
        savedInstanceState.putInt("WaktuPengingat", waktuPengingat);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        aktif = savedInstanceState.getString("Aktif");
        hari = savedInstanceState.getString("Hari");
        jam = savedInstanceState.getInt("Jam");
        menit = savedInstanceState.getInt("Menit");
        pengulangan = savedInstanceState.getString("Pengulangan");
        waktuPengingat = savedInstanceState.getInt("WaktuPengingat");

        hariText.setText(hari);
        waktuText.setText(convertWaktu(jam) + ":" + convertWaktu(menit));
        if (aktif.equals("true")) {
            alarmAktif.setVisibility(View.VISIBLE);
            alarmOff.setVisibility(View.GONE);
        } else {
            alarmAktif.setVisibility(View.GONE);
            alarmOff.setVisibility(View.VISIBLE);
        }
        if (pengulangan.equals("true")) {
            ulangText.setText(R.string.ya);
        } else {
            ulangText.setText(R.string.tidak);
        }
        if (waktuPengingat == 0) {
            waktuPengingatText.setText(R.string.sesuai_jadwal);
        } else {
            waktuPengingatText.setText(String.valueOf(waktuPengingat) + " menit sebelum jadwal");
        }
    }
}

