package com.example.marcello.tomadordefrequencia.componentes.telas;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.marcello.tomadordefrequencia.R;
import com.example.marcello.tomadordefrequencia.componentes.telas.fragments.AindaNaoComecou;
import com.example.marcello.tomadordefrequencia.componentes.telas.fragments.EmProcessoAula;
import com.example.marcello.tomadordefrequencia.componentes.telas.fragments.FimDoProcesso;
import com.example.marcello.tomadordefrequencia.componentes.telas.fragments.SemAulasHojeParaDisciplina;
import com.example.marcello.tomadordefrequencia.model.Aula;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ProximaDisciplina extends AppCompatActivity {
    private static final int RESULTADO_CODIGO_DISCIPLINA = 1;
    private DatabaseReference mDatabase;
    public String COD_DISCIPLINA_ATUAL;
    public String NOME_DISCIPLINA_ATUAL;
    public String NOME_PROFESSOR_DISCIPLINA_ATUAL;
    public String TOMADOR_ATUAL_EM_USO;
    public int ANO;
    public int MES;
    public int DIA;
    private String horaQueComecaProximaAula = new String();

    Aula aula = new Aula();
    private DialogInterface dialog;

    NfcAdapter nfcAdapter;
    Boolean podeLerNfcAgora;
    public int STATUS_ATUAL;
    public final int CHECKIN_AINDA_NAO_COMECOU = 00;
    public final int CHECKIN_EM_PROCESSO = 10;
    public final int CHECKIN_ENCERRADO = 20;
    public final int CHECKOUT_EM_PROCESSO = 21;
    public final int CHECKOUT_ENCERRADO  = 22;
    public final int SEM_AULA = 99;
    public String idDaProximaAula;
    private DatabaseReference referenciaDeAulasDaDisciplinasSincronaComFb;
    private View campoTextoHorarioDaAula;
    private View campoHorarioDaAula;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_layout);
        setSupportActionBar(toolbar);
        setContentView(R.layout.proxima_disciplina);
        podeLerNfcAgora = false;
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        TOMADOR_ATUAL_EM_USO = getIntent().getStringExtra("TOMADOR_ATUAL");

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        ANO = cal.get(Calendar.YEAR);
        MES = cal.get(Calendar.MONTH);
        DIA = cal.get(Calendar.DAY_OF_MONTH);

        ImageButton back = findViewById(R.id.imageButtonBackFinishAct);
        back.setOnClickListener((v)->{
            finish();
        });



        COD_DISCIPLINA_ATUAL = getIntent().getStringExtra("DISCIPLINA_CODIGO");
        NOME_DISCIPLINA_ATUAL = getIntent().getStringExtra("DISCIPLINA_NOME");
        NOME_PROFESSOR_DISCIPLINA_ATUAL = getIntent().getStringExtra("DISCIPLINA_NOME_PROFESSOR");

        ImageButton loginProfessorFragment = findViewById(R.id.loginProfessor);
        final Intent inputIntent = new Intent(this, InputEdit.class);

        loginProfessorFragment.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                inputIntent.putExtra("DISCIPLINA", NOME_DISCIPLINA_ATUAL);
                inputIntent.putExtra("TYPE", "PASSWORD");
                inputIntent.putExtra("HINT_INPUT", "Digite o código de acesso");
                startActivityForResult(inputIntent, RESULTADO_CODIGO_DISCIPLINA);
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();



        referenciaDeAulasDaDisciplinasSincronaComFb = mDatabase.child("/disciplinas/" + COD_DISCIPLINA_ATUAL + "/aulas/" + ANO + "/" + MES + "/" + DIA).getRef();
        referenciaDeAulasDaDisciplinasSincronaComFb.keepSynced(true);

        referenciaDeAulasDaDisciplinasSincronaComFb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULTADO_CODIGO_DISCIPLINA) {
            if (resultCode == RESULT_OK) {
                if(data.getStringExtra("resposta").equals(COD_DISCIPLINA_ATUAL)){
                    Intent telaProfessor = new Intent(this, LiberaInOutProfessor.class);
                    telaProfessor.putExtra("CODIGO_DISCIPLINA", COD_DISCIPLINA_ATUAL);
                    telaProfessor.putExtra("STATUS_ATUAL", String.valueOf(STATUS_ATUAL));
                    telaProfessor.putExtra("CODIGO_TOMADOR_SALA", TOMADOR_ATUAL_EM_USO);
                    telaProfessor.putExtra("ID_AULA", idDaProximaAula);
                    startActivity(telaProfessor);
                }

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
        referenciaDeAulasDaDisciplinasSincronaComFb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for (DataSnapshot dataSnapshotAula: dataSnapshot.getChildren()){
                        Aula aula = dataSnapshotAula.getValue(Aula.class);
                        if(aula.checkout.status != 2){
                            idDaProximaAula = dataSnapshotAula.getKey();
                            controlaStatusDaAula();
                        } else mostraFragmentSemAula();
                    }
                }else mostraFragmentSemAula();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void mostraFragmentSemAula() {
        STATUS_ATUAL = SEM_AULA;
        campoTextoHorarioDaAula = findViewById(R.id.campoFixoHorarioDaAula);
        campoHorarioDaAula = findViewById(R.id.horaProximaAula);
        campoTextoHorarioDaAula.setVisibility(View.INVISIBLE);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment semAula = new SemAulasHojeParaDisciplina();
        ft.replace(R.id.espaçoParaColocarFragment, semAula);
        ft.commitAllowingStateLoss();
        dialog.cancel();
    }

    private void controlaStatusDaAula() {
        campoTextoHorarioDaAula.setVisibility(View.VISIBLE);

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

                          int statusAulaCheckin = dsAula.checkin.status;

                          int statusAulaCheckout = dsAula.checkout.status;
                          findViewById(R.id.loginProfessor).setVisibility(View.VISIBLE);
                          TextView horaProximaAula = findViewById(R.id.horaProximaAula);
                          if(!dsAula.hora.inicio.equals(""))
                            horaProximaAula.setText(dsAula.hora.inicio);

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
                                        break;
                                      case 1:
                                          emProcesso = new EmProcessoAula();
                                          ft.replace(R.id.espaçoParaColocarFragment, emProcesso, "emProcesso");
                                          STATUS_ATUAL = CHECKOUT_EM_PROCESSO;
                                          bundle.putString("checkinOuCheckout", "checkout");
                                          podeLerNfcAgora = true;
                                          emProcesso.setArguments(bundle);
                                          break;
                                      case 2:
                                          fimDoProcesso = new FimDoProcesso();
                                          ft.replace(R.id.espaçoParaColocarFragment, fimDoProcesso, "fimDoProcesso");
                                          STATUS_ATUAL = CHECKOUT_ENCERRADO;
                                          bundle.putString("checkinOuCheckout", "checkout");
                                          podeLerNfcAgora = false;
                                          findViewById(R.id.loginProfessor).setVisibility(View.INVISIBLE);
                                          fimDoProcesso.setArguments(bundle);
                                        break;
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
    public void onNewIntent(Intent intent) {
        try {
            EmProcessoAula fragmentEmProcessoAula = (EmProcessoAula) getFragmentManager().findFragmentByTag("emProcesso");
            if(podeLerNfcAgora){
                fragmentEmProcessoAula.getTagId(intent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onNewIntent(intent);
    }

    @Override
    public void onResume(){

        Intent intent = new Intent(this, ProximaDisciplina.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, intent, 0);
        IntentFilter[] intentFilter = new IntentFilter[] {};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);
        super.onResume();
    }

    @Override
    public void onPause(){
        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }

}