package com.example.marcello.tomadordefrequencia.componentes.telas.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.marcello.tomadordefrequencia.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class IdentificacaoAluno extends Fragment {

//
//    public IdentificacaoAluno() {
//        // Required empty public constructor
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_identificacao_aluno, container, false);
        TextView alunoIdentificado = view.findViewById(R.id.alunoIdentificado);
        String identificacao = getArguments().getString("IDENTIFICACAO");
        alunoIdentificado.setText(identificacao);

        return view;
    }

}
