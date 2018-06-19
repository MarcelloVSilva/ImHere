package com.example.marcello.tomadordefrequencia.componentes.telas;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by marcello on 6/14/18.
 */

public class ProximaDisciplina extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String COD_DISCIPLINA_ATUAL;
    private String NOME_DISCIPLINA_ATUAL;
    private String NOME_PROFESSOR_DISCIPLINA_ATUAL;
    private int ANO;
    private int MES;
    private int DIA;
    private String horaQueComecaProximaAula = new String();

    Aula aula = new Aula();



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proxima_disciplina);

//        Date date = new Date(2018, 04, 03);
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        ANO = cal.get(Calendar.YEAR);
        MES = cal.get(Calendar.MONTH);
        DIA = cal.get(Calendar.DAY_OF_MONTH);

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
        mDatabase.child("/disciplinas/" + COD_DISCIPLINA_ATUAL + "/aulas/" + ANO + "/" + MES + "/" + DIA).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Aula proximaAula = new Aula();
                        proximaAula = null;
                        int ultimaVez = 1;
                        Long contador = dataSnapshot.getChildrenCount();
                        long milisDaProximaAula = 0;
                        String idDaProximaAula = new String();

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            aula = ds.getValue(Aula.class);

                            Calendar horarioDisciplina = Calendar.getInstance();
                            String horarioDeInicioAula = (String) ((HashMap) aula.hora).get("fim");
                            int horas = Integer.parseInt(horarioDeInicioAula.substring(0, 2));
                            int minutos = Integer.parseInt(horarioDeInicioAula.substring(3, 5));

                            horarioDisciplina.set(Calendar.HOUR_OF_DAY, horas);
                            horarioDisciplina.set(Calendar.MINUTE, minutos);

                            long horarioDisciplinaEmMili = horarioDisciplina.getTimeInMillis();

                            if (proximaAula == null || (horarioDisciplinaEmMili < milisDaProximaAula && milisDaProximaAula > 0)) {
                                proximaAula = aula;
                                idDaProximaAula = ds.getKey();
                                horaQueComecaProximaAula = (String) ((HashMap) aula.hora).get("inicio");
                                milisDaProximaAula = horarioDisciplinaEmMili;
                            }
                            if (contador == ultimaVez && proximaAula != null) {
                                TextView hora = findViewById(R.id.horaProximaAula);
                                hora.setText(horaQueComecaProximaAula);
                                controlaStatusDaAula(idDaProximaAula);
                            }
                            contador--;
                        }
                        //mostrar que nao tem aulas cadastradas para hoje
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Log", "passou aqui");
                    }
                });

    }

    private void controlaStatusDaAula(String aula) {
        mDatabase.child("/disciplinas/"+COD_DISCIPLINA_ATUAL+"/aulas/"+ ANO+"/"+MES+"/"+DIA+"/"+aula).
                addValueEventListener(new ValueEventListener() {

                    Aula dsAula = new Aula();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment emProcesso = new EmProcessoAula();
                    TextView statusAulaField = findViewById(R.id.statusAula);

                      @Override
                      public void onDataChange(DataSnapshot dataSnapshot) {
                          dsAula = dataSnapshot.getValue(Aula.class);
                          Object checkin = dsAula.checkin;
                          Object aux = ((HashMap) checkin).get("status");
                          int statusAula = ((Long) aux).intValue();
                          switch(statusAula) {
                              case 0:
                                  //ainda nao comecou
                                  ft.remove(emProcesso);
//                                  statusAulaField.setText("Checkin ainda não começou");
                                  break;
                              case 1:
                                  //em andamento
                                  ft.add(android.R.id.content, emProcesso).commit();
                                  break;
                              case 2:
                                  ft.remove(emProcesso);
//                                  statusAulaField.setText("Checkin já terminou");
                                  break;
                                  //encerrado
                              default: ft.remove(emProcesso);
                          }
                      }


                      @Override
                      public void onCancelled(DatabaseError databaseError) {
                          Log.d("Log", "passou aqui");
                      }
                                      });
//        ligacao com o banco, fica observando os objetos checkin e checkout da aula
    }
}
