package com.example.clientes.activity;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientes.R;
import com.example.clientes.adapter.CarrinhoAdapter;
import com.example.clientes.model.ItemPedido;
import com.example.clientes.model.Pedido;
import com.example.clientes.setup.AppSetup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CarrinhoActivity extends AppCompatActivity {


    private ListView lv_carrinho;
    private double ValorTotal = new Double(0);
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private List<ItemPedido> itens;
    private TextView tvTotalPedidoCarrinho;
    private double total;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        lv_carrinho = findViewById(R.id.lv_carrinho);
        lv_carrinho.setAdapter(new CarrinhoAdapter(CarrinhoActivity.this, AppSetup.carrinho));

        lv_carrinho.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            excluirItem(position);

            return true;
            }
        });

        lv_carrinho.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            editarItem(position);
            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_carrinho, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_salvar:
                salvarPedido();
                break;
            case R.id.menuitem_cancelar:
                cancelarPedido();
        }
        return true;
    }

    private void excluirItem(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Voce Deseja Excluir este Item?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppSetup.carrinho.remove(position);
                Toast.makeText(CarrinhoActivity.this, "Produto Removido", Toast.LENGTH_SHORT).show();

                atualizarView();
                atualizarEstoque(position);
            }
        });

        builder.show();
    }

    private void salvarPedido() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //adiciona um título e uma mensagem
        builder.setTitle("Confirmar");
        builder.setMessage("Finalizar o Pedido?");
        //adiciona os botões
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (AppSetup.carrinho == null) {
                    Toast.makeText(CarrinhoActivity.this, "Voce nao possui produtos no carrinho", Toast.LENGTH_SHORT).show();
                } else {
                    Date dataHoraAtual = new Date();

                    DatabaseReference myRef = database.getReference("pedidos");
                    String key = myRef.push().getKey();

                    Pedido pedido = new Pedido();
                    pedido.setCliente(AppSetup.cliente);
                    pedido.setDataCriacao(dataHoraAtual);
                    pedido.setDataModificacao(dataHoraAtual);
                    pedido.setEstado("aberto");
                    pedido.setFormaDePagamento("a vista");
                    pedido.setItens(AppSetup.carrinho);
//                    pedido.setKey(Long.valueOf(key));
                    pedido.setSituacao(true);
                    pedido.setTotalPedido(total);

                    myRef.child(key).setValue(pedido);

                    AppSetup.clientes = null;
                    AppSetup.carrinho.clear();
                    AppSetup.pedido = null;
                    finish();
                }
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }


    private void cancelarPedido() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirmação Cancelamento");
        builder.setMessage("Cancelar o pedido do cliente?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("tamanho", String.valueOf(AppSetup.carrinho.size()));
                for (ItemPedido item : AppSetup.carrinho) {
                    DatabaseReference myRef = database.getReference("Produtos/" + item.getProduto().getKey() + "/quantidade");
                    myRef.setValue(item.getQuantidade() + item.getProduto().getQuantidade());
                    Log.d("removido", item.toString());
                    Log.d("item", "item removido");
                }
                AppSetup.carrinho.clear();
                AppSetup.cliente = null;
                finish();
            }
        });
        builder.setNegativeButton("Nâo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }




    private void editarItem(final int position ){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar");
        builder.setMessage("Deseja Editar este Item?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                atualizarEstoque(position);
                Intent intent = new Intent(CarrinhoActivity.this, ProdutoDetalheActivity.class);
                Integer index = AppSetup.carrinho.get(position).getProduto().getIndex();
                intent.putExtra("position", index);
                startActivity(intent);

                AppSetup.carrinho.remove(position);

                Toast.makeText(CarrinhoActivity.this, "Click para atualizar", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

/*
    private void editarItem(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirmação");
        builder.setMessage("Editar este Produto?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                atualizarEstoque(position);

                Intent intent = new Intent(CarrinhoActivity.this, ProdutoDetalheActivity.class);
                Integer index = AppSetup.carrinho.get(position).getProduto().getIndex();
                //Log.d("OUTPUT: ", "Valor do index: " +  index);

                intent.putExtra("position", index);

                startActivity(intent);

                AppSetup.carrinho.remove(position);

                //Toast.makeText(CarrinhoActivity.this, "Click para atualizar", Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //por enquanto nada
            }
        });

        builder.show();
    }
    */


    private void atualizarEstoque(final int position){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Produtos/").child(itens.get(position).getProduto().getKey()).child("quantidade");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //referencia da posição do estoque
                long quantidade = (long) dataSnapshot.getValue();

                myRef.setValue(itens.get(position).getQuantidade() + quantidade);

                atualizarView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    public void atualizarView() {
        TextView tvTotalPedidoCarrinho = findViewById(R.id.tvTotalPedidoCarrinho);
        lv_carrinho.setAdapter(new CarrinhoAdapter(CarrinhoActivity.this, AppSetup.carrinho));
        Double total = 0.0;
        for (ItemPedido itemPedido : AppSetup.carrinho) {
            total = total + itemPedido.getTotalItem();
        }
        tvTotalPedidoCarrinho.setText(NumberFormat.getCurrencyInstance().format(total));
    }


    @Override
    protected void onResume(){
        super.onResume();
        if(!AppSetup.carrinho.isEmpty()){
            atualizarView();
        }

        itens = new ArrayList<>();
        itens.addAll(AppSetup.carrinho);
    }

}


