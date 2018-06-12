package com.example.marcello.tomadordefrequencia.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.marcello.tomadordefrequencia.R;
import com.example.marcello.tomadordefrequencia.model.Aula;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.example.marcello.tomadordefrequencia.model.Tomador;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String tomadorEmUso;
    Tomador disciplinas = new Tomador();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        * Quando a aplicao iniciar no tablet tem haver algum tipo de: fakeLoginTomador("cac209")
        * Onde ao iniciar meio que o tablet diz: Olá, eu sou o tomador de id cac209.
        * */
        tomadorEmUso = "cac209";

    }


    @Override
    public void onStart() {
        super.onStart();

        mDatabase = FirebaseDatabase.getInstance().getReference();
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true); //save offline
        mDatabase.child("tomadores").child(tomadorEmUso).child("disciplinas").
                addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    disciplinas = ds.getValue(Tomador.class);

                    Log.d("disciplina", disciplinas.nome);
                    Log.d("disciplina", disciplinas.diasDaSemana);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Log", "passou aqui");
            }
        });

        mDatabase.child("disciplinas").child("FIC0046"/*Aqui virá disciplinas.codigo */).child("aulas").
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Aula aulaAtual = dataSnapshot.child(dataHoje(disciplinas.horarioDeInicioAula)).getValue(Aula.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Log", "passou aqui");
            }
        });

        }

        public String dataHoje(String hora){
            String momentoAtual = new Date().toString().replace(" ", "");
            return momentoAtual; // assim nao vai dar certo. Arrumar forma de setar hora no new Date
        }

    }
