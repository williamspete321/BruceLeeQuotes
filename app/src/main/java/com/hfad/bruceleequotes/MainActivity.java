package com.hfad.bruceleequotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.haha.perflib.Main;

public class MainActivity extends AppCompatActivity {
    //private QuoteExpert expert;
    private String currentQuote;
    private TextView quote;
    private int quoteId;
    private static final String KEY_QUOTE = "quote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quote = (TextView) findViewById(R.id.quote);
        SQLiteOpenHelper quoteDatabaseHelper = new QuoteDatabaseHelper(getApplicationContext());

        if(savedInstanceState != null) {
            currentQuote = savedInstanceState.getString(KEY_QUOTE);
            quote.setText(currentQuote);
        } else {
            ContentValues quoteValues = new ContentValues();
            quoteValues.put("VIEWED", false);
            try {
                SQLiteDatabase db = quoteDatabaseHelper.getWritableDatabase();
                db.update("QUOTE", quoteValues, null, null);
                db.close();
            } catch (SQLiteException e) {
                Toast toast = Toast.makeText(this,
                        "Database unavailable",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
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

            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No Quote To Copy", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickFindQuote(View view) {
        SQLiteOpenHelper quoteDatabaseHelper = new QuoteDatabaseHelper(getApplicationContext());
        try {
            SQLiteDatabase db = quoteDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("QUOTE",
                    new String[] {"_id", "FULL_QUOTE"}, // add VIEWED, FAVORITE later
                    "VIEWED = ?",
                    new String[] {Integer.toString(0)},
                    null, null, "RANDOM() limit 1"); //get a random quote

            //Move to the first record in the cursor

            if (cursor.moveToFirst()) {
                quoteId = cursor.getInt(0);
                currentQuote = cursor.getString(1);
                Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                quote.setText(currentQuote);
                quote.startAnimation(animFadeIn);
                new UpdateQuotesTask().execute(quoteId);
            } else {
                Toast toast = Toast.makeText(this,
                        "No new quotes to view",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
            cursor.close();
            db.close(); // do I need to close the database here?
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this,
                    "Database unavailable",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onClickShareQuote(View view) {
        if (currentQuote != null) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, "Bruce Lee Quote");
            share.putExtra(Intent.EXTRA_TEXT, "\"" + currentQuote + "\" -Bruce Lee");
            startActivity(Intent.createChooser(share, "Share via"));
        } else {
            Toast.makeText(this, "No Quote To Share", Toast.LENGTH_SHORT).show();
        }
    }

    //Inner class to update the quote
    private class UpdateQuotesTask extends AsyncTask<Integer, Void, Boolean> {
        private ContentValues quoteValues;

        protected void onPreExecute() {
            quoteValues = new ContentValues();
            quoteValues.put("VIEWED", true);
        }

        protected Boolean doInBackground(Integer... quotes) {
            int quoteId = quotes[0];
            SQLiteOpenHelper quoteDatabaseHelper = new QuoteDatabaseHelper(getApplicationContext());
            try {
                SQLiteDatabase db = quoteDatabaseHelper.getWritableDatabase();
                db.update("QUOTE", quoteValues,
                        "_id = ?", new String[] {Integer.toString(quoteId)});
                db.close();
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean success) {
            if(!success) {
                Toast toast = Toast.makeText(MainActivity.this,
                        "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
