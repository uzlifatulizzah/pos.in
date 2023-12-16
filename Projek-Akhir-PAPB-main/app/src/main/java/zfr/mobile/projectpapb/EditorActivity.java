package zfr.mobile.projectpapb;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class EditorActivity extends AppCompatActivity {
    private EditText editNamaPenerima, editNamaPengirim, editAlamatPenerima, editJenisPengiriman;
    private Button btn_save, back;
    private ImageView buktifoto;
    private ProgressDialog progressDialog;
    private long id = -1;
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private Bitmap selectedImageBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editNamaPenerima = findViewById(R.id.nama_penerima);
        editNamaPengirim = findViewById(R.id.nama_pengirim);
        editAlamatPenerima = findViewById(R.id.Alamat_penerima);
        editJenisPengiriman = findViewById(R.id.Jenis_pengiriman);

        buktifoto = findViewById(R.id.buktifoto);

        buktifoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        progressDialog = new ProgressDialog(EditorActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Sedang Menyimpan...!");

        btn_save = findViewById(R.id.btn_save);
        back = findViewById(R.id.back);

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editNamaPenerima.getText().length() > 0 && editNamaPengirim.getText().length() > 0 && editAlamatPenerima.getText().length() > 0 && editJenisPengiriman.getText().length() > 0) {
                    if (selectedImageBitmap != null) {
                        // Simpan gambar ke Firebase Storage dan dapatkan URL-nya
                        uploadImageToFirebaseStorage(selectedImageBitmap);
                    } else {
                        Toast.makeText(getApplicationContext(), "Gambar tidak valid!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Pastikan semua data sudah terisi!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getLongExtra("id", -1);
            Log.d("Get Id", "id =" + id);
            editNamaPenerima.setText(intent.getStringExtra("nama-penerima"));
            editNamaPengirim.setText(intent.getStringExtra("nama-pengirim"));
            editAlamatPenerima.setText(intent.getStringExtra("alamat-penerima"));
            editJenisPengiriman.setText(intent.getStringExtra("jenis-pengiriman"));
            Glide.with(getApplicationContext()).load(intent.getStringExtra("buktifoto")).into(buktifoto);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void uploadImageToFirebaseStorage(Bitmap bitmap) {
        progressDialog.show();

        // Menggunakan Firebase Storage Reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("images") // Ganti dengan path penyimpanan yang sesuai
                .child(System.currentTimeMillis() + ".jpg"); // Menambahkan timestamp agar nama file unik

        // Mengkompres gambar menjadi format JPEG dengan kualitas 100
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();

        // Mengunggah gambar ke Firebase Storage
        UploadTask uploadTask = storageReference.putBytes(imageData);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // Jika berhasil, dapatkan URL gambar yang diunggah
                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> uriTask) {
                            if (uriTask.isSuccessful()) {
                                String imageUrl = uriTask.getResult().toString();
                                Log.d("onComplete", imageUrl);
                                // Setelah mendapatkan URL gambar, Anda dapat menyimpan data ke database
                                saveDataToDatabaseWithImage(
                                        editNamaPenerima.getText().toString(),
                                        editNamaPengirim.getText().toString(),
                                        editAlamatPenerima.getText().toString(),
                                        editJenisPengiriman.getText().toString(),
                                        imageUrl // Gunakan URL gambar yang didapatkan dari Firebase Storage
                                );
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Gagal mendapatkan URL gambar!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Gagal mengunggah gambar!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveDataToDatabaseWithImage(String namaPenerima, String namaPengirim, String alamatPenerima,
                                             String jenisPengiriman, String imageUrl) {
        progressDialog.show();
        long newRowId;
        String parsedId = String.valueOf(id);

        if (id != -1) {
            Log.d("save data to database", "Updating Data Now!");
            newRowId = dbHelper.updateUser(
                    parsedId,
                    namaPenerima,
                    namaPengirim,
                    alamatPenerima,
                    jenisPengiriman,
                    imageUrl // Gunakan URL gambar yang didapatkan dari Firebase Storage
            );
        } else {
            Log.d("save data to database", "Inserting Data Now!");
            newRowId = dbHelper.insertUser(
                    namaPenerima,
                    namaPengirim,
                    alamatPenerima,
                    jenisPengiriman,
                    imageUrl // Gunakan URL gambar yang didapatkan dari Firebase Storage
            );
        }

        if (newRowId != -1) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Data Berhasil Disimpan!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Gagal menyimpan data!", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from your gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setTitle("Pos.in");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 10);
                } else if (items[item].equals("Choose from your gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), 20);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20 && data != null) {
            final Uri path = data.getData();
            Thread thread = new Thread(() -> {
                try {
                    assert path != null;
                    InputStream inputStream = getContentResolver().openInputStream(path);
                    selectedImageBitmap = BitmapFactory.decodeStream(inputStream);
                    buktifoto.post(() -> {
                        buktifoto.setImageBitmap(selectedImageBitmap);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
        if (requestCode == 10 && resultCode == RESULT_OK && data != null) {
            selectedImageBitmap = (Bitmap) data.getExtras().get("data");
            buktifoto.setImageBitmap(selectedImageBitmap);
        }
    }
}
