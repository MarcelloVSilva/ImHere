package com.example.marcello.tomadordefrequencia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.example.marcello.tomadordefrequencia.model.Tomador;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String tomadorEmUso;

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
        Query tomador = mDatabase.child("tomadores").equalTo(tomadorEmUso);
        tomador.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Array disciplinas = dataSnapshot.getValue(Tomador.class).getDisciplinas();
                Log.d("onDataChange", "passou aqui");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        }

    }
