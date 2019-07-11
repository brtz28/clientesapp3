package com.example.clientes.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.clientes.R;
import com.example.clientes.adapter.PedidosAdapter;
import com.example.clientes.model.Pedido;
import com.example.clientes.setup.AppSetup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PedidosActivity extends AppCompatActivity {

    private ListView lv_pedidos;
    private static final String TAG = "pedidosactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        //ativa o botão home na actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        lv_pedidos = findViewById(R.id.lv_pedidos);
        lv_pedidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        AppSetup.pedidos.clear();

        for(String pedido : AppSetup.cliente.getPedidos()){
            if(!pedido.equals(" ")) {
                DatabaseReference myRef = database.getReference("pedidos").child(pedido);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("data", String.valueOf(dataSnapshot.getValue(Pedido.class)));
                        Pedido pedido = dataSnapshot.getValue(Pedido.class);
                        pedido.setKey(dataSnapshot.getKey());
                        AppSetup.pedidos.add(pedido);

                        lv_pedidos.setAdapter(new PedidosAdapter(PedidosActivity.this, AppSetup.pedidos));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

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
