package zfr.mobile.projectpapb;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.checkerframework.common.reflection.qual.NewInstance;

public class RegisterActivity extends AppCompatActivity {

    private EditText editName, editEmail, editPassword, editPasswordConf;
    private Button btnregister, btnlogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editName = findViewById(R.id.namalengkap);
        editEmail = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);
        editPasswordConf = findViewById(R.id.password_conf);

        btnregister = findViewById(R.id.btn_register);
        btnlogin = findViewById(R.id.btn_login);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Silahkan Tunggu!");
        progressDialog.setCancelable(false);

        btnlogin.setOnClickListener(view -> {
            finish();
        });

        btnregister.setOnClickListener(view -> {
            RegisterCheck();
        });
    }

    private void RegisterCheck() {
        if(editName.getText().length() <= 0) {
            Toast.makeText(getApplicationContext(), "Silakan isi nama terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(editEmail.getText().length() <= 0) {
            Toast.makeText(getApplicationContext(), "Silakan isi email terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(editPassword.getText().length() <= 0) {
            Toast.makeText(getApplicationContext(), "Silakan isi password terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(editPassword.getText().length() <= 0) {
            Toast.makeText(getApplicationContext(), "Silakan isi password terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(editPasswordConf.getText().length() <= 0) {
            Toast.makeText(getApplicationContext(), "Silakan isi konfirmasi password terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editPassword.getText().toString().equals(editPasswordConf.getText().toString())) {
            register(editName.getText().toString(), editEmail.getText().toString(), editPassword.getText().toString());
        } else {
            Toast.makeText(getApplicationContext(), "Silakan masukkan password yang sama!", Toast.LENGTH_SHORT).show();
        }

    }

    private void register(String name, String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && task.getResult()!=null) {
                            progressDialog.show();
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();
                            firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    reload();
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void updateUI(FirebaseUser user) {

                    }
                });
    }

    private void reload(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
}