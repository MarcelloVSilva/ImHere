package com.example.marcello.tomadordefrequencia.componentes.telas;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcello.tomadordefrequencia.R;
import com.example.marcello.tomadordefrequencia.componentes.telas.fragments.AindaNaoComecou;
import com.example.marcello.tomadordefrequencia.componentes.telas.fragments.EmProcessoAula;
import com.example.marcello.tomadordefrequencia.componentes.telas.fragments.FimDoProcesso;
import com.example.marcello.tomadordefrequencia.componentes.telas.fragments.IdentificacaoAluno;
import com.example.marcello.tomadordefrequencia.componentes.telas.fragments.SemAulasHojeParaDisciplina;
import com.example.marcello.tomadordefrequencia.model.Aluno;
import com.example.marcello.tomadordefrequencia.model.Aula;
import com.example.marcello.tomadordefrequencia.model.Matriculas;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcello on 6/14/18.
 */

public class ProximaDisciplina extends AppCompatActivity {
    private static final int RESULTADO_CODIGO_DISCIPLINA = 1;
    private DatabaseReference mDatabase;
    private String COD_DISCIPLINA_ATUAL;
    private String NOME_DISCIPLINA_ATUAL;
    private String NOME_PROFESSOR_DISCIPLINA_ATUAL;
    private String TOMADOR_ATUAL_EM_USO;
    private int ANO;
    private int MES;
    private int DIA;
    private String horaQueComecaProximaAula = new String();

    Aula aula = new Aula();
    private DialogInterface dialog;

    NfcAdapter nfcAdapter;
    Boolean podeLerNfcAgora;
    public int STATUS_ATUAL;
    private final int CHECKIN_AINDA_NAO_COMECOU = 00;
    private final int CHECKIN_EM_PROCESSO = 10;
    private final int CHECKIN_ENCERRADO = 20;
    private final int CHECKOUT_EM_PROCESSO = 21;
    private final int CHECKOUT_ENCERRADO  = 22;
    private final int SEM_AULA = 99;
    private String idDaProximaAula;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_layout);
        setSupportActionBar(toolbar);
        setContentView(R.layout.proxima_disciplina);
        podeLerNfcAgora = false;
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        TOMADOR_ATUAL_EM_USO = getIntent().getStringExtra("TOMADOR_ATUAL");

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

        ImageButton loginProfessorFragment = findViewById(R.id.loginProfessor);
        final Intent inputIntent = new Intent(this, InputEdit.class);

        loginProfessorFragment.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                inputIntent.putExtra("TYPE", "PASSWORD");
                inputIntent.putExtra("PARA_VERIFICAR", COD_DISCIPLINA_ATUAL);
                inputIntent.putExtra("HINT_INPUT", "Digite o código de acesso");
                startActivityForResult(inputIntent, RESULTADO_CODIGO_DISCIPLINA);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == RESULTADO_CODIGO_DISCIPLINA) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
//                if(data.getStringExtra("resposta").equals(COD_DISCIPLINA_ATUAL)){
                    Intent telaProfessor = new Intent(this, LiberaInOutProfessor.class);
                    telaProfessor.putExtra("CODIGO_DISCIPLINA", COD_DISCIPLINA_ATUAL);
                    telaProfessor.putExtra("STATUS_ATUAL", String.valueOf(STATUS_ATUAL));
                    telaProfessor.putExtra("CODIGO_TOMADOR_SALA", TOMADOR_ATUAL_EM_USO);
                    telaProfessor.putExtra("ID_AULA", idDaProximaAula);
                    startActivity(telaProfessor);
