package com.example.marcello.tomadordefrequencia.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.marcello.tomadordefrequencia.R;
import com.example.marcello.tomadordefrequencia.model.Aula;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.marcello.tomadordefrequencia.model.Tomador;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String tomadorEmUso;
    Tomador disciplina = new Tomador();

    Subject<Tomador> mObservable = PublishSubject.create();
    ArrayList arrDisciplinas = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tomadorEmUso = "cac209";

        mDatabase = FirebaseDatabase.getInstance().getReference();

//        ValueEventListener pegaDisciplinas =


                ValueEventListener pegaDisciplinas = mDatabase.child("tomadores").child(tomadorEmUso).child("disciplina").
                addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            disciplina = ds.getValue(Tomador.class);
                            arrDisciplinas.add(disciplina);

                            Log.d("disciplina", disciplina.nome);
                            Log.d("disciplina", disciplina.diasDaSemana);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Log", "passou aqui");
                    }
                });

        mObservable.map(value -> {
            if (!value.codigo.isEmpty()) {
                return criaObserverAulas(value);
            }
            return false;
        });

//        Observable.just(pegaDisciplinas)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableObserver<ValueEventListener>() {
//                    @Override
//                    public void onNext(Tomador tomador) {
//                        Log.d("Observer", "Next");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.d("Observer", "Complete");
//                    }
//                });


        /*
        * Quando a aplicao iniciar no tablet tem haver algum tipo de: fakeLoginTomador("cac209")
        * Onde ao iniciar meio que o tablet diz: Olá, eu sou o tomador de id cac209.
        * */

    }

    private void criaObserverAulas(Tomador value) {
        mDatabase.child("disciplina").child("FIC0046"/*value */).child("aulas").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Aula aulaAtual = dataSnapshot.child(dataHoje(disciplina.horarioDeInicioAula)).getValue(Aula.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Log", "passou aqui");
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();


//        FirebaseDatabase.getInstance().setPersistenceEnabled(true); //save offline


        /*
        * Tenho que: ver qual a próxima disciplina que é de hoje, depois ver se tem aula pra ela hoje
        * */



        }

        public String dataHoje(String hora){
            String momentoAtual = new Date().toString().replace(" ", "");
            return momentoAtual; // assim nao vai dar certo. Arrumar forma de setar hora no new Date
        }

    }
