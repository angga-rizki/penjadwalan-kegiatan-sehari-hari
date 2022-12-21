package com.puyopuyo.penjadwalankegiatansehari_hari.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.puyopuyo.penjadwalankegiatansehari_hari.data.DatabaseHelper;

import java.util.ArrayList;

public class RebootBroadcastReceiver extends BroadcastReceiver {
    DatabaseHelper db;

    @Override
    public void onReceive(Context context, Intent intent) {
        db = new DatabaseHelper(context);

        //menjalankan code dibawah untuk menjadwalkan ulang alarm setelah perangkat reboot
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            ArrayList<Long> aktifTrue = db.getAktifTrue(); //ambil semua data id dari database

            //menjadwalkan ulang alarm sebanyak id yang disimpan di database
            if (aktifTrue.size() != 0) { //apabila jumlah id tidak 0
                //proses loop untuk menjadwalkan ulang alarm sebanyak id yang disimpan di database
                for (int i = 0; i < aktifTrue.size(); i++) {
                    long id = aktifTrue.get(i);
                    new SetAlarm().buatAlarmUlang(context, id);
                }
            }
        }
    }
}
