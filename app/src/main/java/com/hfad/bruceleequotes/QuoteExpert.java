package com.hfad.bruceleequotes;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuoteExpert {
    private Context mContext;
    private List<String> quoteList;

    public QuoteExpert(Context context) {
        mContext = context;
        quoteList = getQuotes();
    }

    private ArrayList<String> getQuotes() {
        ArrayList<String> quotes = new ArrayList<>();

        BufferedReader reader;
        InputStream is;
        AssetManager manager;

        try {
            manager = mContext.getAssets();
            is = manager.open("bruceleequotes.txt");
            //is = mContext.getResources().openRawResource(R.raw.bruceleequotes);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while (line != null) {
                quotes.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return quotes;
    }

    public String getRandomQuote() {
        Random rand = new Random();
        int randomPosition = rand.nextInt(quoteList.size());
        return quoteList.get(randomPosition);
    }

}
