package com.byos.yohann.fanfic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import android.net.Uri.Builder;
import java.net.URL;

public class EditStoryActivity extends AppCompatActivity {

    private static final String SAVE_OK = "save_ok";
    protected TextView titre;
    protected TextView reference;
    protected TextView firstPage;
    protected SendStoryTask mSendStoryTask;
    protected SendPageTask mSendPageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titre = (TextView) findViewById(R.id.new_story_titre);
        reference = (TextView) findViewById(R.id.new_reference);
        firstPage = (TextView) findViewById(R.id.full_new_text);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_send:
                checkChamps();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_edit_story, menu);
        return true;
    }


    private void checkChamps() {

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(titre.getText())) {

            titre.setError(getString(R.string.error_field_required));
            focusView = titre;
            cancel = true;
        }

        if(TextUtils.isEmpty(reference.getText())) {

            reference.setError(getString(R.string.error_field_required));
            focusView = reference;
            cancel = true;
        }

        if(TextUtils.isEmpty(firstPage.getText())) {

            firstPage.setError(getString(R.string.error_field_required));
            focusView = firstPage;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {

            Log.d("info", titre.getText().toString());
            mSendStoryTask = new SendStoryTask(titre.getText().toString(), reference.getText().toString(), firstPage.getText().toString());
            mSendStoryTask.execute();

        }
    }


    private class SendStoryTask extends AsyncTask<Void, Void, String> {

        private String mTitre;
        private String mReference;
        private String mFirstPage;

        public SendStoryTask(String titre, String reference, String firstPage) {

            this.mReference = reference;
            this.mTitre = titre;
            this.mFirstPage = firstPage;
        }

        @Override
        protected String doInBackground(Void... params) {

            SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.USERFILE, Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt(MainActivity.USERID, 0);
            String userMail = sharedPreferences.getString(MainActivity.USEREMAIL, MainActivity.USEREMAIL);
            String userPass = sharedPreferences.getString(MainActivity.USERPASS, MainActivity.USERPASS);
            BufferedReader reader = null;
            String result = null;
            HttpURLConnection urlConnection = null;

            try {
                String encoded = Base64.encodeToString((userMail + ":" + userPass).getBytes("UTF-8"), Base64.NO_WRAP);
                Builder builder = new Builder();
                builder.scheme("http");
                builder.authority("ycaillon.com");
                builder.path("fanficAPI/public/api/v1/story");
                builder.appendQueryParameter("titre", mTitre);
                builder.appendQueryParameter("ref", mReference);
                URL url = new URL(builder.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", "Basic "+encoded);
                urlConnection.setRequestMethod("POST");
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
                result = buffer.toString();


            } catch (Exception e) {

                Log.e(EditStoryActivity.class.getSimpleName(), "Error ", e);
                return null;

            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {

                    try {

                        reader.close();

                    } catch (final IOException e) {

                        Log.e(EditStoryActivity.class.getSimpleName(), "Error closing stream", e);
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            try {

                if (new JSONObject(s).getBoolean("error")) {

                    Log.e("error", s);
                } else {

                    mSendStoryTask = null;
                    mSendPageTask = new SendPageTask(mFirstPage, new JSONObject(s).getJSONObject("story").getInt("id"));
                    mSendPageTask.execute();
                }

            } catch (JSONException e) {
                Log.e(EditStoryActivity.class.getSimpleName(), "Error reading json", e);
            }
        }
    }

    protected class SendPageTask extends AsyncTask<Void, Void, String> {

        private String mFirstPage;
        private int mStoryId;

        public SendPageTask(String mFirstPage, int storyId) {

            this.mFirstPage = mFirstPage;
            this.mStoryId = storyId;
        }


        @Override
        protected String doInBackground(Void... params) {

            SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.USERFILE, Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt(MainActivity.USERID, 0);
            String userMail = sharedPreferences.getString(MainActivity.USEREMAIL, MainActivity.USEREMAIL);
            String userPass = sharedPreferences.getString(MainActivity.USERPASS, MainActivity.USERPASS);
            BufferedReader reader = null;
            String result = null;
            HttpURLConnection urlConnection = null;

            try {
                String encoded = Base64.encodeToString((userMail + ":" + userPass).getBytes("UTF-8"), Base64.NO_WRAP);
                Builder builder = new Builder();
                builder.scheme("http");
                builder.authority("ycaillon.com");
                builder.path("fanficAPI/public/api/v1/story/" + mStoryId+"/page");
                builder.appendQueryParameter("texte", mFirstPage);
                URL url = new URL(builder.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", "Basic "+encoded);
                urlConnection.setRequestMethod("POST");
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
                result = buffer.toString();


            } catch (Exception e) {

                Log.e(EditStoryActivity.class.getSimpleName(), "Error ", e);
                return null;

            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {

                    try {

                        reader.close();

                    } catch (final IOException e) {

                        Log.e(EditStoryActivity.class.getSimpleName(), "Error closing stream", e);
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            try {

                if (new JSONObject(s).getBoolean("error")) {

                    Log.e("error", s);
                    // TODO: 14/03/2016 error
                } else {

                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }

            } catch (JSONException e) {
                Log.e(EditStoryActivity.class.getSimpleName(), "Error reading json", e);
            }
        }
    }
}
