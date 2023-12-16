package zfr.mobile.projectpapb;

import static android.icu.text.Transliterator.getDisplayName;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private TextView textName;

    private Button btnLogout, btn_add_data, btn_display_data;
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout = findViewById(R.id.btn_logout);
        btn_add_data = findViewById(R.id.btn_add_data);
        btn_display_data = findViewById(R.id.btn_display_data);

        textName = findViewById(R.id.name);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null){
            textName.setText(firebaseUser.getDisplayName());
        } else {
            textName.setText("Login Gagal!");
        }

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

        btn_add_data.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), EditorActivity.class));
        });

        btn_display_data.setOnClickListener(v -> {
            // Use an AsyncTask to perform background operations
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    // Start VIewActivity
                    startActivity(new Intent(getApplicationContext(), VIewActivity.class));
                    return null;
                }
            }.execute();
        });

    }
}