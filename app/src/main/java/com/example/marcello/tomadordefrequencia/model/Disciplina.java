package com.example.marcello.tomadordefrequencia.model;

import java.util.ArrayList;

/**
 * Created by marcello on 6/15/18.
 */

public class Disciplina {
    public Object diasDaSemana;
    public Object horarioDeInicioAula;
    public String nomeProfessor;
    public String nome;
    public Object alunos;
    public Object aulas;

    public Disciplina(ArrayList diasDaSemana, ArrayList horarioDeInicioAula, String nomeProfessor, String nome, ArrayList alunos, Object aulas) {
        this.diasDaSemana = diasDaSemana;
        this.horarioDeInicioAula = horarioDeInicioAula;
        this.nomeProfessor = nomeProfessor;
        this.nome = nome;
        this.alunos = alunos;
        this.aulas = aulas;
    }

    public Disciplina() {

    }
}
