package com.example.clientes.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.clientes.R;
import com.example.clientes.adapter.ClientesAdapter;
import com.example.clientes.barcode.BarcodeCaptureActivity;
import com.example.clientes.model.Cliente;
import com.example.clientes.setup.AppSetup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.clientes.setup.AppSetup.clientes;

public class ClientesActivity extends AppCompatActivity {

    private static final String TAG = "clientesactivity";
    private ListView lvClientes;
    private static final int RC_BARCODE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);

        lvClientes = findViewById(R.id.lv_clientes);
        lvClientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selecionarCliente(position);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Clientes");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Value is: " + dataSnapshot.getValue());

                clientes = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Cliente cliente = ds.getValue(Cliente.class);
                    clientes.add(cliente);

                    lvClientes.setAdapter(new ClientesAdapter(ClientesActivity.this,clientes));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void selecionarCliente(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //adiciona um título e uma mensagem
        builder.setTitle("Confirmar Cliente");
        final Cliente cliente = clientes.get(position);
        builder.setMessage("Nome" + ": " + cliente.getNome());
        //adiciona os botões
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppSetup.cliente = cliente;
                Toast.makeText(ClientesActivity.this, "Cliente Selecionado", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Snackbar.make(findViewById(R.id.container_activity_clientes), "Cancelado", Snackbar.LENGTH_LONG).show();
            }
        });

        builder.show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_clientes, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menuitem_pesquisar).getActionView();
        searchView.setQueryHint("Pesquisar");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Cliente> clientesTemp = new ArrayList<>();
                for (Cliente cliente : clientes) {
                    if (cliente.getNome().contains(newText)) {
                        clientesTemp.add(cliente);
                    }
                }
                lvClientes.setAdapter(new ClientesAdapter(ClientesActivity.this, clientesTemp));

                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_barcode:
                Intent intent = new Intent(ClientesActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                startActivityForResult(intent, RC_BARCODE_CAPTURE);
                break;
        }
        return true;
    }


}
