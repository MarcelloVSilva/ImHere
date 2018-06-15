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
    Tomador disciplina = new Tomador();
    public static JSONObject db;
    Object db1;


    Subject<Tomador> mObservable = PublishSubject.create();
    ArrayList arrDisciplinas = new ArrayList();
    private JSONObject fakeDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tomadorEmUso = "cac209";
        try {
            fakeDb = new JSONObject(loadJSONFromAsset());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
        public String loadJSONFromAsset() {
            String json = null;
            try {
                InputStream is = this.getAssets().open("demo.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            return json;
        }


    @Override
    public void onStart() {
        super.onStart();

        // pega as disciplinas do tomador

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

        public String dataHoje(String hora){
            String momentoAtual = new Date().toString().replace(" ", "");
            return momentoAtual; // assim nao vai dar certo. Arrumar forma de setar hora no new Date
        }

    }








    /*
    * mDatabase = FirebaseDatabase.getInstance().getReference();
        ValueEventListener pegaDisciplinas = mDatabase.child("tomadores").child(tomadorEmUso).child("disciplina").
        addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    disciplina = ds.getValue(Tomador.class);
                    arrDisciplinas.add(disciplina);

                    Log.d("disciplina", disciplina.nome);
                    Log.d("disciplina", disciplina.diasDaSemana);
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