package com.example.marcello.tomadordefrequencia.componentes.telas.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.marcello.tomadordefrequencia.R;

public class IdentificacaoAluno extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_identificacao_aluno, container, false);
        TextView alunoIdentificado = view.findViewById(R.id.alunoIdentificado);
        String identificacao = getArguments().getString("IDENTIFICACAO");
        alunoIdentificado.setText(identificacao);

        return view;
    }


}
