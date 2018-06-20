package com.example.marcello.tomadordefrequencia.componentes.telas;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.marcello.tomadordefrequencia.R;

public class EmProcessoAula extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.em_processo_fragment, container, false);
        Button button = view.findViewById(R.id.inserirMatricula);
        TextView titulo = view.findViewById(R.id.tituloEmProcesso);
        titulo.setText(this.getArguments().getString("checkinOuCheckout")+" liberado");

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("MATRICULA", "inserir matricula");
                // abrir: teclado, campo e botao de enviar
            }
        });
        return view;
    }
}
