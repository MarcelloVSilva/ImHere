package com.example.marcello.tomadordefrequencia.componentes.telas.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.marcello.tomadordefrequencia.R;

public class SemAulasHojeParaDisciplina  extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.sem_aulas_fragment, container, false);
        return view;
    }
}
