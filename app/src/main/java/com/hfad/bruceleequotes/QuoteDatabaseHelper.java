package com.hfad.bruceleequotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class QuoteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "quotes";
    private static final int DB_VERSION = 1;
    private static final String FILE_NAME = "bruceleequotes.txt";

    private List<String> quoteList;
    private Context mContext;

    QuoteDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        quoteList = generateQuoteList();
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

    private List<String> generateQuoteList() {
        QuoteReader reader = new QuoteReader(mContext, FILE_NAME);
        return reader.getQuoteArrayList();
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE QUOTE (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "FULL_QUOTE TEXT, "
            + "VIEWED NUMERIC, "
            + "FAVORITE NUMERIC);");

            String nextQuote = "";
            for(int i=0; i < quoteList.size(); i++) {
                nextQuote = quoteList.get(i);
                insertQuote(db, nextQuote, 0, 0);
            }
        }
    }
}
