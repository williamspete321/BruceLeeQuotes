package com.hfad.bruceleequotes;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuoteExpert {
    private List<String> quoteList;

    public QuoteExpert() {
        quoteList = getQuotes();
    }

    private ArrayList<String> getQuotes() {
        ArrayList<String> quotes = new ArrayList<>();

        Context context = MainActivity.getContext();
        InputStream is = context.getResources().openRawResource(R.raw.bruceleequotes);
        BufferedReader reader;
        try {
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
        Log.d("testMessage", "we are using the getRandomQuote() method I made");
        Random rand = new Random();
        int randomPosition = rand.nextInt(quoteList.size());
        return quoteList.get(randomPosition);
    }

}
