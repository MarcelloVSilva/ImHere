package com.example.marcello.tomadordefrequencia.view;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcello.tomadordefrequencia.R;
import com.example.marcello.tomadordefrequencia.componentes.telas.ProximaDisciplina;
import com.example.marcello.tomadordefrequencia.model.Sala;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.marcello.tomadordefrequencia.componentes.telas.SemDisciplinasParaHoje;

    public class MainActivity extends ListActivity {


    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();

    private DatabaseReference mDatabase;
    private String tomadorEmUso;
    Tomador disciplina = new Tomador();

    ArrayList<Tomador> arrDisciplinas = new ArrayList();
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
    private DatabaseReference referenciaDeDisciplinasDoTomadorSincronaComFb;
    private View listViewTela;
    private List<String> listaDeDisciplinas;
    private ArrayAdapter<String> arrayDisciplinasAdapter;
    private DatabaseReference referenciaDaSalaDoTomadorSincronaComFb;
    private TextView campoLocalDaSala;
    private TextView campoAndarSala;
    private TextView campoNumeroDaSala;


        @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        tomadorEmUso = "cac209";

        campoLocalDaSala = findViewById(R.id.localQueSeEncontraSala);
        campoAndarSala = findViewById(R.id.andarDaSala);
        campoNumeroDaSala = findViewById(R.id.numeroDaSala);

        listViewTela = findViewById(android.R.id.list);
        listaDeDisciplinas = new ArrayList<String>();
        arrayDisciplinasAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaDeDisciplinas);
        setListAdapter(arrayDisciplinasAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String codigo_e_disciplina = String.valueOf(((TextView) view).getText());
                    String codigoDisciplina = codigo_e_disciplina.split("[\\-]")[0];
                    String nomeDisciplina = codigo_e_disciplina.split("[\\-]")[1];
                    Intent intent = new Intent(getBaseContext(), ProximaDisciplina.class);
                    intent.putExtra("DISCIPLINA_CODIGO", codigoDisciplina);
                    intent.putExtra("DISCIPLINA_NOME", nomeDisciplina);
                    intent.putExtra("DISCIPLINA_NOME_PROFESSOR", "");
                    intent.putExtra("TOMADOR_ATUAL", tomadorEmUso);
                    startActivity(intent);

                }
            });



        SimpleDateFormat date = new SimpleDateFormat("EEEE");
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
        referenciaDeDisciplinasDoTomadorSincronaComFb = mDatabase.child("tomadores/"+tomadorEmUso+"/disciplinas").getRef();
        referenciaDeDisciplinasDoTomadorSincronaComFb.keepSynced(true);

        referenciaDeDisciplinasDoTomadorSincronaComFb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        referenciaDaSalaDoTomadorSincronaComFb = mDatabase.child("salas/"+tomadorEmUso).getRef();
        referenciaDaSalaDoTomadorSincronaComFb.keepSynced(true);

        referenciaDaSalaDoTomadorSincronaComFb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        montaListaDeDisciplinas();
        buscaSala();


        }

        private void buscaSala() {
            referenciaDaSalaDoTomadorSincronaComFb
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        Sala sala = dataSnapshot.getValue(Sala.class);
                        campoLocalDaSala.setText(sala.local);
                        campoNumeroDaSala.setText("Sala "+sala.numero.toString());
                        campoAndarSala.setText(sala.andar.toString()+"° Andar");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        private void montaListaDeDisciplinas() {
            listaDeDisciplinas.clear();
            referenciaDeDisciplinasDoTomadorSincronaComFb
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChildren())
                                abreIntentSemResultado();
                            else for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Tomador disciplina = ds.getValue(Tomador.class);
                                listaDeDisciplinas.add(disciplina.codigo + "-" + disciplina.nome);
                                arrayDisciplinasAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

    private void abreIntentSemResultado(){
        Intent intent = new Intent(this, SemDisciplinasParaHoje.class);
        startActivity(intent);
        finish();
    }





}