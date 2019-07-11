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
import com.example.clientes.model.Cliente;

import java.util.List;

public class ClientesAdapter extends ArrayAdapter<Cliente> {

    private final Context context;

    public ClientesAdapter(@NonNull Context context, @NonNull List<Cliente> clientes){
        super(context, 0, clientes);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("StatementWithEmptyBody")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        Cliente cliente = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.cliente_adapter, parent, false);
        }

        TextView tvNome = convertView.findViewById(R.id.tvNomeClienteAdapter);
        TextView tvEmail = convertView.findViewById(R.id.tvEmailClienteAdapter);
        TextView tvIdade = convertView.findViewById(R.id.tvIdadeCliente);
        ImageView imvFoto = convertView.findViewById(R.id.imvFotoClienteAdapter);
        TextView tvCPF = convertView.findViewById(R.id.tvCPFClienteAdapter);

        assert cliente != null;
        tvNome.setText(cliente.getNome());
        tvEmail.setText(cliente.getEmail());
        tvIdade.setText(cliente.getIdade());
        tvCPF.setText(cliente.getCpf());

        if(cliente.getUrl_foto() != null){
            //Picasso.get().load(cliente.getUrl_foto()).into(imvFoto);
        }else{
            imvFoto.setImageResource(R.drawable.img_cliente);
        }

        return convertView;
    }



}
