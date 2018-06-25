package com.example.marcello.tomadordefrequencia.componentes.telas;


import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.util.Map;

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
    private Button liberarProcesso;
    private int ANO;
    private int MES;
    private int DIA;
    private int STATUS_ATUAL;

    public boolean runnableTimeProcess;


    private final int CHECKIN_AINDA_NAO_COMECOU = 00;
    private final int CHECKIN_EM_PROCESSO = 10;
    private final int CHECKIN_ENCERRADO = 20;
    private final int CHECKOUT_EM_PROCESSO = 21;
    private final int CHECKOUT_ENCERRADO  = 22;
    private final int SEM_AULA = 99;
    private EditText tempo_definido;
    private CheckBox check_tempo_limite;

    public long tempo_processo_aberto;

    Handler handler = new Handler();

    private Runnable updateData = new Runnable(){
        public void run(){
            handler.postDelayed(updateData,10000);
        }
    };
    private String idAulaAtual;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liberar_processo_professor);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        codDisciplina = getIntent().getStringExtra("CODIGO_DISCIPLINA");
        idAulaAtual = getIntent().getStringExtra("ID_AULA");
        codSala= getIntent().getStringExtra("CODIGO_TOMADOR_SALA");
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);




        ANO = cal.get(Calendar.YEAR);
        MES = cal.get(Calendar.MONTH);
        DIA = cal.get(Calendar.DAY_OF_MONTH);

//        hora = new Object();

        liberarProcesso = findViewById(R.id.liberar_processo);

        check_tempo_limite = findViewById(R.id.check_tempo_limite);
        tempo_definido = findViewById(R.id.tempo_limite_definido);
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

        DatabaseReference diaAula = mDatabase.child("/disciplinas/" + codDisciplina + "/aulas/" + ANO + "/" + MES + "/" + DIA);
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
        STATUS_ATUAL = Integer.parseInt(getIntent().getStringExtra("STATUS_ATUAL"));
        switch (STATUS_ATUAL){
            case SEM_AULA:
                checkinOuCheckout.setText("Check-in");
                liberarProcesso.setText("Criar aula e iniciar checkin");
                liberarProcesso.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        tempo_processo_aberto = 0;

                        DatabaseReference diaAulaRef = diaAula.getRef();
                        String key = diaAulaRef.push().getKey();
                        Map<String, Object> checkin = new HashMap<>();
                        Map<String, Object> checkout = new HashMap<>();
                        Map<String, Object> hora = new HashMap<>();
                        checkin.put("podeLiberar", true);
                        checkin.put("status", 1);
                        checkout.put("podeLiberar", false);
                        checkout.put("status", 0);
                        hora.put("fim", "23:59");
                        hora.put("inicio", "23:00");
                        Aula novaAula = new Aula("25/06/2018", hora, "cac209", checkin, checkout);
                        diaAula.child("/"+key).setValue(novaAula);
                        finish();

                        if(check_tempo_limite.isChecked()){
                            int delayMillis = Integer.parseInt(String.valueOf(tempo_definido.getText()));
                            tempo_processo_aberto = delayMillis*60*1000;

                            runnableTimeProcess =  new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            Map<String, Object> status = new HashMap<>();
                                            status.put("status", 2);
                                            diaAula.child("/"+key+"/checkin").updateChildren(status);
                                        }
                                    },
                                    tempo_processo_aberto);
                        }
                    }
                });
                break;
            case CHECKIN_AINDA_NAO_COMECOU:
                checkinOuCheckout.setText("Check-in");
                liberarProcesso.setText("Iniciar checkin");
                liberarProcesso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> status = new HashMap<>();
                        status.put("status", 1);
                        diaAula.child(idAulaAtual+"/checkin").updateChildren(status);
                        if(check_tempo_limite.isChecked()){
                            int delayMillis = Integer.parseInt(String.valueOf(tempo_definido.getText()));
                            tempo_processo_aberto = delayMillis*60*1000;

                            runnableTimeProcess =  new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            Map<String, Object> status = new HashMap<>();
                                            status.put("status", 2);
                                            diaAula.child(idAulaAtual+"/checkin").updateChildren(status);
                                        }
                                    },
                                    tempo_processo_aberto);
                        }
                        finish();
                    }
                });
                break;
            case CHECKIN_EM_PROCESSO:
                checkinOuCheckout.setText("Check-in");
                check_tempo_limite.setEnabled(false);
                tempo_definido.setEnabled(false);
                liberarProcesso.setText("Encerrar checkin");
                liberarProcesso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> statusCheckin = new HashMap<>();
                        Map<String, Object> statusCheckout = new HashMap<>();
                        statusCheckin.put("status", 2);
                        statusCheckin.put("podeLiberar", false);
                        statusCheckout.put("podeLiberar", true);
                        diaAula.child(idAulaAtual+"/checkin").updateChildren(statusCheckin);
                        diaAula.child(idAulaAtual+"/checkout").updateChildren(statusCheckout);
                        finish();
                    }
                });
                break;
            case CHECKIN_ENCERRADO:
                checkinOuCheckout.setText("Check-out");
                liberarProcesso.setText("Iniciar checkout");
                liberarProcesso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                break;
            case CHECKOUT_EM_PROCESSO:
                checkinOuCheckout.setText("Check-out");
                liberarProcesso.setText("Finalizar checkout");
                liberarProcesso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                break;
            case CHECKOUT_ENCERRADO:
                checkinOuCheckout.setText("Check-out");
                break;
        }

//        mDatabase.child("/disciplinas/" + codDisciplina + "/aulas/" + ANO + "/" + MES + "/" + DIA);
    }

    //ligar com banco
    //verificar se tem aula pra hoje
    //se tiver só libera ou fecha processo
    //caso nao tenha aula, criar aula pra hoje, ja com check-in status 1.
}


