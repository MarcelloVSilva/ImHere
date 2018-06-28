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
import android.widget.Toast;

import com.example.marcello.tomadordefrequencia.R;

public class InputEdit extends AppCompatActivity {
    public String verificar = new String();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input);

        EditText respostaCampo = findViewById(R.id.insereCodigo);
        String typeEdit = getIntent().getStringExtra("TYPE");
        if (typeEdit.equals("PASSWORD")){
            respostaCampo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        respostaCampo.setSelection(respostaCampo.getText().length());
        respostaCampo.setHint(getIntent().getStringExtra("HINT_INPUT"));

        verificar = getIntent().getStringExtra("PARA_VERIFICAR");
        ImageButton back = findViewById(R.id.imageButtonBackFinishAct);
        back.setOnClickListener((v)->{
            finish();
        });


        Button confirmabtn = findViewById(R.id.confirmabtn);
        confirmabtn.setOnClickListener((v)->{
            Intent devolve = new Intent();
            String resposta = String.valueOf(respostaCampo.getText());
            devolve.putExtra("resposta", resposta);
            setResult(RESULT_OK, devolve);
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


