package zfr.mobile.projectpapb;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;

import zfr.mobile.projectpapb.DatabaseHelper;

public class User {
    private long id;
    private String namaPenerima;
    private String namaPengirim;
    private String alamatPenerima;
    private String jenisPengiriman;
    private String buktifotourl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNamaPenerima() {
        return namaPenerima;
    }

    public void setNamaPenerima(String namaPenerima) {
        this.namaPenerima = namaPenerima;
    }

    public String getNamaPengirim() {
        return namaPengirim;
    }

    public void setNamaPengirim(String namaPengirim) {
        this.namaPengirim = namaPengirim;
    }

    public String getAlamatPenerima() {
        return alamatPenerima;
    }

    public void setAlamatPenerima(String alamatPenerima) {
        this.alamatPenerima = alamatPenerima;
    }

    public String getJenisPengiriman() {
        return jenisPengiriman;
    }

    public void setJenisPengiriman(String jenisPengiriman) {
        this.jenisPengiriman = jenisPengiriman;
    }

    public String getBuktifoto() {
        return buktifotourl;
    }

    public void setBuktifoto(String buktifoto) {
        this.buktifotourl = buktifoto;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAMA_PENERIMA, namaPenerima);
        values.put(DatabaseHelper.COLUMN_NAMA_PENGIRIM, namaPengirim);
        values.put(DatabaseHelper.COLUMN_ALAMAT_PENERIMA, alamatPenerima);
        values.put(DatabaseHelper.COLUMN_JENIS_PENGIRIMAN, jenisPengiriman);
        values.put(DatabaseHelper.COLUMN_BUKTI_FOTO, buktifotourl);
        return values;
    }

    public static User fromCursor(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
        user.setNamaPenerima(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAMA_PENERIMA)));
        user.setNamaPengirim(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAMA_PENGIRIM)));
        user.setAlamatPenerima(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ALAMAT_PENERIMA)));
        user.setJenisPengiriman(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_JENIS_PENGIRIMAN)));
        user.setBuktifoto(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUKTI_FOTO)));
        return user;
    }
}
