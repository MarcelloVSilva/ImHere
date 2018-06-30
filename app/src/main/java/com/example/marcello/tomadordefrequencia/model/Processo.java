package com.example.marcello.tomadordefrequencia.model;

/**
 * Created by marcello on 7/2/18.
 */

public class Processo {
    public Object alunos;
    public boolean podeLiberar;
    public int status;

    public Processo(){

    }

    public Processo(Object alunos, boolean podeLiberar, int status) {
        this.alunos = alunos;
        this.podeLiberar = podeLiberar;
        this.status = status;
    }
}
