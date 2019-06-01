package com.hfad.bruceleequotes;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

public class QuoteDatabaseManager {
    private static final String TAG = "QuoteDatabase";

    public static final String COL_ID = "ID";
    public static final String COL_QUOTE = "QUOTE";
    public static final String COL_VIEWED = "VIEWED";
    public static final String COL_FAVORITE = "FAVORITE";

    private static final String DB_NAME = "quotes_3";
    private static final String TABLE_NAME = "QUOTE_TABLE";
    private static final int DB_VERSION = 1;

    public static final String FILE_NAME = "bruceleequotes.txt";

    private final QuoteDatabaseOpenHelper quoteDatabaseOpenHelper;
    //private Context mContext; use if needed, so context isn't so nested

    public QuoteDatabaseManager(Context context) {
        quoteDatabaseOpenHelper = new QuoteDatabaseOpenHelper(context);
        //mContext = context;
    }

    private Cursor queryNewRandomQuote() {
        //get a random quote from the table that hasn't been viewed yet (VIEWED = 0)
        Cursor cursor = quoteDatabaseOpenHelper.getReadableDatabase().query(TABLE_NAME,
                new String[] {COL_ID, COL_QUOTE, COL_VIEWED, COL_FAVORITE},
                COL_VIEWED + " = ?", new String[] {Integer.toString(0)},
                null, null, "RANDOM() limit 1");
        return cursor;
    }

    public Quote getRandomQuote() {
        Quote quote = null;
        try {
            Cursor cursor = queryNewRandomQuote();
            if (cursor.moveToFirst()) {

                int id = cursor.getInt(0);
                String quoteText = cursor.getString(1);
                boolean quoteIsViewed = (cursor.getInt(2) == 1);
                boolean quoteIsFavorite = (cursor.getInt(3) == 1);

                quote = new Quote(id, quoteText, quoteIsViewed, quoteIsFavorite);
            }
            cursor.close();
        } catch (SQLiteException e) {
            Log.e(TAG, "Database unavailable");
        }
        return quote;
    }

    public void updateSingleQuote(int quoteId, ContentValues contentValues) {
        quoteDatabaseOpenHelper.getWritableDatabase().update(
                TABLE_NAME, contentValues, COL_ID + " = ?", new String[] {Integer.toString(quoteId)});
    }

    public void updateAllQuotes(ContentValues contentValues){
        quoteDatabaseOpenHelper.getWritableDatabase().update(
                TABLE_NAME, contentValues, null, null);
    }

    public void close() {
        quoteDatabaseOpenHelper.close();
    }

    private static class QuoteDatabaseOpenHelper extends SQLiteOpenHelper {

        private final Context helperContext;
        private SQLiteDatabase mDatabase;

        private static final String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_QUOTE + " TEXT, " +
                COL_VIEWED + " NUMERIC, " +
                COL_FAVORITE + " NUMERIC)";

        QuoteDatabaseOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            helperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db; //use if needed, maybe won't
            updateMyDatabase(0, DB_VERSION);
            //previous version updateMyDatabase(db, 0, DB_VERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            updateMyDatabase(oldVersion, newVersion);
        }

        private void updateMyDatabase(int oldVersion, int newVersion) {
            if (oldVersion < 1) {
                mDatabase.execSQL(sql);
                populateQuotesIntoDatabase();
            }
        }

        private void populateQuotesIntoDatabase() {
            try {
                loadQuotes();
            } catch (IOException e) {
                Log.e(TAG, "unable to populate quotes into database");
            }
        }

        private void loadQuotes() throws IOException {
            final AssetManager manager = helperContext.getAssets();
            InputStream inputStream = manager.open(FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    long id = addQuote(line, 0, 0);
                    if (id < 0) {
                        Log.e(TAG, "unable to add quote: " + line);
                    }
                }
            } finally {
                reader.close();
            }
        }

        private long addQuote(String fullQuote, int viewed, int favorite) {
            ContentValues quoteValues = new ContentValues();
            quoteValues.put(COL_QUOTE, fullQuote);
            quoteValues.put(COL_VIEWED, viewed);
            quoteValues.put(COL_FAVORITE, favorite);
            return mDatabase.insert(TABLE_NAME, null, quoteValues);
        }
    }
}
