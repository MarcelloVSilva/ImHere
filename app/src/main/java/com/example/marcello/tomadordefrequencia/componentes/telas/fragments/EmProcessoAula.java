package com.example.marcello.tomadordefrequencia.componentes.telas.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcello.tomadordefrequencia.R;
import com.example.marcello.tomadordefrequencia.componentes.telas.ProximaDisciplina;
import com.example.marcello.tomadordefrequencia.model.Aluno;
import com.fxn769.Numpad;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class EmProcessoAula extends Fragment {

    private static final int RESULTADO_MATRICULA_ALUNO = 1;
    private DatabaseReference mDatabase;
    private NfcAdapter nfcAdapter;
    private String COD_DISCIPLINA_ATUAL;
    private int STATUS_ATUAL;
    private final int CHECKIN_EM_PROCESSO = 10;
    private final int CHECKOUT_EM_PROCESSO = 21;
    private DatabaseReference referenciaDeAlunosSincronaComFb;
    private int ANO;
    private int MES;
    private int DIA;
    private String idAulaAtual;
    private TextView campoMatricula;
    private String processoAtualEmAndamento;
    private Numpad numpad;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.em_processo_fragment, container, false);
        Button inserirMatriculaBtn = view.findViewById(R.id.inserirMatricula);
        TextView titulo = view.findViewById(R.id.tituloEmProcesso);
        titulo.setText(this.getArguments().getString("checkinOuCheckout")+" liberado");

        STATUS_ATUAL = ((ProximaDisciplina)getActivity()).STATUS_ATUAL;
        ANO = ((ProximaDisciplina)getActivity()).ANO;
        MES = ((ProximaDisciplina)getActivity()).MES;
        DIA = ((ProximaDisciplina)getActivity()).DIA;
        COD_DISCIPLINA_ATUAL = ((ProximaDisciplina)getActivity()).COD_DISCIPLINA_ATUAL;
        idAulaAtual = ((ProximaDisciplina) getActivity()).idDaProximaAula;


        mDatabase = FirebaseDatabase.getInstance().getReference();

        inserirMatriculaBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                campoMatricula = view.findViewById(R.id.matriculaDigitaDial);
                String matricula = String.valueOf(campoMatricula.getText());
                if(!matricula.equals(""))
                    verificarPorMatriculaSeAlunoExisteNaDisciplina(matricula);
            }
        });

        numpad = view.findViewById(R.id.tecladoNum);
        campoMatricula = view.findViewById(R.id.matriculaDigitaDial);
        campoMatricula.setHint("Digite sua matrícula");
        numpad.setOnTextChangeListner((String text, int digits_remaining) -> {
            campoMatricula.setText(text);
        });
        nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        processoAtualEmAndamento = STATUS_ATUAL==CHECKIN_EM_PROCESSO?"checkin":"checkout";
        referenciaDeAlunosSincronaComFb = mDatabase.child("/disciplinas/" + COD_DISCIPLINA_ATUAL + "/aulas/" + ANO + "/" + MES + "/" + DIA + "/" + idAulaAtual +"/"+ processoAtualEmAndamento +"/alunos").getRef();
        referenciaDeAlunosSincronaComFb.keepSynced(true);

        referenciaDeAlunosSincronaComFb.addValueEventListener(new ValueEventListener() {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULTADO_MATRICULA_ALUNO) {
            if (resultCode == RESULT_OK) {
                String matricula = data.getStringExtra("resposta");
                if(!matricula.equals(""))
                    verificarPorMatriculaSeAlunoExisteNaDisciplina(matricula);
            }
        }
    }



    public void getTagId(Intent intent) throws IOException, JSONException {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] idStudent = tag.getId();

        ArrayList nfc_id_array = new ArrayList();
        for(byte item: idStudent){
            nfc_id_array.add(item);
        }
        String nfc_id_aux = nfc_id_array.toString();
        String nfc_id = nfc_id_aux.replaceAll("[\\[\\] ]", "");
        verificarPeloNfcIdSeAlunoExisteNaDisciplina(nfc_id);
    }

    private void verificarPeloNfcIdSeAlunoExisteNaDisciplina(String nfc_id) {
        mDatabase.child("/disciplinas/"+COD_DISCIPLINA_ATUAL+"/alunos").addValueEventListener(new ValueEventListener() {

            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot aluno: dataSnapshot.getChildren()){
                    Aluno alunoDb = aluno.getValue(Aluno.class);
                    if(alunoDb.nfc_id != null){
                        if(alunoDb.nfc_id.equals(nfc_id)) {
                            String matriculaDoAluno = aluno.getKey();
                            verificaSeJaNaoFoiInserido(matriculaDoAluno);
                            break;
                        } else alunoNaoExisteNaDisciplina();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void verificarPorMatriculaSeAlunoExisteNaDisciplina(String matricula) {
        mDatabase.child("/disciplinas/"+COD_DISCIPLINA_ATUAL+"/alunos/"+matricula).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    verificaSeJaNaoFoiInserido(matricula);
                } else alunoNaoExisteNaDisciplina();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void alunoNaoExisteNaDisciplina() {
        Toast.makeText(getActivity(), "Aluno(a) não encontrado(a) :(", Toast.LENGTH_LONG).show();
    }

    private void verificaSeJaNaoFoiInserido(String matriculaDoAluno) {
        referenciaDeAlunosSincronaComFb.child(matriculaDoAluno).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                    registrarPresencaNesteProcessoParaAluno(matriculaDoAluno);
                else alunoJaFoiInserido();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void alunoJaFoiInserido() {
        Toast.makeText(getActivity(), "Você já fez "+processoAtualEmAndamento, Toast.LENGTH_LONG).show();
    }

    private void registrarPresencaNesteProcessoParaAluno(String matriculaDoAluno) {

        Map<String, Object> mapAluno = new HashMap<>();
        mapAluno.put(matriculaDoAluno, true);
        referenciaDeAlunosSincronaComFb.updateChildren(mapAluno);
        Toast.makeText(getActivity(), ""+processoAtualEmAndamento+" registrado", Toast.LENGTH_LONG).show();

    }


}