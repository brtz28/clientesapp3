package com.example.clientes.activity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clientes.R;
import com.example.clientes.model.User;
import com.example.clientes.setup.AppSetup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "loginActivity";
    private FirebaseAuth mAuth;
    private EditText etEmail, etSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        AppSetup.mAuth = mAuth;

        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);

        findViewById(R.id.bt_sigin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String senha = etSenha.getText().toString();
                if(!email.isEmpty() && !senha.isEmpty()){
                    signin(email,senha);
                }else{
                    Snackbar.make(findViewById(R.id.container_activity_login),"Preencha todos os campos",Snackbar.LENGTH_LONG).show();
                    etEmail.setError(getString(R.string.input_error_invalido));
                    etSenha.setError(getString(R.string.input_error_invalido));
                }
            }
        });

        findViewById(R.id.tvEsqueceuSenha_tela_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString();
                if(!email.isEmpty()){
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG,"Reset de Email enviado para "+email);
                                Toast.makeText(LoginActivity.this, "Reset de senha enviado para" + email, Toast.LENGTH_SHORT).show();
                            }else{
                                Log.d(TAG, "Reset de Senha Falhou " + task.getException());
                                Snackbar.make(findViewById(R.id.container_activity_login),"Sign Up falhou", Snackbar.LENGTH_LONG).show();
                                etEmail.setError("Erro input");
                            }
                        }
                    });
                }else{
                    Snackbar.make(findViewById(R.id.container_activity_login), "Insira um email",Snackbar.LENGTH_LONG).show();
                    etEmail.setError(getString(R.string.input_error_invalido));
                }
            }
        });
    }

    private void signin(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
//                            if(mAuth.getCurrentUser().isEmailVerified()){
//                                Log.d(TAG, "signInWithEmail:success");
                            setUserSessao(mAuth.getCurrentUser());
//                            }else{
//                                Snackbar.make(findViewById(R.id.container_activity_login), "Valide seu email para o singin.", Snackbar.LENGTH_LONG).show();
//                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure ",  task.getException());
                            if(Objects.requireNonNull(task.getException()).getMessage().contains("password")){
                                Snackbar.make(findViewById(R.id.container_activity_login), R.string.password_fail, Snackbar.LENGTH_LONG).show();
                                etSenha.setError(getString(R.string.input_error_invalido));
                            }else{
                                Snackbar.make(findViewById(R.id.container_activity_login), R.string.email_fail, Snackbar.LENGTH_LONG).show();
                                etEmail.setError(getString(R.string.input_error_invalido));
                            }
                        }
                    }
                });
    }

    private void setUserSessao(final FirebaseUser firebaseUser) {

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        AppSetup.user = dataSnapshot.getValue(User.class);
                        AppSetup.user.setFirebaseUser(firebaseUser);
                        startActivity(new Intent(LoginActivity.this, ProdutosActivity.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(LoginActivity.this, getString(R.string.toast_problemas_signin), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    }
