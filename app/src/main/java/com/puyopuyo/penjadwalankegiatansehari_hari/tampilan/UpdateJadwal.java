package com.puyopuyo.penjadwalankegiatansehari_hari.tampilan;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.puyopuyo.penjadwalankegiatansehari_hari.R;
import com.puyopuyo.penjadwalankegiatansehari_hari.alarm.AlarmBroadcastReceiver;
import com.puyopuyo.penjadwalankegiatansehari_hari.alarm.SetAlarm;
import com.puyopuyo.penjadwalankegiatansehari_hari.data.DatabaseHelper;
import com.puyopuyo.penjadwalankegiatansehari_hari.data.Jadwal;

import java.util.Calendar;

public class UpdateJadwal extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    DatabaseHelper db;
    Jadwal jadwal;

    private FloatingActionButton alarmAktif, alarmOff;
    private EditText namaJadwal, catatanEditText;
    private TextView hariText, waktuText, ulangText, waktuPengingatText;
    private Switch switchPengulangan;

    private String nama, aktif, hari, waktu, pengulangan, catatan, aktifHistory;
    private int jam, menit, waktuPengingat;
    private long id, tanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_jadwal);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        db = new DatabaseHelper(getApplicationContext());

        alarmAktif = (FloatingActionButton) findViewById(R.id.alarm_aktif);
        alarmOff = (FloatingActionButton) findViewById(R.id.alarm_off);
        namaJadwal = (EditText) findViewById(R.id.nama_jadwal);
        hariText = (TextView) findViewById(R.id.set_hari);
        waktuText = (TextView) findViewById(R.id.set_waktu);
        ulangText = (TextView) findViewById(R.id.set_ulang);
        switchPengulangan = (Switch) findViewById(R.id.switch_ulang);
        waktuPengingatText = (TextView) findViewById(R.id.set_waktupengingat);
        catatanEditText = (EditText) findViewById(R.id.edit_text_catatan);

        //ambil informasi dari item click
        Intent intent = getIntent();
        id = intent.getLongExtra("_id", 0); //id database

        //set nilai item
        db.updateJadwalAktifHistory24();
        ambilData(); //ambil data dari database
        waktu = convertWaktu(jam) + ":" + convertWaktu(menit);

        //set text dari item
        namaJadwal.setText(nama);
        hariText.setText(hari);
        waktuText.setText(waktu);
        catatanEditText.setText(catatan);
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
        if (pengulangan.equals("true")) {
            switchPengulangan.setChecked(true);
        } else {
            switchPengulangan.setChecked(false);
        }

        //setting tampilan tombol aktif
        if (aktif.equals("true")) {
            alarmAktif.setVisibility(View.VISIBLE);
            alarmOff.setVisibility(View.GONE);
        } else if (aktif.equals("false")) {
            alarmAktif.setVisibility(View.GONE);
            alarmOff.setVisibility(View.VISIBLE);
        }
    }

    /**
     * tombol hapus jadwal
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update_jadwal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.hapus_jadwal) {
            konfirmasiHapus(); //konfirmasi hapus apabila di klik
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ambil data dari database
     */
    private void ambilData() {
        //tabel jadwal
        jadwal = db.getJadwal(id);
        hari = jadwal.getHari();
        nama = jadwal.getNama();
        aktif = jadwal.getAktif();
        pengulangan = jadwal.getPengulangan();
        waktuPengingat = jadwal.getWaktuPengingat();
        jam = jadwal.getJam();
        menit = jadwal.getMenit();
        catatan = jadwal.getCatatan();
        tanggal = jadwal.getTanggal();
        aktifHistory = jadwal.getAktifHistory();
    }

    public void setAlarmAktif(View v) {
        alarmAktif = (FloatingActionButton) findViewById(R.id.alarm_aktif);
        alarmOff = (FloatingActionButton) findViewById(R.id.alarm_off);
        alarmAktif.setVisibility(View.VISIBLE);
        alarmOff.setVisibility(View.GONE);
        aktif = "true";
    }

    public void setAlarmOff(View v) {
        alarmAktif = (FloatingActionButton) findViewById(R.id.alarm_aktif);
        alarmOff = (FloatingActionButton) findViewById(R.id.alarm_off);
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

    /**
     * input waktu
     */
    public void setWaktu(View v) {
        DialogFragment timePicker = new TimePickerDialogFragment(); //membuat dialog time picker
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    /**
     * ambil data input waktu
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        jam = hourOfDay;
        menit = minute;
        waktu = convertWaktu(hourOfDay) + ":" + convertWaktu(minute);
        waktuText.setText(waktu);
    }

    public void setUlangSwitch(View v) {
        boolean on = switchPengulangan.isChecked();
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
                    Toast.makeText(UpdateJadwal.this, "Waktu maksimal 360 menit!", Toast.LENGTH_LONG).show();
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
     * update database
     */
    private void updateJadwal() {
        nama = namaJadwal.getText().toString().trim();

        Calendar calendar = Calendar.getInstance();
        long tanggalHistory = calendar.getTimeInMillis();

        int diupdate = db.updateJadwal(id, hari, nama, jam, menit, pengulangan, waktuPengingat, aktif, catatan);
        if (diupdate == 0) {
            Toast.makeText(this, getString(R.string.gagal_update), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.berhasil_update), Toast.LENGTH_SHORT).show();
            new SetAlarm().buatAlarm(getApplicationContext(), id);

            try {
                if (aktifHistory.equals("true")) {
                    db.insertHistory(nama, hari, tanggalHistory, "edit", "true");
                } else {
                    db.updateHistory(tanggal, nama, hari);
                }
            } catch (Exception e) {}
        }
    }

    private void konfirmasiHapus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.konfirmasi_hapus);
        builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                hapusJadwal();
                finish();
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void hapusJadwal() {
        nama = namaJadwal.getText().toString().trim();

        Calendar calendar = Calendar.getInstance();
        long tanggalHistory = calendar.getTimeInMillis();

        int dihapus = db.deleteJadwal(id);
        new AlarmBroadcastReceiver().cancelNotifikasi(this, id); //hapus notifikasi yang tampil di status bar

        if (dihapus == 0) {
            Toast.makeText(this, getString(R.string.gagal_hapus), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.berhasil_hapus), Toast.LENGTH_SHORT).show();
            new SetAlarm().hapusAlarm(this, id);

            try {
                if (aktifHistory.equals("true")) {
                    db.insertHistory(nama, hari, tanggalHistory, "hapus", "true");
                } else {
                    db.deleteHistory(tanggal);
                }
            } catch (Exception e) {}
        }
    }

    public void update(View v) {
        nama = namaJadwal.getText().toString().trim();
        catatan = catatanEditText.getText().toString().trim();

        if (nama.length() == 0) {
            namaJadwal.setError("Nama Jadwal tidak boleh kosong!");
        } else {
            updateJadwal();
            finish();
        }
    }

    private String dayOfWeek(int hari) {
        switch (hari) {
            case 2:
                return "Senin";
            case 3:
                return "Selasa";
            case 4:
                return "Rabu";
            case 5:
                return "Kamis";
            case 6:
                return "Jumat";
            case 7:
                return "Sabtu";
            case 1:
                return "Minggu";
        }
        return null;
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
