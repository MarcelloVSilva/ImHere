package com.example.marcello.tomadordefrequencia.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.marcello.tomadordefrequencia.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.marcello.tomadordefrequencia.model.Tomador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String tomadorEmUso;
    Tomador disciplina = new Tomador();
    public static JSONObject db;
    Object db1;


    Subject<Tomador> mObservable = PublishSubject.create();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tomadorEmUso = "cac209";

        SimpleDateFormat date = new SimpleDateFormat("EEEE");
//        Date d = new Date(2018, 05, 13);
        Date d = new Date();
        diaDaSemanaHoje = date.format(d);

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

                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            disciplina = ds.getValue(Tomador.class);
                            if(verificaSeDisciplinaTemAulaHoje(disciplina)) {
                                arrDisciplinas.add(disciplina);
                                pegaAulasDaDisciplina();
                            }else{
                                TextView t = findViewById(R.id.readDataFromFirebase);
                                t.setText("Não tem disciplinas pra hoje");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Log", "passou aqui");
                    }
                });
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

    private void pegaAulasDaDisciplina() {
        for(Tomador disciplina : arrDisciplinas) {
            mDatabase.child("/disciplinas/"+disciplina.codigo).
                    addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("Log", "passou");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("Log", "passou aqui");
                        }
                    });
        }
    }
}