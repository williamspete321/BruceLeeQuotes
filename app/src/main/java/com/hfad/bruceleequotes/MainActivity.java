package com.hfad.bruceleequotes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Quote currentQuote;
    private String currentQuoteText;
    private int currentQuoteId;
    private boolean currentQuoteIsViewed, currentQuoteIsFavorite;

    private static final String KEY_ID = "ID";
    private static final String KEY_QUOTE = "QUOTE";
    private static final String KEY_VIEWED = "VIEWED";
    private static final String KEY_FAVORITE = "FAVORITE";

    private QuoteDatabaseManager quoteDatabaseManager;
    private TextView quoteTextView;
    private MenuItem favoriteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        quoteDatabaseManager = new QuoteDatabaseManager(getApplicationContext());
        quoteTextView = findViewById(R.id.quote);

        if(savedInstanceState != null) {
            int id = savedInstanceState.getInt(KEY_ID);
            String quote = savedInstanceState.getString(KEY_QUOTE);
            boolean viewed = savedInstanceState.getBoolean(KEY_VIEWED);
            boolean favorite = savedInstanceState.getBoolean(KEY_FAVORITE);

            currentQuote = new Quote(id, quote, viewed, favorite);
            currentQuoteIsFavorite = currentQuote.getFavorite();
            currentQuoteText = currentQuote.getQuote();

            quoteTextView.setText(currentQuoteText);
        } else {
            quoteDatabaseManager = new QuoteDatabaseManager(getApplicationContext());
            ContentValues quoteValues = new ContentValues();
            quoteValues.put("VIEWED", false);
            try {
                quoteDatabaseManager.updateAllQuotes(quoteValues);
            } catch (SQLiteException e) {
                Toast.makeText(getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        favoriteItem = menu.findItem(R.id.favorite);
        if(currentQuote != null) {
            toggleItem(favoriteItem,currentQuoteIsFavorite);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.favorite:
                if(currentQuote != null) {
                    currentQuoteIsFavorite = !currentQuoteIsFavorite;
                    toggleItem(item, currentQuoteIsFavorite);
                    updateFavorite(currentQuoteIsFavorite);
                } else {
                    Toast.makeText(getApplicationContext(), "No Quote To Save", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.share_quote:
                shareQuote();
                break;
            case R.id.menu:
                //or return true?
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleItem(MenuItem item, boolean favoriteState) {
        item.setChecked(favoriteState);
        item.setIcon(iconDrawable(favoriteState));
    }

    private Drawable iconDrawable(boolean favoriteState) {
        int drawableId = (favoriteState ? R.drawable.ic_favorite_checked : R.drawable.ic_favorite_unchecked);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getDrawable(drawableId);
        } else {
            return ContextCompat.getDrawable(getApplicationContext(),drawableId);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(KEY_ID, currentQuoteId);
        savedInstanceState.putString(KEY_QUOTE, currentQuoteText);
        savedInstanceState.putBoolean(KEY_VIEWED, currentQuoteIsViewed);
        savedInstanceState.putBoolean(KEY_FAVORITE, currentQuoteIsFavorite);
    }

    private void updateFavorite(boolean favoriteState) {
        currentQuote.setFavorite(favoriteState);
        new UpdateQuotesTask().execute(currentQuote);
    }

    public void onClickFindQuote(View view) {
        Quote newQuote = quoteDatabaseManager.getRandomQuote();

        if(newQuote != null) {
            currentQuote = newQuote;

            currentQuoteId = currentQuote.getId();
            currentQuoteText = currentQuote.getQuote();
            currentQuoteIsViewed = currentQuote.getViewed();
            currentQuoteIsFavorite = currentQuote.getFavorite();

            toggleItem(favoriteItem, currentQuoteIsFavorite);

            Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
            quoteTextView.setText(currentQuoteText);
            quoteTextView.startAnimation(animFadeIn);

            new UpdateQuotesTask().execute(currentQuote);

        } else {
            Toast.makeText(getApplicationContext(), "No New Quote", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareQuote() {
        if (currentQuote != null) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, "Bruce Lee Quote");
            share.putExtra(Intent.EXTRA_TEXT, "\"" + currentQuoteText + "\" -Bruce Lee");
            startActivity(Intent.createChooser(share, "Share via"));
        } else {
            Toast.makeText(getApplicationContext(), "No Quote To Share", Toast.LENGTH_SHORT).show();
        }
    }

    private class UpdateQuotesTask extends AsyncTask<Quote, Void, Boolean> {
        private ContentValues quoteValues;

        protected void onPreExecute() {
            quoteValues = new ContentValues();
        }

        protected Boolean doInBackground(Quote... quotes) {
            Quote q = quotes[0];
            int quoteId = q.getId();
            quoteValues.put("VIEWED", true);
            quoteValues.put("FAVORITE", q.getFavorite());
            try {
                quoteDatabaseManager.updateSingleQuote(quoteId, quoteValues);
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean success) {
            if(!success) {
                Toast.makeText(MainActivity.this, "Database unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        quoteDatabaseManager.close();
    }
}
