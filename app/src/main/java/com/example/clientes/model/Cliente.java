package com.example.clientes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cliente implements Serializable {
    private Long codigoDeBarras;
    private String cpf;
    private String nome;
    private String sobrenome;
    private String email;
    private String idade;
    private boolean situacao;
    private String url_foto;
    private String key;
    private List<String> pedidos = new ArrayList<>();

    public Cliente(){
    }

    public Long getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(Long codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public boolean getSituacao() {
        return situacao;
    }

    public void setSituacao(boolean situacao) {
        this.situacao = situacao;
    }



    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail(){return email;}

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getUrl_foto() {
        return url_foto;
    }

    public void setUrl_foto(String url_foto) {
        this.url_foto = url_foto;
    }

    public List<String> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<String> pedidos) {
        this.pedidos = pedidos;
    }


    public String getKey() {
        return key;
    }

   public void setKey(String key) {
        this.key = key;
    }

    /*
    @Override
    public String toString() {
        return "Cliente{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", idade='" + idade + '\'' +
                ", url_foto='" + url_foto + '\'' +
                ", key='" + key + '' +
                '}';
    }
    */

    @Override
    public String toString() {
        return "Cliente{" +
                "codigoDeBarras=" + codigoDeBarras +
                ", cpf='" + cpf + '\'' +
                ", nome='" + nome + '\'' +
                ", sobrenome='" + sobrenome + '\'' +
                ", email='" + email + '\'' +
                ", idade='" + idade + '\'' +
                ", situacao=" + situacao +
                ", url_foto='" + url_foto + '\'' +
                ", key='" + key + '\'' +
                ", pedidos=" + pedidos +
                '}';
    }
}





