package com.byos.yohann.fanfic.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.byos.yohann.fanfic.EditStoryActivity;
import com.byos.yohann.fanfic.MainActivity;
import com.byos.yohann.fanfic.NewPageActivity;
import com.byos.yohann.fanfic.R;
import com.byos.yohann.fanfic.Story;
import com.byos.yohann.fanfic.JsonApiToData;
import com.byos.yohann.fanfic.StoryWriteAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class StoryWriteFragment extends Fragment {


    private static final String TAG = StoryWriteFragment.class.getSimpleName();
    private static final int UPDATE_LIST_REQUEST = 1;
    protected RecyclerView mRecyclerView;
    protected StoryWriteAdapter storyAdapter;
    protected ArrayList<Story> data;
    protected StoryManagerTask storyManagerTask;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected View rootView;
    protected ProgressBar loader;
    protected TextView textLine;

    public StoryWriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {

            data = new ArrayList<>();
            storyManagerTask = new StoryManagerTask();
            storyManagerTask.execute();
        } else {
            data = savedInstanceState.getParcelableArrayList(TAG);
        }
        storyAdapter = new StoryWriteAdapter(getActivity(), this, data);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_story_write, container, false);
        rootView.setTag(TAG);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_view_write);

        loader = (ProgressBar) rootView.findViewById(R.id.loader_write);
        textLine = (TextView) rootView.findViewById(R.id.no_content_write);
        textLine.setVisibility(View.INVISIBLE);
        if(savedInstanceState == null)
            loader.setVisibility(View.VISIBLE);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(storyAdapter);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), EditStoryActivity.class);
                startActivityForResult(intent, UPDATE_LIST_REQUEST);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList(TAG, data);
    }

    public void onClickStory(int id) {

        Intent intent = new Intent(getActivity(), NewPageActivity.class);
        intent.putExtra(NewPageActivity.STORY_ID, id);
        startActivityForResult(intent, UPDATE_LIST_REQUEST);
    }


    private class StoryManagerTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String storyListJson = null;
            //On récupère les informations de l'utilisateur
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.USERFILE, Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt(MainActivity.USERID, 0);
            String userMail = sharedPreferences.getString(MainActivity.USEREMAIL, MainActivity.USEREMAIL);
            String userPass = sharedPreferences.getString(MainActivity.USERPASS, MainActivity.USERPASS);

            try {
                String encoded = Base64.encodeToString((userMail + ":" + userPass).getBytes("UTF-8"), Base64.NO_WRAP);
                URL url = new URL("http://ycaillon.com/fanficAPI/public/api/v1/user/"+userId);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", "Basic "+encoded);
                urlConnection.setRequestMethod("GET");
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
                storyListJson = buffer.toString();


            } catch (Exception e) {

                Log.e("StoryWriteFragment", "Error ", e);
                return null;

            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {

                    try {

                        reader.close();

                    } catch (final IOException e) {

                        Log.e("StoryWriteFragment", "Error closing stream", e);
                    }
                }
            }

            return storyListJson;

        }

        @Override
        protected void onPostExecute(String result) {

            loader.setVisibility(View.INVISIBLE);

            if(result == null) {

                textLine.setText(getString(R.string.no_internet));
                textLine.setVisibility(View.VISIBLE);
                return;
            } else {

                data = JsonApiToData.getUserStories(result);
                storyAdapter.swap(data);
                if (data.size() == 0) {

                    textLine.setText(getString(R.string.no_content_write));
                    textLine.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            storyManagerTask = new StoryManagerTask();
            storyManagerTask.execute();
        }


    }
}
