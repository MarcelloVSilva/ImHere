package com.example.marcello.tomadordefrequencia.view;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.marcello.tomadordefrequencia.componentes.telas.SemDisciplinasParaHoje;

    public class MainActivity extends ListActivity {

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
                            if(!dataSnapshot.hasChildren())
                                abreIntentSemResultado();
                            else for (DataSnapshot ds: dataSnapshot.getChildren()){
                                Tomador disciplina = ds.getValue(Tomador.class);
                                listaDeDisciplinas.add(disciplina.codigo+"-"+disciplina.nome);
                                arrayDisciplinasAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

//        private void pegaDisciplinas() {
//
//        mDatabase.child("tomadores/"+tomadorEmUso+"/disciplinas").
//                addValueEventListener(new ValueEventListener() {
//
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Tomador proximaDisciplina;
//                        arrDisciplinas.clear();
//                        int ultimaVez = 1;
//                        Long contador = dataSnapshot.getChildrenCount();
//                        for (DataSnapshot ds: dataSnapshot.getChildren()){
//                            disciplina = ds.getValue(Tomador.class);
//                            if(verificaSeDisciplinaTemAulaHoje(disciplina)) {
//                                arrDisciplinas.add(disciplina);
//                            }
//                            if(contador == ultimaVez) {
//                                if (!arrDisciplinas.isEmpty()) {
//                                    intentDaProximaDisciplina();
//                                } else abreIntentSemResultado();
//                            }
//                            contador--;
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.d("Log", "passou aqui");
//                    }
//                });
//    }
//
//        private void intentDaProximaDisciplina() {
//            Calendar horarioDisciplina = Calendar.getInstance();
//
//            Tomador proximaDisciplina = new Tomador();
//            long aulaMaisProxima = 0;
//            String horarioDeInicioAula= null;
//
//            for (Tomador disciplina: arrDisciplinas) {
//                try {
//                    if(disciplina.horarioDeInicioAula instanceof ArrayList)
//                        horarioDeInicioAula = (String) ((ArrayList) disciplina.horarioDeInicioAula).get((Integer) DIASDASEMANA.get(diaDaSemanaHoje));
//                    else if(disciplina.horarioDeInicioAula instanceof HashMap)
//                        horarioDeInicioAula = (String) ((HashMap) disciplina.horarioDeInicioAula).get(DIASDASEMANA.get(diaDaSemanaHoje).toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                int horas = Integer.parseInt(horarioDeInicioAula.substring(0,2));
//                int minutos = Integer.parseInt(horarioDeInicioAula.substring(3,5));
//
//                horarioDisciplina.set(Calendar.HOUR_OF_DAY, horas);
//                horarioDisciplina.set(Calendar.MINUTE, minutos);
//
//                long horarioDisciplinaEmMili = horarioDisciplina.getTimeInMillis();
//
//                if(horarioDisciplinaEmMili > diaHoraAtual.getTime() && (horarioDisciplinaEmMili < aulaMaisProxima || aulaMaisProxima ==0)){
//                    proximaDisciplina = disciplina;
//                    aulaMaisProxima = horarioDisciplinaEmMili;
//                }
//            }
//
//            if(proximaDisciplina.codigo != null) {
//                Intent intent = new Intent(this, ProximaDisciplina.class);
//                intent.putExtra("DISCIPLINA_CODIGO", proximaDisciplina.codigo);
//                intent.putExtra("DISCIPLINA_NOME", proximaDisciplina.nome);
//                intent.putExtra("DISCIPLINA_NOME_PROFESSOR", proximaDisciplina.nomeProfessor);
//                intent.putExtra("TOMADOR_ATUAL", tomadorEmUso);
//                startActivity(intent);
//                finish();
//            } else {
//                Intent intent = new Intent(this, NaoTemMaisDisciplinaHoje.class);
//                startActivity(intent);
//                finish();
//            }
//        }
//
//        private boolean verificaSeDisciplinaTemAulaHoje(Tomador disciplinaAtual) {
//        for (Object dia: ((ArrayList) disciplinaAtual.diasDaSemana)){
//            try {
//                if(dia.toString().equals(DIASDASEMANA.getString(diaDaSemanaHoje))){
//                    return true;
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//        return false;
//    }

    private void abreIntentSemResultado(){
        Intent intent = new Intent(this, SemDisciplinasParaHoje.class);
        startActivity(intent);
        finish();
    }



}