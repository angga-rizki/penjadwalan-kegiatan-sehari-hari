package com.puyopuyo.penjadwalankegiatansehari_hari.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.puyopuyo.penjadwalankegiatansehari_hari.data.DatabaseHelper;
import com.puyopuyo.penjadwalankegiatansehari_hari.data.Jadwal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class SetAlarm {

    private AlarmManager alarmManager;
    private Date date;
    private SimpleDateFormat simpleDateFormat;

    private String aktif, pengulangan;
    private long waktuAlarm;
    private long msMinggu = 604800000;

    public void buatAlarm(Context context, long id) {
        setWaktuAlarm(context, id); //menjalankan method setWaktuAlarm

        //cek pilihan alarm aktif dan pilihan pengulangan dari jadwal yang dibuat
        if (aktif.equals("true")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setAlarm(context, id);
                Toast.makeText(context, "Diingatkan : " + simpleDateFormat.format(date),
                        Toast.LENGTH_LONG).show();
            } else {
                if (pengulangan.equals("true")) {
                    setAlarmUlang(context, id);
                    Toast.makeText(context, "Diingatkan : " + simpleDateFormat.format(date),
                            Toast.LENGTH_LONG).show();
                } else {
                    setAlarm(context, id);
                    Toast.makeText(context, "Diingatkan : " + simpleDateFormat.format(date),
                            Toast.LENGTH_LONG).show();
                }
            }
        } else {
            if (checkAlaram(context, id)) { //hasil nilai checkAlarm = null
                Toast.makeText(context, "Notifikasi tidak diaktifkan", Toast.LENGTH_LONG).show();
            } else {
                hapusAlarm(context, id);
                Toast.makeText(context, "Notifikasi dimatikan", Toast.LENGTH_LONG).show();
            }
        }
    }

    void buatAlarmUlang(Context context, long id) {
        setWaktuAlarm(context, id); //menjalankan method setWaktuAlarm

        //cek pilihan alarm aktif dan pilihan pengulangan dari jadwal yang dibuat
        if (aktif.equals("true")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setAlarm(context, id);
            } else {
                if (pengulangan.equals("true")) {
                    setAlarmUlang(context, id);
                } else {
                    setAlarm(context, id);
                }
            }
        }
    }

    /** mengatur waktu alarm dan membaca data jadwal */
    private void setWaktuAlarm(Context context, long id) {
        DatabaseHelper db;
        Jadwal jadwal;

        String hari;
        int jam, menit, waktuPengingat;
        long msMenit = 60000;

        db = new DatabaseHelper(context);

        //ambil data dari database
        jadwal = db.getJadwal(id);
        hari = jadwal.getHari();
        jam = jadwal.getJam();
        menit = jadwal.getMenit();
        pengulangan = jadwal.getPengulangan();
        waktuPengingat = jadwal.getWaktuPengingat();
        aktif = jadwal.getAktif();

        //set waktu notifikasi
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek(hari));
        calendar.set(Calendar.HOUR_OF_DAY, jam);
        calendar.set(Calendar.MINUTE, menit);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long waktuPengingatAlarm = waktuPengingat * msMenit; //waktu pengingat dikali milisekon menit
        waktuAlarm = calendar.getTimeInMillis() - waktuPengingatAlarm; //waktu notifikasi
    }

    private void setAlarm(Context context, long id) {
        //membuat intent untuk mengirim data "id" ke AlarmBroadcastReceiver
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra("id", id);

        //pendingIntent membungkus intent yang telah dibuat untuk dikirim nanti
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //set notifikasi
        if (waktuAlarm < System.currentTimeMillis()) {
            alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(waktuAlarm + msMinggu, pendingIntent), pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, waktuAlarm + msMinggu, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, waktuAlarm + msMinggu, pendingIntent);
            }
        } else {
            alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(waktuAlarm, pendingIntent), pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, waktuAlarm, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, waktuAlarm, pendingIntent);
            }
        }

        //ubah format waktu dari milisekon ke jam:menit
        date = new Date(waktuAlarm);
        simpleDateFormat = new SimpleDateFormat("EEEE, HH:mm", new Locale("id", "ID"));
    }

    void setAlarmUlang(Context context, long id) {
        //mengirim data dan perintah ke AlarmBroadcastReceiver dengan pending intent
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        //set notifikasi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setWaktuAlarm(context, id);
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(waktuAlarm + msMinggu, pendingIntent), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWaktuAlarm(context, id);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, waktuAlarm + msMinggu, pendingIntent);
        } else {
            if (waktuAlarm < System.currentTimeMillis()) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, waktuAlarm + msMinggu, msMinggu, pendingIntent);
            } else {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, waktuAlarm, msMinggu, pendingIntent);
            }
        }

        //ubah format waktu dari milisekon ke jam:menit
        date = new Date(waktuAlarm);
        simpleDateFormat = new SimpleDateFormat("EEEE, HH:mm", new Locale("id", "ID"));
    }

    /**
     * hapus jadwal alarm
     */
    public void hapusAlarm(Context context, long id) {
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    /**
     * check jadwal alarm, apabila alarm tidak ada maka akan mengembalikan nilai "true" untuk hasil "null" untuk dicek
     */
    private boolean checkAlaram(Context context, long id) {
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);

        return (PendingIntent.getBroadcast(context, (int) id, intent,
                PendingIntent.FLAG_NO_CREATE) == null); //hasil null apabila alarm tidak ada
    }

    /**
     * check alarm notifikasi aktif terjadwal atau tidak
     */
    public void checkAlarmAll(Context context) {
        DatabaseHelper db;
        db = new DatabaseHelper(context);

        ArrayList<Long> aktifTrue = db.getAktifTrue(); //ambil semua data id dimana aktif  = "true"

        if (aktifTrue.size() != 0) {
            for (int i = 0; i < aktifTrue.size(); i++) {
                long id = aktifTrue.get(i);

                boolean checkAlarm = checkAlaram(context, id);
                if (checkAlarm) { //alarm notifikasi tidak ada = "true"
                    buatAlarmUlang(context, id); //buat ulang alarm notifikasi
                }
            }
        }
    }

    /**
     * menentukan nilai Calendar.DAY_OF_WEEK berdasarkan nama hari
     */
    private int dayOfWeek(String hari) {
        switch (hari) {
            case "Senin":
                return 2;
            case "Selasa":
                return 3;
            case "Rabu":
                return 4;
            case "Kamis":
                return 5;
            case "Jumat":
                return 6;
            case "Sabtu":
                return 7;
            case "Minggu":
                return 1;
        }
        return 0;
    }
}
