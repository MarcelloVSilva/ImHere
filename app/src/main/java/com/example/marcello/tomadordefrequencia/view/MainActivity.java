package com.example.marcello.tomadordefrequencia.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.marcello.tomadordefrequencia.componentes.telas.NaoTemMaisDisciplinaHoje;
import com.example.marcello.tomadordefrequencia.componentes.telas.ProximaDisciplina;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.marcello.tomadordefrequencia.model.Tomador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.marcello.tomadordefrequencia.componentes.telas.SemDisciplinasParaHoje;

    public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String tomadorEmUso;
    Tomador disciplina = new Tomador();
    public static JSONObject db;
    Object db1;

    ArrayList<Tomador> arrDisciplinas = new ArrayList();
    private JSONObject fakeDb;
    private String diaDaSemanaHoje;

    private static final int segunda = 2;
    private static final int terça= 3;
    private static final int quarta = 4;
    private static final long quinta = 5;
    private static final int sexta = 6;
    private static final int sábado = 7;
    private static final int domingo = 8;


    private static final String _DIASDASEMANA = "{" +
                "segunda-feira:"+ segunda+ "," +
                "terça-feira:"+ terça+ "," +
                "quarta-feira:"+ quarta+ "," +
                "quinta-feira:"+ quinta+ "," +
                "sexta-feira:"+ sexta+ "," +
                "sábado:"+ sábado+ "," +
                "domingo:"+ domingo +
            "}";
    private JSONObject DIASDASEMANA;
        private Date diaHoraAtual;


        @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        tomadorEmUso = "cac209";

        SimpleDateFormat date = new SimpleDateFormat("EEEE");
//        diaHoraAtual = new Date(2018, 05, 13);
        diaHoraAtual = new Date();
        diaDaSemanaHoje = date.format(diaHoraAtual);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        try {
            DIASDASEMANA = new JSONObject(_DIASDASEMANA);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        pegaDisciplinas();

        ProgressDialog dialog = ProgressDialog.show(this, "",
                "Carregando disciplinas...", true);

        // verifica e seleciona qual disciplina é de hoje
            // se nao tem mostra tela que nao tem disciplina hoje

        // pega aulas
            // se nao tem mostra tela que nao tem aulas pra hoje

        // mostra a proxima aula do dia para esse tomador

        // checkin/checkout: emProcesso
            // lerCartao/inserirMatricula
                // ao identificar aluno: identificarAlunoNegativo/identificarAlunoPositivo

        // acabou - checkin/checkout: fimDoProcesso
            // ao fim do checkout mostra a proxima aula ou que não mais aulas hoje

        // professor clicou para entrar na disciplina:
            // inserirCodDisciplina

        // mostrar informaçoes disciplina:

        // liberar checkin pelo tablet:

        // liberar checkou pelo tablet:

        }

    private void pegaDisciplinas() {

        mDatabase.child("tomadores/"+tomadorEmUso+"/disciplinas").
                addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Tomador proximaDisciplina;
                        arrDisciplinas.clear();
                        int ultimaVez = 1;
                        Long contador = dataSnapshot.getChildrenCount();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            disciplina = ds.getValue(Tomador.class);
                            if(verificaSeDisciplinaTemAulaHoje(disciplina)) {
                                arrDisciplinas.add(disciplina);
                            }else if(contador == ultimaVez) {
                                if (!arrDisciplinas.isEmpty()) {
                                    intentDaProximaDisciplina();
                                } else abreIntentSemResultado();
                            }
                            contador--;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Log", "passou aqui");
                    }
                });
    }

        private void intentDaProximaDisciplina() {
            Calendar horarioDisciplina = Calendar.getInstance();

            Tomador proximaDisciplina = new Tomador();
            long aulaMaisProxima = 0;
            String horarioDeInicioAula= null;

            for (Tomador disciplina: arrDisciplinas) {
                try {
                    horarioDeInicioAula = (String) ((HashMap) disciplina.horarioDeInicioAula).get(DIASDASEMANA.get(diaDaSemanaHoje).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                int horas = Integer.parseInt(horarioDeInicioAula.substring(0,2));
                int minutos = Integer.parseInt(horarioDeInicioAula.substring(3,5));

                horarioDisciplina.set(Calendar.HOUR, horas);
                horarioDisciplina.set(Calendar.MINUTE, minutos);

                long horarioDisciplinaEmMili = horarioDisciplina.getTimeInMillis();

                if(horarioDisciplinaEmMili > diaHoraAtual.getTime() && (horarioDisciplinaEmMili < aulaMaisProxima || aulaMaisProxima ==0)){
                    proximaDisciplina = disciplina;
                    aulaMaisProxima = horarioDisciplinaEmMili;
                }
//                if(arrDisciplinas.getTime() - )
            }
            if(proximaDisciplina.codigo != null) {
                Intent intent = new Intent(this, ProximaDisciplina.class);
                intent.putExtra("DISCIPLINA_CODIGO", proximaDisciplina.codigo);
                intent.putExtra("DISCIPLINA_NOME", proximaDisciplina.nome);
                intent.putExtra("DISCIPLINA_NOME_PROFESSOR", proximaDisciplina.nomeProfessor);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, NaoTemMaisDisciplinaHoje.class);
                startActivity(intent);
                finish();
            }
        }

        private boolean verificaSeDisciplinaTemAulaHoje(Tomador disciplinaAtual) {
        for (Object dia: ((ArrayList) disciplinaAtual.diasDaSemana)){
            try {
                if(dia.toString().equals(DIASDASEMANA.getString(diaDaSemanaHoje))){
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    private void abreIntentSemResultado(){
        Intent intent = new Intent(this, SemDisciplinasParaHoje.class);
        startActivity(intent);
        finish();
    }
}