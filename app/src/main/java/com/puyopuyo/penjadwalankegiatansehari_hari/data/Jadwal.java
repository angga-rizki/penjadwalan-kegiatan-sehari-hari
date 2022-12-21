package com.puyopuyo.penjadwalankegiatansehari_hari.data;

public class Jadwal {
    private long id;
    private String hari;
    private String nama;
    private int jam;
    private int menit;
    private String pengulangan;
    private int waktuPengingat;
    private String aktif;
    private String catatan;
    private long tanggal;
    private String aktifHistory;

    //constructor
    Jadwal(long id, String hari, String nama, int jam, int menit, String pengulangan, int waktuPengingat,
           String aktif, String catatan, long tanggal, String aktifHistory) {
        this.id = id;
        this.hari = hari;
        this.nama = nama;
        this.jam = jam;
        this.menit = menit;
        this.pengulangan = pengulangan;
        this.waktuPengingat = waktuPengingat;
        this.aktif = aktif;
        this.catatan = catatan;
        this.tanggal = tanggal;
        this.aktifHistory = aktifHistory;
    }

    //getter
    public long getId() { return id; }
    public String getHari() { return hari; }
    public String getNama() { return nama; }
    public int getJam() { return jam; }
    public int getMenit() { return menit; }
    public String getPengulangan() { return pengulangan; }
    public int getWaktuPengingat() { return waktuPengingat; }
    public String getAktif() { return aktif; }
    public String getCatatan() { return catatan; }
    public long getTanggal() { return tanggal; }
    public String getAktifHistory() { return aktifHistory; }
}
