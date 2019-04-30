package com.hfad.bruceleequotes;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class QuoteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "quotes";
    private static final int DB_VERSION = 1;
    private Context mContext;

    QuoteDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private static void insertQuote(SQLiteDatabase db, String fullQuote,
        int viewed, int favorite) {
        ContentValues quoteValues = new ContentValues();
        quoteValues.put("FULL_QUOTE", fullQuote);
        quoteValues.put("VIEWED", viewed);
        quoteValues.put("FAVORITE", favorite);
        db.insert("QUOTE", null, quoteValues);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE QUOTE (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "FULL_QUOTE TEXT, "
            + "VIEWED NUMERIC, "
            + "FAVORITE NUMERIC);");

            BufferedReader reader;
            InputStream is;
            AssetManager manager;
            String nextQuote;

            try {
                manager = mContext.getAssets();
                is = manager.open("bruceleequotes.txt");
                reader = new BufferedReader(new InputStreamReader(is));
                nextQuote = reader.readLine();
                while (nextQuote != null) {
                    insertQuote(db, nextQuote, 0, 0);
                    nextQuote = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