//                }
//                else

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog = ProgressDialog.show(this, "",
                "Carregando aula", true);

        TextView nomeDisciplina = findViewById(R.id.nomeDaDisciplina);
        TextView nomeProfessor = findViewById(R.id.nomeDoProfessor);
        nomeDisciplina.setText(NOME_DISCIPLINA_ATUAL);
        nomeProfessor.setText(NOME_PROFESSOR_DISCIPLINA_ATUAL);
        pegaAulasDaDisciplina();
    }

    private void pegaAulasDaDisciplina() {
        mDatabase.child("/disciplinas/" + COD_DISCIPLINA_ATUAL + "/aulas/" + ANO + "/" + MES + "/" + DIA).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Aula proximaAula = new Aula();
                        proximaAula = null;
                        int ultimaVez = 1;
                        Long contador = dataSnapshot.getChildrenCount();
                        long milisDaProximaAula = 0;
                        idDaProximaAula = new String();

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
                                controlaStatusDaAula();
                            }
                            contador--;
                        }
                        if(proximaAula == null){
                            STATUS_ATUAL = SEM_AULA;
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            Fragment semAula = new SemAulasHojeParaDisciplina();
                            ft.replace(R.id.espaçoParaColocarFragment, semAula);
                            ft.commitAllowingStateLoss();
                            dialog.cancel();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Log", "passou aqui");
                    }
                });

    }

    private void controlaStatusDaAula() {
        mDatabase.child("/disciplinas/"+COD_DISCIPLINA_ATUAL+"/aulas/"+ ANO+"/"+MES+"/"+DIA+"/"+idDaProximaAula).
                addValueEventListener(new ValueEventListener() {

                    Aula dsAula = new Aula();
                    Fragment emProcesso;

                    Fragment fimDoProcesso;
                    Fragment aindaNaoComecou;

                      @Override
                      public void onDataChange(DataSnapshot dataSnapshot) {
                          Bundle bundle = new Bundle();

                          FragmentTransaction ft = getFragmentManager().beginTransaction();
                          dsAula = dataSnapshot.getValue(Aula.class);

                          Object checkin = dsAula.checkin;
                          Object aux = ((HashMap) checkin).get("status");
                          int statusAulaCheckin = ((Long) aux).intValue();

                          Object checkout = dsAula.checkout;
                          Object aux2 = ((HashMap) checkout).get("status");
                          int statusAulaCheckout = ((Long) aux2).intValue();
                          switch (statusAulaCheckin) {
                              case 0:
                                  aindaNaoComecou = new AindaNaoComecou();
                                  ft.replace(R.id.espaçoParaColocarFragment, aindaNaoComecou, "aindaNaoComecou");
                                  STATUS_ATUAL = CHECKIN_AINDA_NAO_COMECOU;
                                  bundle.putString("checkinOuCheckout", "checkin");
                                  podeLerNfcAgora = false;
                                  aindaNaoComecou.setArguments(bundle);
                                  break;
                              case 1:
                                  emProcesso = new EmProcessoAula();
                                  ft.replace(R.id.espaçoParaColocarFragment, emProcesso, "emProcesso");
                                  STATUS_ATUAL = CHECKIN_EM_PROCESSO;
                                  bundle.putString("checkinOuCheckout", "checkin");
                                  podeLerNfcAgora = true;
                                  emProcesso.setArguments(bundle);
                                  break;
                              case 2:
                                  switch (statusAulaCheckout) {
                                      case 0:
                                          fimDoProcesso = new FimDoProcesso();
                                          ft.replace(R.id.espaçoParaColocarFragment, fimDoProcesso, "fimDoProcesso");
                                          STATUS_ATUAL = CHECKIN_ENCERRADO;
                                          bundle.putString("checkinOuCheckout", "checkin");
                                          podeLerNfcAgora = false;
                                          fimDoProcesso.setArguments(bundle);
//                                        break;
                                      case 1:
                                          emProcesso = new EmProcessoAula();
                                          ft.replace(R.id.espaçoParaColocarFragment, emProcesso, "emProcesso");
                                          STATUS_ATUAL = CHECKOUT_EM_PROCESSO;
                                          bundle.putString("checkinOuCheckout", "checkout");
                                          podeLerNfcAgora = true;
//                                        emProcesso.setArguments(bundle);
                                          break;
                                      case 2:
                                          fimDoProcesso = new FimDoProcesso();
                                          ft.replace(R.id.espaçoParaColocarFragment, fimDoProcesso, "fimDoProcesso");
                                          STATUS_ATUAL = CHECKIN_ENCERRADO;
                                          bundle.putString("checkinOuCheckout", "checkout");
                                          podeLerNfcAgora = false;
                                          fimDoProcesso.setArguments(bundle);
//                                        break;
                                  }
                                  break;
                          }
                          ft.addToBackStack(null);
                          ft.commitAllowingStateLoss();
                          dialog.cancel();
                      }


                      @Override
                      public void onCancelled(DatabaseError databaseError) {}
                                      });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        if(!podeLerNfcAgora) {
            Toast.makeText(this, "Não está na hora", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "Agora sim", Toast.LENGTH_LONG).show();
        try {
            getTagId(intent);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onNewIntent(intent);
    }

    private void getTagId(Intent intent) throws IOException, JSONException {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] idStudent = tag.getId();

        ArrayList nfc_id_array = new ArrayList();
        for(byte item: idStudent){
            nfc_id_array.add(item);
        }
        String nfc_id_aux = nfc_id_array.toString();
        String nfc_id = nfc_id_aux.replaceAll("[\\[\\] ]", "");
        verificarSeAlunoExisteNaDisciplinaNfcId(nfc_id);
    }

    private void verificarSeAlunoExisteNaDisciplinaNfcId(String nfc_id) {
        mDatabase.child("/disciplinas/"+COD_DISCIPLINA_ATUAL+"/alunos").addValueEventListener(new ValueEventListener() {
//            @Override
            Fragment identificacaoAluno;
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot aluno: dataSnapshot.getChildren()){
                    Aluno alunoDb = aluno.getValue(Aluno.class);
                    if(alunoDb.nfc_id != null){
                        if(alunoDb.nfc_id.equals(nfc_id)) {
                            String matriculaDoAluno = aluno.getKey();
                            Toast.makeText(getBaseContext(), ""+matriculaDoAluno, Toast.LENGTH_LONG).show();
                            verificaSeJaNaoFoiInserido(matriculaDoAluno);
                            break;
                        } else {
//                            FragmentTransaction ft = getFragmentManager().beginTransaction();
//                            identificacaoAluno = new IdentificacaoAluno();
//                            ft.replace(R.id.espaçoParaColocarFragment, identificacaoAluno);
//                            Bundle bundle = new Bundle();
//                            bundle.putString("IDENTIFICACAO", "Aluno(a) não encontrado(a)");
//                            identificacaoAluno.setArguments(bundle);
//                            ft.commitAllowingStateLoss();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void verificarSeAlunoExisteNaDisciplinaMatricula(String matricula) {
        mDatabase.child("/disciplinas/"+COD_DISCIPLINA_ATUAL+"/alunos/"+matricula).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    verificaSeJaNaoFoiInserido(matricula);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    private void verificaSeJaNaoFoiInserido(String matriculaDoAluno) {
        String qualProcesso = STATUS_ATUAL==CHECKIN_EM_PROCESSO?"checkin":"checkout";
        mDatabase.child("/disciplinas/" + COD_DISCIPLINA_ATUAL + "/aulas/" + ANO + "/" + MES + "/" + DIA + "/" + idDaProximaAula+"/"+qualProcesso+"/alunos").
            addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long count = dataSnapshot.getChildrenCount();
                    boolean jaFoiInserido = false;
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        Object matricula = ds.getValue();
                        if(String.valueOf(matricula).equals(matriculaDoAluno)){
                            jaFoiInserido = true;
                        }
                    }
                    if(!jaFoiInserido)
                        registrarPresencaNesteProcessoParaAluno(matriculaDoAluno);
                    else Toast.makeText(getBaseContext(), "Você já realizou "+qualProcesso, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
        });
    }

    private void registrarPresencaNesteProcessoParaAluno(String matriculaDoAluno) {
        DatabaseReference aulaProcesso = mDatabase.child("/disciplinas/" + COD_DISCIPLINA_ATUAL + "/aulas/" + ANO + "/" + MES + "/" + DIA + "/" + idDaProximaAula);
        DatabaseReference listaAlunos = null;
        String idxAlunoPresente;
        switch (STATUS_ATUAL) {
            case CHECKIN_EM_PROCESSO:
                listaAlunos = aulaProcesso.child("/checkin/alunos").getRef();
                break;
            case CHECKOUT_EM_PROCESSO:
                listaAlunos = aulaProcesso.child("/checkout/alunos").getRef();
                break;
        }
        idxAlunoPresente = listaAlunos.push().getKey();
        Map<String, Object> mapAluno = new HashMap<>();
        mapAluno.put(idxAlunoPresente, matriculaDoAluno);
        listaAlunos.updateChildren(mapAluno);

    }

    @Override
    protected void onResume(){

        Intent intent = new Intent(this, ProximaDisciplina.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, intent, 0);
        IntentFilter[] intentFilter = new IntentFilter[] {};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);
        super.onResume();
    }

    @Override
    protected void onPause(){
        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }

}
