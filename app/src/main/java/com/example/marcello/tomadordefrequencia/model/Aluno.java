package com.example.marcello.tomadordefrequencia.model;

import java.io.Serializable;

/**
 * Created by marcello on 6/12/18.
 */

public class Aluno implements Serializable {
    public String nfc_id;
    public String nome;

    public Aluno(String nfc_id, String nome) {
        this.nfc_id = nfc_id;
        this.nome = nome;
    }

    public Aluno(){

    }



}
