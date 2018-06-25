package com.example.marcello.tomadordefrequencia.model;

import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * Created by marcello on 6/12/18.
 */

public class Aula implements Serializable {
    public Object hora;
    public String data;
    public String sala;
    public Object checkin;
    public Object checkout;

    public Aula(){

    }

    public Aula(String data, Object hora, String sala, Object checkin, Object checkout) {
        this.hora = hora;
        this.data = data;
        this.sala = sala;
        this.checkin = checkin;
        this.checkout = checkout;
    }

}
