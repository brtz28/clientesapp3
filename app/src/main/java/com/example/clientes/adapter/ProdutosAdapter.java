package com.example.clientes.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clientes.R;
import com.example.clientes.model.Produto;

import java.text.NumberFormat;
import java.util.List;

public class ProdutosAdapter extends ArrayAdapter<Produto> {

    private final Context context;

    public ProdutosAdapter(@NonNull Context context, @NonNull List<Produto> produtos) {
        super(context, 0, produtos);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    //@SuppressWarnings("StatementWithEmptyBody")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Devolve o objeto do modelo
        Produto produto = getItem(position);

        //infla a view
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_produto_adapter, parent, false);
        }

        //mapeia os componentes da UI para vincular os dados do objeto de modelo
        TextView tvNome = convertView.findViewById(R.id.tvNomeProdutoAdapter);
        TextView tvEstoque = convertView.findViewById(R.id.tvEstoqueProdutoAdapater);
        TextView tvValor = convertView.findViewById(R.id.tvValorProdutoItemAdapter);
        ImageView imvFoto = convertView.findViewById(R.id.imvFotoProdutoAdapter);

        //vincula os dados do objeto de modelo à view
        assert produto != null;
        tvNome.setText(produto.getNome());
        tvEstoque.setText(produto.getQuantidade().toString());
        tvValor.setText(NumberFormat.getCurrencyInstance().format(produto.getValor()));
        if(produto.getUrl_foto().equals("")){
            imvFoto.setImageResource(R.drawable.img_carrinho_de_compras);
        }else{
            //carrega a imagem do serviço Storage aqui
        }


        return convertView;
    }
}

