package com.hfad.bruceleequotes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.content.Context;

public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;
    private QuoteExpert expert;
    private String currentQuote;
    //private List<String> quoteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState != null) {
            currentQuote = savedInstanceState.getString("quote");
            TextView quote = (TextView) findViewById(R.id.quote);
            quote.setText(currentQuote);
        }
        expert = new QuoteExpert();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("quote", currentQuote);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public static Context getContext(){
        Log.d("testMessage", "we are using the getContext() method I made");
        return instance.getApplicationContext();
    }

    public void onClickFindQuote(View view) {
        currentQuote = expert.getRandomQuote();
        TextView quote = (TextView) findViewById(R.id.quote);
        quote.setText(currentQuote);
    }
}
