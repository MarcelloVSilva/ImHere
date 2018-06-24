package com.example.marcello.tomadordefrequencia.model;

/**
 * Created by marcello on 6/24/18.
 */

public class Sala {
    public Long andar;
    public String local;
    public Long numero;

    public Sala(Long andar, String local, Long numero) {
        this.andar = andar;
        this.local = local;
        this.numero = numero;
    }

    public Sala(){

    }
}
