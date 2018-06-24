package com.example.marcello.tomadordefrequencia.componentes.telas;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.marcello.tomadordefrequencia.R;
import com.example.marcello.tomadordefrequencia.model.Aula;
import com.example.marcello.tomadordefrequencia.model.Disciplina;
import com.example.marcello.tomadordefrequencia.model.Sala;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class LiberaInOutProfessor extends AppCompatActivity {

    private String codDisciplina;
    private DatabaseReference mDatabase;
    private TextView nomeDisciplina;
    private TextView nomeProfessor;
    private TextView detalhes1;
    private TextView detalhes2;
    private TextView detalhes3;
    private TextView detalhes4;
    private TextView checkinOuCheckout;
    private String codSala;
    private int ANO;
    private int MES;
    private int DIA;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liberar_processo_professor);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        codDisciplina = getIntent().getStringExtra("CODIGO_DISCIPLINA");
        codSala= getIntent().getStringExtra("CODIGO_TOMADOR_SALA");

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        ANO = cal.get(Calendar.YEAR);
        MES = cal.get(Calendar.MONTH);
        DIA = cal.get(Calendar.DAY_OF_MONTH);

        nomeDisciplina = findViewById(R.id.nomeDaDisciplina);
        nomeProfessor = findViewById(R.id.nomeDoProfessor);
        detalhes1= findViewById(R.id.detalhesDisciplina1);
        detalhes2= findViewById(R.id.detalhesDisciplina2);
        detalhes3= findViewById(R.id.detalhesDisciplina3);
        detalhes4= findViewById(R.id.detalhesDisciplina4);
        checkinOuCheckout= findViewById(R.id.titulo_card_processo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.child("/disciplinas/"+codDisciplina).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Disciplina disciplina = dataSnapshot.getValue(Disciplina.class);
                nomeDisciplina.setText(disciplina.nome);
                nomeProfessor.setText(disciplina.nomeProfessor);
                detalhes4.setText("Quantidade de alunos: "+String.valueOf(((ArrayList) disciplina.alunos).size()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("salas/"+codSala).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Sala sala = dataSnapshot.getValue(Sala.class);
                detalhes1.setText(sala.local);
                detalhes2.setText("Sala "+ sala.numero);
                detalhes3.setText(sala.andar+"° Andar");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("/disciplinas/" + codDisciplina + "/aulas/" + ANO + "/" + MES + "/" + "22").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Aula aula = new Aula();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            aula = ds.getValue(Aula.class);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    //ligar com banco
    //verificar se tem aula pra hoje
    //se tiver só libera ou fecha processo
    //caso nao tenha aula, criar aula pra hoje, ja com check-in status 1.
}


