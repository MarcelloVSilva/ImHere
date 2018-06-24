package com.example.marcello.tomadordefrequencia.componentes.telas;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.marcello.tomadordefrequencia.R;

public class LiberaInOutProfessor extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liberar_processo_professor);
    }
    //ligar com banco
    //verificar se tem aula pra hoje
    //se tiver só libera ou fecha processo
    //caso nao tenha aula, criar aula pra hoje, ja com check-in status 1.
}


