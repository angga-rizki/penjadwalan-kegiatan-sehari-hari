package com.puyopuyo.penjadwalankegiatansehari_hari.alarm;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.puyopuyo.penjadwalankegiatansehari_hari.R;
import com.puyopuyo.penjadwalankegiatansehari_hari.data.DatabaseHelper;
import com.puyopuyo.penjadwalankegiatansehari_hari.data.Jadwal;
import com.puyopuyo.penjadwalankegiatansehari_hari.tampilan.UpdateJadwal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    DatabaseHelper db;
    Jadwal jadwal;

    @Override
    public void onReceive(Context context, Intent intent) {
        db = new DatabaseHelper(context);

        //ambil data dari database
        long id = intent.getLongExtra("id", 0);
        jadwal = db.getJadwal(id);
        String hari = jadwal.getHari();
        String nama = jadwal.getNama();
        int jam = jadwal.getJam();
        int menit = jadwal.getMenit();
        String pengulangan = jadwal.getPengulangan();
        String catatan = jadwal.getCatatan();
        long tanggalHistory = jadwal.getTanggal();
        String aktifHistory = jadwal.getAktifHistory();

        //menentukan waktu notifikasi dari database untuk ditampilkan di notifikasi
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek(hari));
        calendar.set(Calendar.HOUR_OF_DAY, jam);
        calendar.set(Calendar.MINUTE, menit);
        calendar.set(Calendar.SECOND, 0);
        long tanggal = calendar.getTimeInMillis();

        //ubah format waktu menjadi "nama hari, hari bulan tahun,  jam:menit
        Date date = new Date(tanggal);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy,  HH:mm", new Locale("id", "ID"));

        try {
            if (!aktifHistory.equals("true")) {
                db.updateJadwalAktifHistory(id);
                db.updateAktifHistory(tanggalHistory);
            }
        } catch (Exception e) {}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (pengulangan.equals("true")) {
                setNotifikasi(context, id, nama, catatan, date, simpleDateFormat);
                new SetAlarm().setAlarmUlang(context, id);
            } else {
                setNotifikasi(context, id, nama, catatan, date, simpleDateFormat);
            }
        } else {
            setNotifikasi(context, id, nama, catatan, date, simpleDateFormat);
        }

        new SetAlarm().checkAlarmAll(context);
    }

    private void setNotifikasi(Context context, long id, String nama, String catatan, Date date,
                               SimpleDateFormat simpleDateFormat) {
        //ambil data sharedPreferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String ringtone = sharedPref.getString("ringtone", "DEFAULT_NOTIFICATION_SOUND");
        String led = sharedPref.getString("led", "#FFFF0000");
        String getar = sharedPref.getString("getar", "1");

        //set notifikasi
        //intent untuk menuju halaman edit jika notifikasi ditekan dan mengirim nilai "id"
        Intent intent = new Intent(context, UpdateJadwal.class);
        intent.putExtra("_id", id);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent); //agar kembali ke halaman utama saat melakukan back
        PendingIntent pendingIntent = stackBuilder.getPendingIntent((int) id, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(nama + " : " + simpleDateFormat.format(date))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true);

        if (!led.equals("0")) {
            notification.setLights(Color.parseColor(led), 2000, 2000);
        }
        if (ringtone.length() != 0) {
            notification.setSound(Uri.parse(ringtone));
        }

        switch (getar) {
            case "1":
                notification.setVibrate(new long[]{500, 500, 400, 500, 400, 500, 400, 500});
                break;
            case "2":
                notification.setVibrate(new long[]{500, 300, 300, 300, 300, 300, 300, 300});
                break;
            case "3":
                notification.setVibrate(new long[]{500, 800, 400, 800, 400, 800, 400, 800});
                break;
        }

        if (catatan.length() != 0) {
            notification.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(nama + " : " + simpleDateFormat.format(date) +
                            "\n\nCatatan :\n" + catatan));
        } else {
            notification.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(nama + " : " + simpleDateFormat.format(date)));
        }

        //android lolipop dan keatas hanya membolehkan icon putih untuk icon notifikasi
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setSmallIcon(R.drawable.ic_notifications_active_white_24dp);
        } else {
            notification.setSmallIcon(R.mipmap.ic_launcher);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) id, notification.build());
    }

    public void cancelNotifikasi(Context context, long id) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel((int) id);
    }

    /**
     * menentukan nilai Calendar.DAY_OF_WEEK dari nama hari
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
