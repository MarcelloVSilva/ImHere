package com.example.marcello.tomadordefrequencia.model;

import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * Created by marcello on 6/12/18.
 */

public class Aula implements Serializable {
    public Hora hora;
    public String data;
    public String sala;
    public Processo checkin;
    public Processo checkout;

    public Aula(){

    }

    public Aula(String data, Hora hora, String sala, Processo checkin, Processo checkout) {
        this.hora = hora;
        this.data = data;
        this.sala = sala;
        this.checkin = checkin;
        this.checkout = checkout;
    }

}
