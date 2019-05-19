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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private String currentQuote;
    private TextView quote;
    private static final String KEY_QUOTE = "quote";

    private QuoteDatabaseManager quoteDatabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quoteDatabaseManager = new QuoteDatabaseManager(getApplicationContext());
        quote = (TextView) findViewById(R.id.quote);

        if(savedInstanceState != null) {
            currentQuote = savedInstanceState.getString(KEY_QUOTE);
            quote.setText(currentQuote);
        } else {
            quoteDatabaseManager = new QuoteDatabaseManager(getApplicationContext());
            ContentValues quoteValues = new ContentValues();
            quoteValues.put("VIEWED", false);
            try {
                quoteDatabaseManager.updateAllQuotes(quoteValues);
            } catch (SQLiteException e) {
                Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
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
        try {
            Cursor cursor = quoteDatabaseManager.getRandomQuote();
            if (cursor.moveToFirst()) {
                int quoteId = cursor.getInt(0);
                currentQuote = cursor.getString(1);
                Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                quote.setText(currentQuote);
                quote.startAnimation(animFadeIn);
                new UpdateQuotesTask().execute(quoteId);
            } else {
                Toast toast = Toast.makeText(this, "No new quotes to view", Toast.LENGTH_SHORT);
                toast.show();
            }
            cursor.close();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
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

    //Inner class to update the quote to "VIEWED = true"
    private class UpdateQuotesTask extends AsyncTask<Integer, Void, Boolean> {
        private ContentValues quoteValues;

        protected void onPreExecute() {
            quoteValues = new ContentValues();
            quoteValues.put("VIEWED", true);
        }

        protected Boolean doInBackground(Integer... quotes) {
            int quoteId = quotes[0];
            try {
                quoteDatabaseManager.updateSingleQuote(quoteId, quoteValues);
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean success) {
            if(!success) {
                Toast toast = Toast.makeText(MainActivity.this, "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onDestroy() {
        quoteDatabaseManager.close();
        super.onDestroy();
    }
}
