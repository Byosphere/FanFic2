package com.byos.yohann.fanfic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewPageActivity extends AppCompatActivity {

    private static final String SAVE_OK = "save_ok";
    public static final String STORY_ID = "story_id";
    protected TextView laNouvellePage;
    protected SavePageTask mSavePageTask;
    protected int storyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        laNouvellePage = (TextView) findViewById(R.id.new_page_text);
        if(savedInstanceState == null) {
            storyId = getIntent().getIntExtra(STORY_ID, -1);
        } else {

            storyId =savedInstanceState.getInt(STORY_ID);
        }

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STORY_ID, storyId);
    }

    private void checkChamps() {

        if(TextUtils.isEmpty(laNouvellePage.getText())) {

            laNouvellePage.setError(getString(R.string.error_field_required));
            laNouvellePage.requestFocus();

        } else {

            mSavePageTask = new SavePageTask(laNouvellePage.getText().toString());
            mSavePageTask.execute();
            Toast.makeText(this, getString(R.string.enregistrer), Toast.LENGTH_SHORT).show();
        }
    }


    private class SavePageTask extends AsyncTask<Void, Void, String> {

        private String mPage;

        public SavePageTask(String page) {

            this.mPage = page;
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
                builder.path("fanficAPI/public/api/v1/story/"+storyId+"/page");
                builder.appendQueryParameter("texte", mPage);
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

                Log.e(NewPageActivity.class.getSimpleName(), "Error ", e);
                return null;

            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {

                    try {

                        reader.close();

                    } catch (final IOException e) {

                        Log.e(NewPageActivity.class.getSimpleName(), "Error closing stream", e);
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

                    mSavePageTask = null;
                    Intent intent = getIntent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }

            } catch (JSONException e) {
                Log.e(NewPageActivity.class.getSimpleName(), "Error reading json", e);
            }
        }
    }
}
