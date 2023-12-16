package zfr.mobile.projectpapb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserDatabase";
    private static final int DATABASE_VERSION = 1;

    // Nama tabel
    public static final String TABLE_USERS = "users";

    // Nama kolom
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAMA_PENERIMA = "nama_penerima";
    public static final String COLUMN_NAMA_PENGIRIM = "nama_pengirim";
    public static final String COLUMN_ALAMAT_PENERIMA = "alamat_penerima";
    public static final String COLUMN_JENIS_PENGIRIMAN = "jenis_pengiriman";
    public static final String COLUMN_BUKTI_FOTO = "buktifoto";

    // Membuat tabel
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAMA_PENERIMA + " TEXT, " +
                    COLUMN_NAMA_PENGIRIM + " TEXT, " +
                    COLUMN_ALAMAT_PENERIMA + " TEXT, " +
                    COLUMN_JENIS_PENGIRIMAN + " TEXT, " +
                    COLUMN_BUKTI_FOTO + " TEXT" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public long insertUser(String namaPenerima, String namaPengirim, String alamatPenerima, String jenisPengiriman, String buktiFotoUrl) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA_PENERIMA, namaPenerima);
        values.put(COLUMN_NAMA_PENGIRIM, namaPengirim);
        values.put(COLUMN_ALAMAT_PENERIMA, alamatPenerima);
        values.put(COLUMN_JENIS_PENGIRIMAN, jenisPengiriman);
        values.put(COLUMN_BUKTI_FOTO, buktiFotoUrl);

        long newRowId = database.insert(TABLE_USERS, null, values);
        database.close();
        return newRowId;
    }

    public int updateUser(String id, String namaPenerima, String namaPengirim, String alamatPenerima, String jenisPengiriman, String buktiFotoUrl) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA_PENERIMA, namaPenerima);
        values.put(COLUMN_NAMA_PENGIRIM, namaPengirim);
        values.put(COLUMN_ALAMAT_PENERIMA, alamatPenerima);
        values.put(COLUMN_JENIS_PENGIRIMAN, jenisPengiriman);
        values.put(COLUMN_BUKTI_FOTO, buktiFotoUrl);

        return database.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{id});
    }

    public Cursor getAllUsers() {
        SQLiteDatabase database = this.getReadableDatabase();
        String[] columns = {
                COLUMN_ID,
                COLUMN_NAMA_PENERIMA,
                COLUMN_NAMA_PENGIRIM,
                COLUMN_ALAMAT_PENERIMA,
                COLUMN_JENIS_PENGIRIMAN,
        };
        return database.query(TABLE_USERS, columns, null, null, null, null, null);
    }
}
