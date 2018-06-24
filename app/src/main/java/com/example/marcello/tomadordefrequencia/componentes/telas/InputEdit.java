package com.example.marcello.tomadordefrequencia.componentes.telas;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.marcello.tomadordefrequencia.R;

public class InputEdit extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input);

        EditText respostaCampo = findViewById(R.id.insereCodigo);
        String typeEdit = getIntent().getStringExtra("TYPE");
        if (typeEdit.equals("PASSWORD")){
            respostaCampo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        respostaCampo.setSelection(respostaCampo.getText().length());
        ImageButton back = findViewById(R.id.imageButtonBackFinishAct);
        respostaCampo.setHint(getIntent().getStringExtra("HINT_INPUT"));
        Button confirmabtn = findViewById(R.id.confirmabtn);

        confirmabtn.setOnClickListener((v)->{
            Intent devolve = new Intent();
            String resposta = String.valueOf(respostaCampo.getText());
            devolve.putExtra("resposta", resposta);
            setResult(RESULT_OK, devolve);
            finish();
        });

        back.setOnClickListener((v)->{
            finish();
        });
    }

//    @Override
//    public void onBackPressed() {
//
//        super.onBackPressed();
//    }


    @Override
    protected void onStart() {
        super.onStart();
    }

}


