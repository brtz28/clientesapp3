package com.example.clientes.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.clientes.R;
import com.example.clientes.model.ItemPedido;

import java.text.NumberFormat;
import java.util.List;

public class CarrinhoAdapter extends ArrayAdapter<ItemPedido> {

    private final Context context;

    public CarrinhoAdapter(Context context, List<ItemPedido> carrinho) {
        super(context, 0, carrinho);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_carrinho_adapter, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //bindview
        ItemPedido item = getItem(position);
        holder.nomeProduto.setText(item.getProduto().getNome());
        holder.quantidade.setText(item.getQuantidade().toString());
        holder.totalDoItem.setText(NumberFormat.getCurrencyInstance().format(item.getTotalItem()));
        if(item.getProduto().getUrl_foto().equals("")){
            holder.pbFoto.setVisibility(View.INVISIBLE);
            holder.fotoProduto.setImageResource(R.drawable.img_carrinho_de_compras);
        }else{
            //carrega a imagem do serviço Storage aqui
        }
        return convertView;
    }

    private class ViewHolder{
        TextView nomeProduto;
        TextView quantidade;
        TextView totalDoItem;
        ImageView fotoProduto;
        ProgressBar pbFoto;

        public ViewHolder(View convertView){
            //mapeia os componentes da UI para vincular os dados do objeto de modelo
            nomeProduto = convertView.findViewById(R.id.tvNomeProdutoCarrinhoAdapter);
            quantidade = convertView.findViewById(R.id.tvQuantidadeDeProdutoCarrinhoAdapater);
            totalDoItem =  convertView.findViewById(R.id.tvTotalItemCarrinhoAdapter);
            fotoProduto = convertView.findViewById(R.id.imvFotoProdutoCarrinhoAdapter);
            pbFoto = convertView.findViewById(R.id.pb_foto_carrinho);
        }
    }

}
