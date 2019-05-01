package com.hfad.bruceleequotes;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class QuoteReader {

    private Context mContext;
    private ArrayList<String> quotes;
    private String mFilename;

    QuoteReader(Context context, String filename) {
        mContext = context;
        mFilename = filename;
        quotes = readAssetsFileToArrayList();
    }

    public ArrayList<String> getQuoteArrayList() {
        return quotes;
    }

    private ArrayList<String> readAssetsFileToArrayList() {
        ArrayList<String> quotes = new ArrayList<String>();
        AssetManager manager = mContext.getAssets();
        try {
            InputStream is = manager.open(mFilename);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str = null;
            while((str = br.readLine()) != null) {
                quotes.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return quotes;
    }
}
