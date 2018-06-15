package com.example.marcello.tomadordefrequencia.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.marcello.tomadordefrequencia.R;
import com.example.marcello.tomadordefrequencia.model.Aula;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.marcello.tomadordefrequencia.model.Tomador;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String tomadorEmUso;
    Tomador disciplinas = new Tomador();
    public static JSONObject db;
    Object db1;


    Subject<Tomador> mObservable = PublishSubject.create();
    ArrayList<Tomador> arrDisciplinas = new ArrayList();
    private JSONObject fakeDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tomadorEmUso = "cac209";

        mDatabase = FirebaseDatabase.getInstance().getReference();

//        try {
//            fakeDb = new JSONObject(loadJSONFromAsset());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//        public String loadJSONFromAsset() {
//            String json = null;
//            try {
//                InputStream is = this.getAssets().open("demo.json");
//                int size = is.available();
//                byte[] buffer = new byte[size];
//                is.read(buffer);
//                is.close();
//                json = new String(buffer, "UTF-8");
//            } catch (IOException ex) {
//                ex.printStackTrace();
//                return null;
//            }
//            return json;
        }


    @Override
    public void onStart() {
        super.onStart();
        pegaDisciplinas();








        // pega as disciplinas do tomador
//        try {
//            Object tomadores = fakeDb.get("tomadores");
//            tomador = tomadores.get(tomadorEmUso);
//            disciplinas = (Tomador) tomador.get("disciplinas");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        // verifica e seleciona qual disciplinas é de hoje
            // se nao tem mostra tela que nao tem disciplinas hoje

        // pega aulas
            // se nao tem mostra tela que nao tem aulas pra hoje

        // mostra a proxima aula do dia para esse tomador

        // checkin/checkout: emProcesso
            // lerCartao/inserirMatricula
                // ao identificar aluno: identificarAlunoNegativo/identificarAlunoPositivo

        // acabou - checkin/checkout: fimDoProcesso
            // ao fim do checkout mostra a proxima aula ou que não mais aulas hoje

        // professor clicou para entrar na disciplinas:
            // inserirCodDisciplina

        // mostrar informaçoes disciplinas:

        // liberar checkin pelo tablet:

        // liberar checkou pelo tablet:

        }

    private void pegaDisciplinas() {

        mDatabase.child("tomadores/"+tomadorEmUso+"/disciplinas").
                addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            disciplinas = ds.getValue(Tomador.class);
                            arrDisciplinas.add(disciplinas);
                            pegaAulasDaDisciplina();

                            Log.d("disciplinas", disciplinas.nome);
                            Log.d("disciplinas", disciplinas.diasDaSemana);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Log", "passou aqui");
                    }
                });
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

    public String dataHoje(String hora){
            String momentoAtual = new Date().toString().replace(" ", "");
            return momentoAtual; // assim nao vai dar certo. Arrumar forma de setar hora no new Date
        }

    }








    /*
    * mDatabase = FirebaseDatabase.getInstance().getReference();
        ValueEventListener pegaDisciplinas = mDatabase.child("tomadores").child(tomadorEmUso).child("disciplinas").
        addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    disciplinas = ds.getValue(Tomador.class);
                    arrDisciplinas.add(disciplinas);

                    Log.d("disciplinas", disciplinas.nome);
                    Log.d("disciplinas", disciplinas.diasDaSemana);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Log", "passou aqui");
            }
        });


        * Quando a aplicao iniciar no tablet tem haver algum tipo de: fakeLoginTomador("cac209")
        * Onde ao iniciar meio que o tablet diz: Olá, eu sou o tomador de id cac209.
        *


    */