package com.example.clientes.setup;

import android.graphics.Bitmap;

import com.example.clientes.model.Cliente;
import com.example.clientes.model.ItemPedido;
import com.example.clientes.model.Pedido;
import com.example.clientes.model.Produto;
import com.example.clientes.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppSetup {

    public static List<Produto> produtos = new ArrayList<>();
    public static List<Cliente> clientes = new ArrayList<>();
    public static List<ItemPedido> carrinho = new ArrayList<>();
    public static List<Pedido> pedidos = new ArrayList<>();
    public static Cliente cliente = null;
    public static Pedido pedido = null;
    public static FirebaseAuth mAuth = null;
    public static User user = null;
    public static Map<String, Bitmap> cacheProdutos = new HashMap<>();
    public static Map<String, Bitmap> cacheClientes = new HashMap<>();
}
