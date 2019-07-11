package com.example.clientes.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.clientes.R;
import com.example.clientes.adapter.ProdutosAdapter;
import com.example.clientes.barcode.BarcodeCaptureActivity;
import com.example.clientes.model.Produto;
import com.example.clientes.setup.AppSetup;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProdutosActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "produtosactivity";
    private static final int RC_BARCODE_CAPTURE = 1;
    private DrawerLayout drawer;
    private ListView lvProdutos;
    private AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (AppSetup.user.getFuncao().equals("Administrador")){
            navigationView.getMenu().findItem(R.id.admGroup).setVisible(true);
        }



        lvProdutos = findViewById(R.id.lv_produtos);
        lvProdutos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(ProdutosActivity.this,"Clicou no cartão", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProdutosActivity.this, ProdutoDetalheActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });


        // obtém a referência do database e do nó
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/Produtos");

        // Escuta o database
        myRef.orderByChild("nome").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //imprime os dados originais no LogCat (veja que eles chegam na ordem de criação dos nós)
                Log.d(TAG, "Value is: " + dataSnapshot.getValue());

                AppSetup.produtos = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Produto produto = ds.getValue(Produto.class);
                    produto.setKey(ds.getKey()); //armazena a UUID gerada pelo banco
                    produto.setIndex(AppSetup.produtos.size());
                    AppSetup.produtos.add(produto);
                }

                //carrega os dados na View
                lvProdutos.setAdapter(new ProdutosAdapter(ProdutosActivity.this, AppSetup.produtos));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_activity_produtos, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menuitem_pesquisar).getActionView();
        searchView.setQueryHint("nome");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Produto> produtosTemp = new ArrayList<>();
                for(Produto produto : AppSetup.produtos){
                    if(produto.getNome().contains(newText)){
                        produtosTemp.add(produto);
                    }
                }
                //carrega os dados na View
                lvProdutos.setAdapter(new ProdutosAdapter(ProdutosActivity.this, produtosTemp));
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuitem_barcode:
                //Toast.makeText(this, "Ler código de barras", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProdutosActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true); //liga a funcionalidade autofoco
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false); //liga a lanterna do dispotivo
                startActivityForResult(intent, RC_BARCODE_CAPTURE);
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    //Toast.makeText(this, barcode.displayValue, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    //localiza o produto na lista (ou não)
                    boolean flag = true;
                    int position = 0;
                    for (Produto produto : AppSetup.produtos) {
                        if (String.valueOf(produto.getCodigoDeBarras()).equals(barcode.displayValue)) {
                            flag = false;
                            Intent intent = new Intent(ProdutosActivity.this, ProdutoDetalheActivity.class);
                            intent.putExtra("position", position);
                            startActivity(intent);
                            break;
                        }
                        position++;
                    }
                    if (flag) {
                        Snackbar.make(findViewById(R.id.container_activity_produtos), "codigo de barras não cadastrado", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Falha na leitura do código", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                Toast.makeText(this, String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)), Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_clientes:{
                startActivity(new Intent(ProdutosActivity.this, ClientesActivity.class));
                break;
            }

            case R.id.nav_carrinho:{
                if (AppSetup.carrinho.isEmpty()){
                    Toast.makeText(this, "O carrinho esta vazio", Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(new Intent(ProdutosActivity.this, CarrinhoActivity.class));
                }
                break;
            }

            case R.id.nav_produto_adminstracao: {
                startActivity(new Intent(ProdutosActivity.this, ProdutoAdminActivity.class));
                break;
            }

            case R.id.nav_cliente_administracao: {
                startActivity(new Intent(ProdutosActivity.this, ClienteAdminActivity.class));
                break;
            }

            case R.id.nav_usuario_administracao: {
                startActivity(new Intent(ProdutosActivity.this, UsuarioAdminActivity.class));
                break;
            }

            case R.id.nav_sobre:{
                startActivity(new Intent(ProdutosActivity.this, SobreActivity.class));
                break;
            }

            case R.id.nav_sair:{
                if(AppSetup.carrinho.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Deseja Sair?");

                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            finish();
                            //Toast.makeText(ProdutosActivity.this, "Sim=" + arg1, Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Toast.makeText(ProdutosActivity.this, "Não=" + arg1, Toast.LENGTH_SHORT).show();
                        }
                    });

                    alerta = builder.create();
                    alerta.show();

                    //finish();
                }else{
                    //colocar um modal pedindo a confirmação
                    //cancelar o pedido

                }
                break;
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}

