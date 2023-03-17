package com.example.simpletranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editSourceLangText = findViewById(R.id.edt_source_lang_text);
        TextView tvTargetLangText = findViewById(R.id.tv_target_lang_text);
        Button btnTranslate = findViewById(R.id.btn_translate);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        //Translator Object Creation
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.HINDI)
                        .build();
        final Translator englishHindiTranslator = Translation.getClient(options);

        // Closing Translator
        getLifecycle().addObserver(englishHindiTranslator);

        //Button Usage
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                btnTranslate.setVisibility(View.GONE);

                //Conditons is the model present
                DownloadConditions conditions = new DownloadConditions.Builder()
                        .requireWifi()
                        .build();
                englishHindiTranslator.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener(
                                (OnSuccessListener) v1 -> {
                                    // Model downloaded successfully. Okay to start translating.
                                    // (Set a flag, unhide the translation UI, etc.)
                                    Log.e("simpleTranslator","Download Successful");
                                    progressBar.setVisibility(View.GONE);
                                    btnTranslate.setVisibility(View.VISIBLE);

                                    // Translator Code Called
                                    englishHindiTranslator.translate(editSourceLangText.getText().toString())
                                            .addOnSuccessListener(
                                                    (OnSuccessListener) translatedText -> {
                                                        // Translation successful.
                                                        tvTargetLangText.setText(translatedText.toString());
                                                    })
                                            .addOnFailureListener(
                                                    new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Error.
                                                            // ...
                                                            Log.e("simpleTranslator",e.toString());
                                                        }
                                                    });
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Model couldn’t be downloaded or other internal error.
                                        // ...
                                        Log.e("simpleTranslator","Download Error :"+e.toString());
                                        progressBar.setVisibility(View.GONE);
                                        btnTranslate.setVisibility(View.VISIBLE);
                                    }
                                });
            }
        });
    }
}