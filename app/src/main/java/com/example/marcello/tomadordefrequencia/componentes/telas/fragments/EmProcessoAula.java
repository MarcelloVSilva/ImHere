package com.example.marcello.tomadordefrequencia.componentes.telas.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.marcello.tomadordefrequencia.R;
import com.example.marcello.tomadordefrequencia.componentes.telas.InputEdit;
import com.example.marcello.tomadordefrequencia.componentes.telas.ProximaDisciplina;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.app.Activity.RESULT_OK;

public class EmProcessoAula extends Fragment {

    private static final int RESULTADO_MATRICULA_ALUNO = 1;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.em_processo_fragment, container, false);
        Button inserirMatriculaBtn = view.findViewById(R.id.inserirMatricula);
        TextView titulo = view.findViewById(R.id.tituloEmProcesso);
        titulo.setText(this.getArguments().getString("checkinOuCheckout")+" liberado");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final Intent inputIntent = new Intent(getActivity(), InputEdit.class);

        inserirMatriculaBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                inputIntent.putExtra("TYPE", "TEXT");
                inputIntent.putExtra("HINT_INPUT", "Digite sua matrícula");
                startActivityForResult(inputIntent, RESULTADO_MATRICULA_ALUNO);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == RESULTADO_MATRICULA_ALUNO) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String matricula = data.getStringExtra("resposta");
                ((ProximaDisciplina)getActivity()).verificarSeAlunoExisteNaDisciplinaMatricula(matricula);
            }
        }
    }

}
