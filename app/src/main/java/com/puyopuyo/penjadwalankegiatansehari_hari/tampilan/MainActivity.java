package com.puyopuyo.penjadwalankegiatansehari_hari.tampilan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.puyopuyo.penjadwalankegiatansehari_hari.BuildConfig;
import com.puyopuyo.penjadwalankegiatansehari_hari.R;
import com.puyopuyo.penjadwalankegiatansehari_hari.alarm.AlarmBroadcastReceiver;
import com.puyopuyo.penjadwalankegiatansehari_hari.alarm.SetAlarm;
import com.puyopuyo.penjadwalankegiatansehari_hari.data.DatabaseHelper;
import com.puyopuyo.penjadwalankegiatansehari_hari.data.Jadwal;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;
    private int posisi;
    private long tanggal, tanggalHistory;
    private String nama, hari, posisiHari, namaHari, aktifHistory;
    DatabaseHelper db;
    Jadwal jadwal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new DatabaseHelper(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the seven
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setPosisi(); //ambil nilai posisi
        mViewPager.setCurrentItem(posisi); //setting posisi
        setTema();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setHari(); //ambil nilai hari

                Intent intent = new Intent(MainActivity.this, TambahJadwal.class);
                intent.putExtra("hari", posisiHari); //kirim nilai hari berdasarkan tab yang dipilih
                startActivity(intent);
            }
        });

        //check alarm notifikasi aktif terjadwal atau tidak, jika tidak akan dijadwalkan ulang
        new SetAlarm().checkAlarmAll(getApplicationContext());
    }

    private void setTema() {
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: //tab senin
                        setWarna("8d6e63", "5f4339");
                        break;
                    case 1: //tab selasa
                        setWarna("d84315", "9f0000");
                        break;
                    case 2: //tab rabu
                        setWarna("827717", "524c00");
                        break;
                    case 3: //tab kamis
                        setWarna("8e24aa", "5c007a");
                        break;
                    case 4: //tab jumat
                        setWarna("558b2f", "255d00");
                        break;
                    case 5: //tab sabtu
                        setWarna("1976d2", "004ba0");
                        break;
                    case 6: //tab minggu
                        setWarna("d81b60", "a00037");
                        break;
                }
            }
        });
    }

    /** setting posisi default tab berdasarkan hari saat ini */
    private void setPosisi(){
        int hari = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (hari == Calendar.MONDAY) {
            setWarna("8d6e63", "5f4339");
            posisi = 0;
        } else if (hari == Calendar.TUESDAY) {
            setWarna("d84315", "9f0000");
            posisi = 1;
        } else if (hari == Calendar.WEDNESDAY) {
            setWarna("827717", "524c00");
            posisi = 2;
        } else if (hari == Calendar.THURSDAY) {
            setWarna("8e24aa", "5c007a");
            posisi = 3;
        } else if (hari == Calendar.FRIDAY) {
            setWarna("558b2f", "255d00");
            posisi = 4;
        } else if (hari == Calendar.SATURDAY) {
            setWarna("1976d2", "004ba0");
            posisi = 5;
        } else if (hari == Calendar.SUNDAY) {
            setWarna("d81b60", "a00037");
            posisi = 6;
        }
    }

    private void setWarna(String warna, String warnaStatusBar) {
        coordinatorLayout.setBackgroundColor(Color.parseColor("#14" + warna));
        appBarLayout.setBackgroundColor(Color.parseColor("#" + warna));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#" + warnaStatusBar));
        }
    }

    /** set nilai hari berdasarkan tab yang dipilih */
    private void setHari(){
        posisi = mViewPager.getCurrentItem();
        switch (posisi){
            case 0: //tab senin
                posisiHari = "Senin";
                break;
            case 1: //tab selasa
                posisiHari = "Selasa";
                break;
            case 2: //tab rabu
                posisiHari = "Rabu";
                break;
            case 3: //tab kamis
                posisiHari = "Kamis";
                break;
            case 4: //tab jumat
                posisiHari = "Jumat";
                break;
            case 5: //tab sabtu
                posisiHari = "Sabtu";
                break;
            case 6: //tab minggu
                posisiHari = "Minggu";
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent;

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_hapus_hari:
                pilihHari();
                return true;
            case R.id.action_hapus_semua:
                konfirmasiHapusSemua();
                return true;
            case R.id.action_history:
                intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_setting:
                intent = new Intent(MainActivity.this, Setting.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                about();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** dialog pilih hari */
    private void pilihHari(){
        //array daftar hari untuk dipilih
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

                namaHari = hariItem[item]; //namaHari = hari yang dipilih dari array
                konfirmasiHapus();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void konfirmasiHapus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Hapus jadwal hari " + namaHari + "?");
        builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                hapusJadwal();
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

    private void konfirmasiHapusSemua() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.konfirmasi_hapus_semua);
        builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                hapusSemua();
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

    private void about() {
        //buat dialog yang berisi textview
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView textView = new TextView(this);
        textView.setPadding(0, 60, 0, 60);
        textView.setText(getText(R.string.about1) + " " + BuildConfig.VERSION_NAME + getText(R.string.about2));
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(textView);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void hapusJadwal() {
        db.updateJadwalAktifHistory24();

        ArrayList<Long> idHari = db.getAllIdHari(namaHari); //ambil data semua id berdasarkan hari yang dipilih

        if (idHari.size() != 0){ //apabila jumlah idHari tidak 0
            for (int i = 0; i < idHari.size(); i++){
                long id = idHari.get(i);
                new AlarmBroadcastReceiver().cancelNotifikasi(this, id); //hapus notifikasi yang tampil pada status bar
                new SetAlarm().hapusAlarm(this, id);

                jadwal = db.getJadwal(id);
                nama = jadwal.getNama();
                hari = jadwal.getHari();
                tanggal = jadwal.getTanggal();
                aktifHistory = jadwal.getAktifHistory();

                Calendar calendar = Calendar.getInstance();
                tanggalHistory = calendar.getTimeInMillis();

                try {
                    if (aktifHistory.equals("true")) {
                        db.insertHistory(nama, hari, tanggalHistory, "hapus", "true");
                    } else {
                        db.deleteHistory(tanggal);
                    }
                } catch (Exception e) {}
            }

            db.deleteHari(namaHari); //hapus semua data di database sesuai dengan hari yang dipilih
            refresh(); //refresh item pada listview
            Toast.makeText(this, getString(R.string.hapus_jadwal) + " " + namaHari + " dihapus", Toast.LENGTH_SHORT).show();
        }
        else { //apabila id yang dipilih kosong
            Toast.makeText(this, getString(R.string.hapus_kosong), Toast.LENGTH_SHORT).show();
        }
    }

    private void hapusSemua() {
        db.updateJadwalAktifHistory24();

        ArrayList<Long> idAll = db.getAllId();

        if (idAll.size() != 0) {
            for (int i = 0; i < idAll.size(); i++){
                long id = idAll.get(i);
                new AlarmBroadcastReceiver().cancelNotifikasi(this, id); //hapus notifikasi yang tampil pada status bar
                new SetAlarm().hapusAlarm(this, id);

                jadwal = db.getJadwal(id);
                nama = jadwal.getNama();
                hari = jadwal.getHari();
                tanggal = jadwal.getTanggal();
                aktifHistory = jadwal.getAktifHistory();

                Calendar calendar = Calendar.getInstance();
                tanggalHistory = calendar.getTimeInMillis();

                try {
                    if (aktifHistory.equals("true")) {
                        db.insertHistory(nama, hari, tanggalHistory, "hapus", "true");
                    } else {
                        db.deleteHistory(tanggal);
                    }
                } catch (Exception e) {}
            }

            db.deleteAll(); //hapus semua data database
            refresh(); //refresh item pada listview
            Toast.makeText(this, getString(R.string.hapus_semua), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, getString(R.string.hapus_kosong), Toast.LENGTH_SHORT).show();
        }
    }

    /** refresh item pada listview */
    private void refresh() {
        Senin senin; Selasa selasa; Rabu rabu; Kamis kamis; Jumat jumat; Sabtu sabtu; Minggu minggu;
        posisi = mViewPager.getCurrentItem();
        switch (posisi){
            case 0: //tab senin
                senin = (Senin) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 0);
                selasa = (Selasa) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 1);

                senin.refresh();
                selasa.refresh();
                break;
            case 1: //tab selasa
                senin = (Senin) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 0);
                selasa = (Selasa) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 1);
                rabu = (Rabu) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 2);

                senin.refresh();
                selasa.refresh();
                rabu.refresh();
                break;
            case 2: //tab rabu
                selasa = (Selasa) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 1);
                rabu = (Rabu) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 2);
                kamis = (Kamis) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 3);

                selasa.refresh();
                rabu.refresh();
                kamis.refresh();
                break;
            case 3: //tab kamis
                rabu = (Rabu) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 2);
                kamis = (Kamis) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 3);
                jumat = (Jumat) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 4);

                rabu.refresh();
                kamis.refresh();
                jumat.refresh();
                break;
            case 4: //tab jumat
                kamis = (Kamis) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 3);
                jumat = (Jumat) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 4);
                sabtu = (Sabtu) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 5);

                kamis.refresh();
                jumat.refresh();
                sabtu.refresh();
                break;
            case 5: //tab sabtu
                jumat = (Jumat) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 4);
                sabtu = (Sabtu) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 5);
                minggu = (Minggu) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 6);

                jumat.refresh();
                sabtu.refresh();
                minggu.refresh();
                break;
            case 6: //tab minggu
                sabtu = (Sabtu) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 5);
                minggu = (Minggu) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.container + ":" + 6);

                sabtu.refresh();
                minggu.refresh();
                break;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new Senin();
                case 1: return new Selasa();
                case 2: return new Rabu();
                case 3: return new Kamis();
                case 4: return new Jumat();
                case 5: return new Sabtu();
                case 6: return new Minggu();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "SENIN";
                case 1: return "SELASA";
                case 2: return "RABU";
                case 3: return "KAMIS";
                case 4: return "JUMAT";
                case 5: return "SABTU";
                case 6: return "MINGGU";
            }
            return null;
        }
    }
}
