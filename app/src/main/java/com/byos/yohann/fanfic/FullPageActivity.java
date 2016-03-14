package com.byos.yohann.fanfic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullPageActivity extends AppCompatActivity implements GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    public static final String PAGE_ACTUELLE = "page_actuelle";
    public static final String STORY_ID = "story_id";
    public static final String STORY_TITLE = "story_title";
    public static final String TOTAL_PAGES = "total_pages";
    private static final java.lang.String PAGE_CONTENT = "page_content";
    private static final String LISTE_PAGES = "liste_pages";
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private TextView mPagination;
    private TextView mPageContent;
    private int nbTotalPages;
    private int initialPageLocation;
    private ArrayList<Page> mListePage;
    private int mPageActuelle;
    private ProgressBar loader;
    private GestureDetector mDetector;
    private int mStoryId;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullpage);
        Intent intent = getIntent();
        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
        mPagination = (TextView) findViewById(R.id.pagination);
        mPageContent = (TextView) findViewById(R.id.page_content);
        loader = (ProgressBar) findViewById(R.id.page_loader);
        nbTotalPages = intent.getIntExtra(TOTAL_PAGES, 1);
        mStoryId = intent.getIntExtra(STORY_ID, 0);
        initialPageLocation = intent.getIntExtra(PAGE_ACTUELLE, 1);

        getSupportActionBar().setTitle(intent.getStringExtra(STORY_TITLE));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDetector = new GestureDetector(this, this);
        mDetector.setOnDoubleTapListener(this);
        if(savedInstanceState == null) {

            mPageActuelle = initialPageLocation;
            nbTotalPages = intent.getIntExtra(TOTAL_PAGES, 1);
            loader.setVisibility(View.VISIBLE);
            mContentView.setVisibility(View.INVISIBLE);
            QueryPageTask queryPageTask = new QueryPageTask(mStoryId, QueryPageTask.RECUPERER_PAGES);
            queryPageTask.execute();
        } else {
            mListePage = savedInstanceState.getParcelableArrayList(LISTE_PAGES);
            mPageActuelle = savedInstanceState.getInt(PAGE_ACTUELLE);
            mPagination.setText(mPageActuelle+" / "+nbTotalPages);
            mPageContent.setText(savedInstanceState.getString(PAGE_CONTENT));

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PAGE_ACTUELLE, mPageActuelle);
        outState.putString(PAGE_CONTENT, mPageContent.getText().toString());
        outState.putParcelableArrayList(LISTE_PAGES, mListePage);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide(50);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // Left to Right swipe action (page suivante)
                    if (x2 > x1) {
                        previousPage();
                    } else {
                        nextPage();
                    }

                }
                break;
        }

        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void previousPage() {

        if(mPageActuelle > 1) {

            mPageActuelle--;
            mPagination.setText(mPageActuelle+" / "+nbTotalPages);
            mPageContent.setText(mListePage.get(mPageActuelle-1).getText());
        }
    }

    private void nextPage() {

        if(mPageActuelle < nbTotalPages) {

            mPageActuelle++;
            mPagination.setText(mPageActuelle+" / "+nbTotalPages);
            mPageContent.setText(mListePage.get(mPageActuelle-1).getText());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        return this.onTouchEvent(ev);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;
        mContentView.setFitsSystemWindows(false);
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
        mContentView.setFitsSystemWindows(true);

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

        toggle();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public class QueryPageTask extends AsyncTask<Void, Void, ArrayList<Page>> {

        private static final int RECUPERER_PAGES = 1;
        private static final int UPDATE_PAGE = 2;
        private int storyId;
        private int action;

        public QueryPageTask(int storyId, int action) {

            this.storyId = storyId;
            this.action = action;

        }

        @Override
        protected ArrayList<Page> doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String datas = null;

            //On récupère les informations de l'utilisateur
            SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.USERFILE, Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt(MainActivity.USERID, 0);
            String userMail = sharedPreferences.getString(MainActivity.USEREMAIL, MainActivity.USEREMAIL);
            String userPass = sharedPreferences.getString(MainActivity.USERPASS, MainActivity.USERPASS);

            try {
                String encoded = Base64.encodeToString((userMail + ":" + userPass).getBytes("UTF-8"), Base64.NO_WRAP);
                URL url = null;
                switch (action) {

                    case RECUPERER_PAGES:
                        url = new URL("http://ycaillon.com/fanficAPI/public/api/v1/story/"+ storyId);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Authorization", "Basic "+encoded);
                        urlConnection.setRequestMethod("GET");
                        break;

                    case UPDATE_PAGE:
                        url = new URL("http://ycaillon.com/fanficAPI/public/api/v1/user/"+userId+"?follow="+ storyId+"&page="+mPageActuelle);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Authorization", "Basic "+encoded);
                        urlConnection.setRequestMethod("PUT");
                        break;

                    default:
                }


                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                datas = buffer.toString();


            } catch (Exception e) {

                Log.e("StoryListFragment", "Error ", e);
                return null;

            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {

                    try {

                        reader.close();

                    } catch (final IOException e) {

                        Log.e("StoryListFragment", "Error closing stream", e);
                    }
                }
            }
            ArrayList<Page> listePage = null;

            if (action == RECUPERER_PAGES)
                listePage = JsonApiToData.getPages(datas);

            return listePage;
        }

        @Override
        protected void onPostExecute(ArrayList<Page> pages) {


            if(action == RECUPERER_PAGES) {

                if(pages == null) {
                    Toast.makeText(getApplicationContext(), R.string.error_page, Toast.LENGTH_SHORT).show();
                    finish();
                }
                mListePage = pages;
                mPagination.setText(mPageActuelle+" / "+nbTotalPages);
                mPageContent.setText(pages.get(mPageActuelle-1).getText());
                loader.setVisibility(View.INVISIBLE);
                mContentView.setVisibility(View.VISIBLE);

            } else {

                Intent resultIntent = new Intent();
                resultIntent.putExtra(PAGE_ACTUELLE, mPageActuelle);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }

        }
    }

    @Override
    public void onBackPressed() {

        if(initialPageLocation != mPageActuelle) {
            QueryPageTask queryPageTask = new QueryPageTask(mStoryId, QueryPageTask.UPDATE_PAGE);
            queryPageTask.execute();
            Snackbar.make(mContentView, getString(R.string.sauvegarde), Snackbar.LENGTH_LONG).show();
        } else {

          super.onBackPressed();
        }

    }
}
