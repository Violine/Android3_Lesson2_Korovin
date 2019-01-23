package com.example.alexander.android3_lesson2_korovin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Cancellable;
import io.reactivex.subscribers.DisposableSubscriber;

public class MainActivity extends AppCompatActivity {

    private AppCompatEditText editText;
    private TextView textView;
    private Flowable<String> observable;
    private DisposableSubscriber<String> observer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        initView();
    }

    @SuppressLint("CheckResult")
    private void initView() {
        editText = findViewById(R.id.edit_text);
        textView = findViewById(R.id.text_view);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    observable = getEditTextObservable(editText);
                    observer = getObserver(textView);
                    observable.subscribe(observer);
                } else {
                    if (observable != null && observer != null) {
                        if (!observer.isDisposed())
                            observer.dispose();
                    }

                }
            }
        });
    }

    private Flowable<String> getEditTextObservable (final AppCompatEditText editText) {
        return  Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(final FlowableEmitter<String> emitter) throws Exception {
                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (start < s.length()) {
                            char lastChar = s.charAt(start);
                            emitter.onNext(String.valueOf(lastChar));
                        } else emitter.onError(new Throwable("Ошибка"));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };

                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        editText.addTextChangedListener(null);
                    }
                });
                editText.addTextChangedListener(textWatcher);
            }
        }, BackpressureStrategy.BUFFER);
    }

    private DisposableSubscriber<String> getObserver (final TextView textView) {
        return new DisposableSubscriber<String>() {
            @Override
            public void onNext(String s) {
                textView.append(s);

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }
}
