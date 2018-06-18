package com.example.marcello.tomadordefrequencia.componentes.telas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.marcello.tomadordefrequencia.R;
import com.example.marcello.tomadordefrequencia.model.Tomador;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by marcello on 6/14/18.
 */

public class ProximaDisciplina extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String COD_DISCIPLINA_ATUAL;
    private String NOME_DISCIPLINA_ATUAL;
    private String NOME_PROFESSOR_DISCIPLINA_ATUAL;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proxima_disciplina);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        COD_DISCIPLINA_ATUAL = getIntent().getStringExtra("DISCIPLINA_CODIGO");
        NOME_DISCIPLINA_ATUAL = getIntent().getStringExtra("DISCIPLINA_NOME");
        NOME_PROFESSOR_DISCIPLINA_ATUAL = getIntent().getStringExtra("DISCIPLINA_NOME_PROFESSOR");

    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView nomeDisciplina = findViewById(R.id.nomeDaDisciplina);
        TextView nomeProfessor = findViewById(R.id.nomeDoProfessor);
        nomeDisciplina.setText(NOME_DISCIPLINA_ATUAL);
        nomeProfessor.setText(NOME_PROFESSOR_DISCIPLINA_ATUAL);
        pegaAulasDaDisciplina();
    }

    private void pegaAulasDaDisciplina() {
        mDatabase.child("/disciplinas/"+COD_DISCIPLINA_ATUAL).
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
