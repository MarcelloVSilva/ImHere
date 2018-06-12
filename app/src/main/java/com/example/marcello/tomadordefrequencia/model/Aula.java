package com.example.marcello.tomadordefrequencia.model;

import java.lang.reflect.Array;

/**
 * Created by marcello on 6/12/18.
 */

public class Aula {
    public String data;
    public String hora;
    public String sala;
    public Array checkin;

    public Aula(String data, String hora, String sala, Array checkin, Array checkout) {
        this.data = data;
        this.hora = hora;
        this.sala = sala;
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public Array checkout;

    public Aula() {
    }
}
