package com.example.marcello.tomadordefrequencia.model;

import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * Created by marcello on 6/10/18.
 */

public class Tomador implements Serializable {
    public String codigo;
    public String diasDaSemana;
    public String horarioDeInicioAula;
    public String nome;
    public String nomeProfessor;

    public Tomador(){

    }

    public Tomador(String codigo, String diasDaSemana, String horarioDeInicioAula, String nome, String nomeProfessor) {
        this.codigo = codigo;
        this.diasDaSemana = diasDaSemana;
        this.horarioDeInicioAula = horarioDeInicioAula;
        this.nome = nome;
        this.nomeProfessor = nomeProfessor;
    }
}
