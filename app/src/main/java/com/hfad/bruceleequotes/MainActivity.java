package com.hfad.bruceleequotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private QuoteExpert expert;
    private String currentQuote;
    private TextView quote;
    private static final String KEY_QUOTE = "quote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quote = (TextView) findViewById(R.id.quote);
        if(savedInstanceState != null) {
            currentQuote = savedInstanceState.getString(KEY_QUOTE);
            quote.setText(currentQuote);
        }
        expert = new QuoteExpert(getApplicationContext());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
            savedInstanceState.putString(KEY_QUOTE, currentQuote);
    }

    public void onClickCopyQuote(View view) {
        if(currentQuote != null) {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(KEY_QUOTE, "\"" + currentQuote + "\" -Bruce Lee");
            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(MainActivity.this, "Copied", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "No Quote To Copy", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickFindQuote(View view) {
        currentQuote = expert.getRandomQuote();
        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        quote.setText(currentQuote);
        quote.startAnimation(animFadeIn);
    }

    public void onClickShareQuote(View view) {
        if (currentQuote != null) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, "Bruce Lee Quote");
            share.putExtra(Intent.EXTRA_TEXT, "\"" + currentQuote + "\" -Bruce Lee");
            startActivity(Intent.createChooser(share, "Share via"));
        } else {
            Toast.makeText(MainActivity.this, "No Quote To Share", Toast.LENGTH_SHORT).show();
        }

    }

}
