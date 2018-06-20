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
                    Fragment emProcesso = new EmProcessoAula();

                    Fragment fimDoProcesso = new FimDoProcesso();
                    Fragment aindaNaoComecou = new AindaNaoComecou();

                      @Override
                      public void onDataChange(DataSnapshot dataSnapshot) {
                          Bundle bundle = new Bundle();

                          emProcesso.setArguments(bundle);
                          fimDoProcesso.setArguments(bundle);

                          FragmentTransaction ft = getFragmentManager().beginTransaction();
                          dsAula = dataSnapshot.getValue(Aula.class);

                          Object checkin = dsAula.checkin;
                          Object aux = ((HashMap) checkin).get("status");
                          int statusAulaCheckin = ((Long) aux).intValue();

                          Object checkout = dsAula.checkout;
                          Object aux2 = ((HashMap) checkout).get("status");
                          int statusAulaCheckout = ((Long) aux2).intValue();

                          switch(statusAulaCheckin) {
                              case 0: //ainda nao comecou
                                  bundle.putString("checkinOuCheckout", "checkin");
                                  aindaNaoComecou.setArguments(bundle);
                                  ft.replace(R.id.espaçoParaColocarFragment, aindaNaoComecou);
                                  break;
                              case 1: //em andamento
                                  bundle.putString("checkinOuCheckout", "checkin");
                                  emProcesso.setArguments(bundle);
                                  ft.replace(R.id.espaçoParaColocarFragment, emProcesso);
                                  break;
                              case 2: //encerrado
                                  switch (statusAulaCheckout){
                                      case 0:
                                          bundle.putString("checkinOuCheckout", "checkin");
                                          fimDoProcesso.setArguments(bundle);
                                          ft.replace(R.id.espaçoParaColocarFragment, fimDoProcesso);
                                          break;
                                      case 1:
                                          bundle.putString("checkinOuCheckout", "checkout");
                                          emProcesso.setArguments(bundle);
                                          ft.replace(R.id.espaçoParaColocarFragment, emProcesso);
                                          break;
                                      case 2:
                                          bundle.putString("checkinOuCheckout", "checkout");
                                          fimDoProcesso.setArguments(bundle);
                                          ft.replace(R.id.espaçoParaColocarFragment, fimDoProcesso);
                                          break;
                                  }
                                  break;
                          }
                          ft.addToBackStack(null);
                          ft.commitAllowingStateLoss();
                      }


                      @Override
                      public void onCancelled(DatabaseError databaseError) {
                          Log.d("Log", "passou aqui");
                      }
                                      });
//        ligacao com o banco, fica observando os objetos checkin e checkout da aula
    }
}
