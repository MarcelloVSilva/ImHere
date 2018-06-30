package com.example.marcello.tomadordefrequencia.componentes.telas;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcello.tomadordefrequencia.R;
import com.example.marcello.tomadordefrequencia.model.Aula;
import com.example.marcello.tomadordefrequencia.model.Disciplina;
import com.example.marcello.tomadordefrequencia.model.Processo;
import com.example.marcello.tomadordefrequencia.model.Sala;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LiberaInOutProfessor extends AppCompatActivity {


    Timer timer;
    TimerTask timerTask;

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

    public int tempo_processo_aberto;
    private String idAulaAtual;
    private Calendar calendar;
    private DatabaseReference referenciaDaDisciplinaHojeSincronaComFb;
    private String processoAtual;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liberar_processo_professor);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        codDisciplina = getIntent().getStringExtra("CODIGO_DISCIPLINA");
        idAulaAtual = getIntent().getStringExtra("ID_AULA");
        codSala= getIntent().getStringExtra("CODIGO_TOMADOR_SALA");
        Date date = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(date);

        ImageButton back = findViewById(R.id.imageButtonBackFinishAct);
        back.setOnClickListener((v)->{
            finish();
        });

        ANO = calendar.get(Calendar.YEAR);
        MES = calendar.get(Calendar.MONTH);
        DIA = calendar.get(Calendar.DAY_OF_MONTH);



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

        referenciaDaDisciplinaHojeSincronaComFb = mDatabase.child("/disciplinas/" + codDisciplina + "/aulas/" + ANO + "/" + MES + "/" + DIA);
        referenciaDaDisciplinaHojeSincronaComFb.keepSynced(true);

        mDatabase.child("/disciplinas/"+codDisciplina).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Disciplina disciplina = dataSnapshot.getValue(Disciplina.class);
                nomeDisciplina.setText(disciplina.nome);
                nomeProfessor.setText(disciplina.nomeProfessor);
                detalhes4.setText("Quantidade de alunos: "+String.valueOf(((HashMap) disciplina.alunos).size()));
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
                        SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy");
                        String dataDaAulaHoje = DIA+"/"+(MES+1)+"/"+ANO;

                        if(check_tempo_limite.isChecked()){
                            processoAtual = "checkin";
                            tempo_processo_aberto =  Integer.parseInt(String.valueOf(tempo_definido.getText()));
                            iniciarTimerParaFecharProcesso(tempo_processo_aberto);
                        }
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        int minutes = calendar.get(Calendar.MINUTE);
                        DatabaseReference diaAulaRef = referenciaDaDisciplinaHojeSincronaComFb.getRef();
                        idAulaAtual = diaAulaRef.push().getKey();

                        Processo checkin =  new Processo();
                        checkin.status = 1;
                        checkin.podeLiberar = true;
                        Processo checkout =  new Processo();
                        checkout.status = 0;
                        checkout.podeLiberar = false;
                        Map<String, Object> hora = new HashMap<>();
                        hora.put("fim", "");
                        hora.put("inicio", hours+":"+minutes);
                        Aula novaAula = new Aula(dataDaAulaHoje, hora, codSala, checkin, checkout);
                        referenciaDaDisciplinaHojeSincronaComFb.child("/"+idAulaAtual).setValue(novaAula);
                        finish();

                    }
                });
                break;
            case CHECKIN_AINDA_NAO_COMECOU:
                checkinOuCheckout.setText("Check-in");
                liberarProcesso.setText("Iniciar checkin");
                liberarProcesso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(check_tempo_limite.isChecked()){
                            processoAtual = "checkin";
                            tempo_processo_aberto =  Integer.parseInt(String.valueOf(tempo_definido.getText()));
                            iniciarTimerParaFecharProcesso(tempo_processo_aberto);
                        }
                        Map<String, Object> status = new HashMap<>();
                        status.put("status", 1);
                        referenciaDaDisciplinaHojeSincronaComFb.child(idAulaAtual+"/checkin").updateChildren(status);
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
                        forcarParadaDoTimer(view);



                        Map<String, Object> statusCheckin = new HashMap<>();
                        Map<String, Object> statusCheckout = new HashMap<>();
                        statusCheckin.put("status", 2);
                        statusCheckin.put("podeLiberar", false);
                        statusCheckout.put("podeLiberar", true);
                        forcarParadaDoTimer(view);
                        referenciaDaDisciplinaHojeSincronaComFb.child(idAulaAtual+"/checkin").updateChildren(statusCheckin);
                        referenciaDaDisciplinaHojeSincronaComFb.child(idAulaAtual+"/checkout").updateChildren(statusCheckout);
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
                        processoAtual = "checkout";
                        if(check_tempo_limite.isChecked()){
                            tempo_processo_aberto =  Integer.parseInt(String.valueOf(tempo_definido.getText()));
                            iniciarTimerParaFecharProcesso(tempo_processo_aberto);
                        }
                        Map<String, Object> status = new HashMap<>();
                        status.put("status", 1);
                        referenciaDaDisciplinaHojeSincronaComFb.child(idAulaAtual+"/checkout").updateChildren(status);
                        finish();
                    }
                });
                break;
            case CHECKOUT_EM_PROCESSO:
                checkinOuCheckout.setText("Check-out");
                liberarProcesso.setText("Finalizar checkout");
                check_tempo_limite.setEnabled(false);
                tempo_definido.setEnabled(false);
                liberarProcesso.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        forcarParadaDoTimer(view);
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        int minutes = calendar.get(Calendar.MINUTE);

                        Map<String, Object> status = new HashMap<>();
                        Map<String, Object> hora = new HashMap<>();
                        hora.put("fim", hours+":"+minutes);
                        status.put("status", 2);
                        status.put("podeLiberar", false);
                        referenciaDaDisciplinaHojeSincronaComFb.child(idAulaAtual+"/hora").updateChildren(hora);
                        referenciaDaDisciplinaHojeSincronaComFb.child(idAulaAtual+"/checkout").updateChildren(status);
                        finish();

                    }
                });
                break;
            case CHECKOUT_ENCERRADO:
                checkinOuCheckout.setText("Check-out");
                break;
        }
    }


    public void iniciarTimerParaFecharProcesso(int tempoQueProcessoFicaraAberto) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }
        }, tempoQueProcessoFicaraAberto*1000*60);

    }

    public void forcarParadaDoTimer(View v) {
        if (timer != null) {
            timer.cancel();
            Toast.makeText(getBaseContext(), "Parou timer", Toast.LENGTH_LONG).show();
            timer = null;
        }
    }

    private void TimerMethod() {
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);

            Map<String, Object> status = new HashMap<>();
            status.put("status", 2);
            status.put("podeLiberar", false);

            referenciaDaDisciplinaHojeSincronaComFb.child(idAulaAtual+"/"+processoAtual).updateChildren(status);
            if(processoAtual.equals("checkin")){
                Map<String, Object> statusCheckout = new HashMap<>();
                statusCheckout.put("podeLiberar", true);
                referenciaDaDisciplinaHojeSincronaComFb.child(idAulaAtual+"/checkout").updateChildren(statusCheckout);

            }
            if(processoAtual.equals("checkout")){
                Map<String, Object> hora = new HashMap<>();
                hora.put("fim", hours+":"+minutes);
                referenciaDaDisciplinaHojeSincronaComFb.child(idAulaAtual+"/hora").updateChildren(hora);
            }

            Toast.makeText(getBaseContext(), processoAtual+" encerrado automaticamente", Toast.LENGTH_LONG).show();
        }
    };


}


