package com.example.clientes.activity;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.clientes.R;
import com.example.clientes.model.User;
import com.example.clientes.setup.AppSetup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class UsuarioAdminActivity extends AppCompatActivity {

    private static String TAG = "loginActivity";
    private FirebaseAuth mAuth;
    private EditText etEmailUser, etPasswordUser, etNomeUser, etSobrenomeUser;
    private Spinner spFuncaoUser;
    private String[] FUNCAO = new String[]{"Vendedor", "Administrador"};
    private Button cadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_admin);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        AppSetup.mAuth = mAuth;

        etNomeUser = findViewById(R.id.etNomeUser);
        etSobrenomeUser = findViewById(R.id.etSobrenomeUser);
        etEmailUser = findViewById(R.id.etEmailUser);
        etPasswordUser = findViewById(R.id.etPasswordUser);
        spFuncaoUser = findViewById(R.id.spFuncaoUser);
        cadastrar = findViewById(R.id.btCadastrarUser);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, FUNCAO);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFuncaoUser.setAdapter(adapter);

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmailUser.getText().toString();
                String senha = etPasswordUser.getText().toString();

                if (!email.isEmpty() && !senha.isEmpty()) {
                    signup(email, senha);
                } else {
                    Snackbar.make(findViewById(R.id.container_activity_login), "Preencha todos os campos.", Snackbar.LENGTH_LONG).show();
                    etEmailUser.setError(getString(R.string.input_error_invalido));
                    etPasswordUser.setError(getString(R.string.input_error_invalido));
                }
            }
        });
    }

    private void signup(String email, String senha) {
        mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail: Sucess");
                    cadastrarUser();
                    Toast.makeText(UsuarioAdminActivity.this, "Cadastrado com Sucesso", Toast.LENGTH_SHORT).show();

                } else {
                    Log.w(TAG, "CreateUserWithEmail:failure", task.getException());
                    if (Objects.requireNonNull(task.getException()).getMessage().contains("email")) {
                        //Snackbar.make(findViewById(R.id.container_activity_login), "Email ja cadastrado", Snackbar.LENGTH_LONG).show();
                        etEmailUser.setError(getString(R.string.input_error_invalido));
                    } else {
                        //Snackbar.make(findViewById(R.id.container_activity_login), "Falha", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void cadastrarUser() {
        User user = new User();
        user.setFirebaseUser(mAuth.getCurrentUser());
        user.setNome(etNomeUser.getText().toString());
        user.setSobrenome(etSobrenomeUser.getText().toString());
        user.setFuncao(FUNCAO[spFuncaoUser.getSelectedItemPosition()]);
        user.setEmail(mAuth.getCurrentUser().getEmail());
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(user.getFirebaseUser().getUid())
                .setValue(user);
        AppSetup.user = user;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}

