package com.example.marcello.tomadordefrequencia.componentes.telas;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.marcello.tomadordefrequencia.R;

public class FimDoProcesso extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.ja_terminou_fragment, container, false);
        RelativeLayout rl = (RelativeLayout) view;

        ImageView img = view.findViewById(R.id.imageViewJaTerminou);
        TextView titulo = view.findViewById(R.id.tituloFimDoProcesso);
        String checkinOuCheckout = this.getArguments().getString("checkinOuCheckout");

        titulo.setText(checkinOuCheckout+" encerrado");
        if(checkinOuCheckout.equals("checkin"))
            img.setImageResource(R.drawable.checkin_encerrado);
        else if (checkinOuCheckout.equals("checkout"))
            img.setImageResource(R.drawable.checkout_encerrado);

        return view;
    }
}


