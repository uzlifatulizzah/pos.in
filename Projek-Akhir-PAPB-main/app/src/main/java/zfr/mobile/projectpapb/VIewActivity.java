package zfr.mobile.projectpapb;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class VIewActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private RecyclerView recyclerView;
    private Button btn_add;
    private ProgressDialog progressDialog;
    private List<User> list = new ArrayList<>();
    private AdapterUser adapterUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        recyclerView = findViewById(R.id.recyclerview);
        btn_add = findViewById(R.id.btn_add);

        progressDialog = new ProgressDialog(VIewActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Mengambil data...!");

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        adapterUser = new AdapterUser(getApplicationContext(), list);
        adapterUser.setOnItemClickListener(new AdapterUser.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showOptionsDialog(position);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapterUser);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditorActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    private void getData() {
        progressDialog.show();
        list.clear();

        String[] projection = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_NAMA_PENERIMA,
                DatabaseHelper.COLUMN_NAMA_PENGIRIM,
                DatabaseHelper.COLUMN_ALAMAT_PENERIMA,
                DatabaseHelper.COLUMN_JENIS_PENGIRIMAN,
                DatabaseHelper.COLUMN_BUKTI_FOTO
        };

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_USERS,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            User user = User.fromCursor(cursor);
            list.add(user);
        }

        adapterUser.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    private void showOptionsDialog(int pos) {
        final CharSequence[] dialogItems = {"Edit", "Hapus"};
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(VIewActivity.this);
        dialogBuilder.setItems(dialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        navigateToEditActivity(pos);
                        break;
                    case 1:
                        deleteData(list.get(pos).getId(), pos);
                        break;
                }
            }
        });
        dialogBuilder.show();
    }

    private void navigateToEditActivity(int pos) {
        Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
        intent.putExtra("id", list.get(pos).getId());
        intent.putExtra("nama-penerima", list.get(pos).getNamaPenerima());
        intent.putExtra("nama-pengirim", list.get(pos).getNamaPengirim());
        intent.putExtra("alamat-penerima", list.get(pos).getAlamatPenerima());
        intent.putExtra("jenis-pengiriman", list.get(pos).getJenisPengiriman());
        intent.putExtra("buktifoto", list.get(pos).getBuktifoto());
        startActivity(intent);
    }

    private void deleteData(long id, int position) {
        progressDialog.show();

        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        int deletedRows = database.delete(
                DatabaseHelper.TABLE_USERS,
                selection,
                selectionArgs
        );

        if (deletedRows > 0) {
            Toast.makeText(getApplicationContext(), "Data Berhasil Dihapus!", Toast.LENGTH_SHORT).show();
            list.remove(position);
            adapterUser.notifyItemRemoved(position);
        } else {
            Toast.makeText(getApplicationContext(), "Data Gagal dihapus!", Toast.LENGTH_SHORT).show();
        }

        progressDialog.dismiss();
    }
}
