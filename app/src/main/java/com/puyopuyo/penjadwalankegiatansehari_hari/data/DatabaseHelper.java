package com.puyopuyo.penjadwalankegiatansehari_hari.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String NAMA_DATABASE = "penjadwalan.db";

    private static final String TABEL_JADWAL = "jadwal"; //nama tabel
    private static final String TABEL_HISTORY = "history";

    //key tabel jadwal
    private static final String KEY_ID = "_id";
    private static final String KEY_HARI = "hari";
    private static final String KEY_NAMA = "nama";
    private static final String KEY_JAM = "jam";
    private static final String KEY_MENIT = "menit";
    private static final String KEY_PENGULANGAN = "pengulangan";
    private static final String KEY_WAKTUPENGINGAT = "waktu_pengingat";
    private static final String KEY_AKTIF = "aktif";
    private static final String KEY_CATATAN = "catatan";
    private static final String KEY_TANGGAL = "tanggal";
    private static final String KEY_AKTIF_HISTORY = "aktif_history";

    //key tabel history
    private static final String ID = "_id";
    private static final String NAMA = "nama";
    private static final String HARI = "hari";
    private static final String TANGGAL = "tanggal";
    private static final String AKSI = "aksi";
    private static final String AKTIF = "aktif";

    //statement create tabel
    private static final String CREATE_TABEL_JADWAL = "CREATE TABLE " + TABEL_JADWAL + "("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_HARI + " TEXT NOT NULL, "
            + KEY_NAMA + " TEXT NOT NULL, "
            + KEY_JAM + " INTEGER NOT NULL, "
            + KEY_MENIT + " INTEGER NOT NULL, "
            + KEY_PENGULANGAN + " TEXT NOT NULL, "
            + KEY_WAKTUPENGINGAT + " INTEGER NOT NULL, "
            + KEY_AKTIF + " TEXT NOT NULL, "
            + KEY_CATATAN + " TEXT, "
            + KEY_TANGGAL + " INTEGER NOT NULL, "
            + KEY_AKTIF_HISTORY + " TEXT NOT NULL" + ")";
    private static final String CREATE_TABEL_HISTORY = "CREATE TABLE " + TABEL_HISTORY + "("
            + ID + " INTEGER PRIMARY KEY, "
            + NAMA + " TEXT NOT NULL, "
            + HARI + " TEXT NOT NULL, "
            + TANGGAL + " INTEGER NOT NULL, "
            + AKSI + " TEXT NOT NULL, "
            + AKTIF + " TEXT NOT NULL" + ")";

    public DatabaseHelper(Context context) {
        super(context, NAMA_DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABEL_JADWAL); //buat tabel database
        db.execSQL(CREATE_TABEL_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * insert data
     */
    public long insertJadwal(String hari, String nama, int jam, int menit, String pengulangan,
                             int waktuPengingat, String aktif, String catatan, long tanggal, String aktifHistory) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_HARI, hari);
        values.put(KEY_NAMA, nama);
        values.put(KEY_JAM, jam);
        values.put(KEY_MENIT, menit);
        values.put(KEY_PENGULANGAN, pengulangan);
        values.put(KEY_WAKTUPENGINGAT, waktuPengingat);
        values.put(KEY_AKTIF, aktif);
        values.put(KEY_CATATAN, catatan);
        values.put(KEY_TANGGAL, tanggal);
        values.put(KEY_AKTIF_HISTORY, aktifHistory);

        long resultJadwal = db.insert(TABEL_JADWAL, null, values);

        closeDB();
        return resultJadwal;
    }

    public long insertHistory(String nama, String hari, long tanggal,
                             String aksi, String aktif) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAMA, nama);
        values.put(HARI, hari);
        values.put(TANGGAL, tanggal);
        values.put(AKSI, aksi);
        values.put(AKTIF, aktif);

        long resultHistory = db.insert(TABEL_HISTORY, null, values);

        closeDB();
        return resultHistory;
    }

    /**
     * baca data
     */
    public Jadwal getJadwal(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABEL_JADWAL,
                new String[]{KEY_ID, KEY_HARI, KEY_NAMA, KEY_JAM, KEY_MENIT, KEY_PENGULANGAN,
                        KEY_WAKTUPENGINGAT, KEY_AKTIF, KEY_CATATAN, KEY_TANGGAL, KEY_AKTIF_HISTORY},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        Jadwal jadwal = null;
        if (cursor.moveToFirst()) {
            jadwal = new Jadwal(
                    cursor.getLong(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_HARI)),
                    cursor.getString(cursor.getColumnIndex(KEY_NAMA)),
                    cursor.getInt(cursor.getColumnIndex(KEY_JAM)),
                    cursor.getInt(cursor.getColumnIndex(KEY_MENIT)),
                    cursor.getString(cursor.getColumnIndex(KEY_PENGULANGAN)),
                    cursor.getInt(cursor.getColumnIndex(KEY_WAKTUPENGINGAT)),
                    cursor.getString(cursor.getColumnIndex(KEY_AKTIF)),
                    cursor.getString(cursor.getColumnIndex(KEY_CATATAN)),
                    cursor.getLong(cursor.getColumnIndex(KEY_TANGGAL)),
                    cursor.getString(cursor.getColumnIndex(KEY_AKTIF_HISTORY))
            );
        }

        cursor.close();
        closeDB();
        return jadwal;
    }

    public Cursor getAllHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABEL_HISTORY,
                new String[]{ID, NAMA, HARI, TANGGAL, AKSI, AKTIF},
                AKTIF + "=?",
                new String[]{"true"}, null, null, TANGGAL + " DESC", null);
    }

    public ArrayList<Long> getAllIdHistory() {
        ArrayList<Long> idHistory = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABEL_HISTORY, new String[]{ID}, AKTIF + " = ?", new String[]{"true"}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                idHistory.add(cursor.getLong(cursor.getColumnIndex(ID)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDB();
        return idHistory;
    }

    /**
     * ambil data jadwal hari
     */
    public Cursor getJadwalHari(String hari) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABEL_JADWAL,
                new String[]{KEY_ID, KEY_HARI, KEY_NAMA, KEY_JAM, KEY_MENIT, KEY_PENGULANGAN, KEY_WAKTUPENGINGAT, KEY_AKTIF},
                KEY_HARI + "=?",
                new String[]{hari}, null, null, KEY_JAM + " ASC, " + KEY_MENIT + " ASC", null);
    }

    /**
     * ambil data semua id
     */
    public ArrayList<Long> getAllId() {
        ArrayList<Long> idJadwal = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABEL_JADWAL, new String[]{KEY_ID}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                idJadwal.add(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDB();
        return idJadwal;
    }

    /**
     * ambil semua data id hari
     */
    public ArrayList<Long> getAllIdHari(String hari) {
        ArrayList<Long> idJadwal = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABEL_JADWAL, new String[]{KEY_ID}, KEY_HARI + "=?", new String[]{hari}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                idJadwal.add(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDB();
        return idJadwal;
    }

    /**
     * ambil semua data id dimana aktif = "true"
     */
    public ArrayList<Long> getAktifTrue() {
        ArrayList<Long> aktifTrue = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABEL_JADWAL, new String[]{KEY_ID}, KEY_AKTIF + "=?",
                new String[]{"true"}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                aktifTrue.add(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDB();
        return aktifTrue;
    }

    /**
     * update data
     */
    public int updateJadwal(long id, String hari, String nama, int jam, int menit, String pengulangan,
                            int waktuPengingat, String aktif, String catatan) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_HARI, hari);
        values.put(KEY_NAMA, nama);
        values.put(KEY_JAM, jam);
        values.put(KEY_MENIT, menit);
        values.put(KEY_PENGULANGAN, pengulangan);
        values.put(KEY_WAKTUPENGINGAT, waktuPengingat);
        values.put(KEY_AKTIF, aktif);
        values.put(KEY_CATATAN, catatan);

        int update = db.update(TABEL_JADWAL, values, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        closeDB();
        return update;
    }

    public int updateHistory(long tanggal, String nama, String hari) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAMA, nama);
        values.put(HARI, hari);

        int update = db.update(TABEL_HISTORY, values, TANGGAL + " = ?", new String[]{String.valueOf(tanggal)});

        closeDB();
        return update;
    }

    public int updateJadwalAktifHistory(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AKTIF_HISTORY, "true");

        int update = db.update(TABEL_JADWAL, values, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        closeDB();
        return update;
    }

    public int updateJadwalAktifHistory24() {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AKTIF_HISTORY, "true");

        int update = db.update(TABEL_JADWAL, values, "(" + TANGGAL + " <= (strftime('%s','now', '-1 day')*1000.0))", null);

        closeDB();
        return update;
    }

    public int updateAktifHistory(long tanggal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AKTIF, "true");

        int update = db.update(TABEL_HISTORY, values, TANGGAL + " = ?", new String[]{String.valueOf(tanggal)});

        closeDB();
        return update;
    }

    public int updateHistory24() {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AKTIF, "true");

        int update = db.update(TABEL_HISTORY, values, "(" + TANGGAL + " <= (strftime('%s','now', '-1 day')*1000.0))", null);

        closeDB();
        return update;
    }

    /**
     * delete data
     */
    public int deleteJadwal(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(TABEL_JADWAL, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        closeDB();
        return delete;
    }

    public int deleteHari(String hari) {
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(TABEL_JADWAL, KEY_HARI + " = ?", new String[]{hari});

        closeDB();
        return delete;
    }

    public int deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(TABEL_JADWAL, null, null);

        closeDB();
        return delete;
    }

    public int deleteHistory(long tanggal) {
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(TABEL_HISTORY, TANGGAL + " = ?", new String[]{String.valueOf(tanggal)});

        closeDB();
        return delete;
    }

    public int deleteAllHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(TABEL_HISTORY, AKTIF + " = ?", new String[]{"true"});

        closeDB();
        return delete;
    }

    /**
     * tutup koneksi
     */
    private void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
