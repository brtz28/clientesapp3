package com.example.clientes.activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientes.R;
import com.example.clientes.model.ItemPedido;
import com.example.clientes.model.Produto;
import com.example.clientes.setup.AppSetup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;

public class ProdutoDetalheActivity extends AppCompatActivity {

    private static final String TAG = "produtoDetalheActivity";
    private TextView tvNome, tvDescricao, tvValor, tvEstoque;
    private EditText etQuantidade;
    private ImageView imvFoto;
    private Button btVender;
    private Produto produto;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_detalhe);

        //mapeia os componentes da UI
        tvNome = findViewById(R.id.tvNomeProduto);
        tvDescricao = findViewById(R.id.tvDerscricaoProduto);
        tvValor = findViewById(R.id.tvValorProduto);
        tvEstoque = findViewById(R.id.tvQuantidadeProduto);
        etQuantidade = findViewById(R.id.etQuantidade);
        imvFoto = findViewById(R.id.imvFoto);
        btVender = findViewById(R.id.btComprarProduto);

        //obtém a position anexada como metadado
        Integer position = getIntent().getExtras().getInt("position");
        produto = AppSetup.produtos.get(position);
        Log.d(TAG, ""+ produto.equals(AppSetup.produtos.get(position)));

        //bindview
        tvNome.setText(AppSetup.produtos.get(position).getNome());
        tvDescricao.setText(AppSetup.produtos.get(position).getDescricao());
        tvValor.setText(NumberFormat.getCurrencyInstance().format(AppSetup.produtos.get(position).getValor()));
        if(produto.getUrl_foto() != ""){
            //carrega a imagem aqui
        }

        // obtém a referência do database e do nó
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Produtos/" + produto.getKey() + "/quantidade");
        // Escuta o database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer quantidade = dataSnapshot.getValue(Integer.class);
                tvEstoque.setText(String.format("%s %s", "Estoque", quantidade.toString()));
                produto.setQuantidade(quantidade);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btVender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppSetup.cliente == null){
                    startActivity(new Intent(ProdutoDetalheActivity.this, ClientesActivity.class));
                }else{
                    if(etQuantidade.getText().toString().isEmpty()){
                        Toast.makeText(ProdutoDetalheActivity.this, "Digite a quantidade.", Toast.LENGTH_SHORT).show();
                    }else{
                        Integer quantidade = Integer.valueOf(etQuantidade.getText().toString());
                        if(quantidade <= produto.getQuantidade()){
                            //vende
                            ItemPedido item = new ItemPedido();
                            item.setProduto(produto);
                            item.setQuantidade(quantidade);
                            item.setTotalItem(quantidade * produto.getValor());
                            item.setSituacao(true);
                            AppSetup.carrinho.add(item);
                            myRef.setValue(produto.getQuantidade()- quantidade);
                            Toast.makeText(ProdutoDetalheActivity.this, "Adicionado ao Carrinho", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ProdutoDetalheActivity.this, CarrinhoActivity.class));
                            finish();
                        }else{
                            Toast.makeText(ProdutoDetalheActivity.this, "Quantidade acima do estoque.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}



