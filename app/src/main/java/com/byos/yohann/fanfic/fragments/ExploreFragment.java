package com.byos.yohann.fanfic.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
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

import com.byos.yohann.fanfic.JsonApiToData;
import com.byos.yohann.fanfic.MainActivity;
import com.byos.yohann.fanfic.R;
import com.byos.yohann.fanfic.Story;
import com.byos.yohann.fanfic.StoryExploreAdapter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ExploreFragment extends Fragment {

    private static final String TAG = ExploreFragment.class.getSimpleName();
    protected RecyclerView mRecyclerView;
    protected StoryExploreAdapter storyExploreAdapter;
    protected ArrayList<Story> data;
    protected ExploreManagerTask exploreManagerTask;
    protected SetFollowersTask setFollowersTask;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected View rootView;
    protected ProgressBar loader;
    protected TextView textLine;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {

            data = new ArrayList<>();
            exploreManagerTask = new ExploreManagerTask();
            exploreManagerTask.execute();
        } else {
            data = savedInstanceState.getParcelableArrayList(TAG);
        }
        storyExploreAdapter = new StoryExploreAdapter(getActivity(), this, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        rootView.setTag(TAG);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_view_explore);
        loader = (ProgressBar) rootView.findViewById(R.id.loader_explore);
        textLine = (TextView) rootView.findViewById(R.id.no_content_explore);
        textLine.setVisibility(View.INVISIBLE);
        if(savedInstanceState == null)
            loader.setVisibility(View.VISIBLE);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(storyExploreAdapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList(TAG, data);
    }

    public void onClickStory(Story story) {

        setFollowersTask = new SetFollowersTask(story);
        setFollowersTask.execute();
    }


    private class ExploreManagerTask extends AsyncTask<Void,Void,String> {


        @Override
        protected String doInBackground(Void... params) {

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
                URL url = new URL("http://ycaillon.com/fanficAPI/public/api/v1/story/explore");
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

                Log.e(ExploreFragment.class.getSimpleName(), "Error ", e);
                return null;

            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {

                    try {

                        reader.close();

                    } catch (final IOException e) {

                        Log.e(ExploreFragment.class.getSimpleName(), "Error closing stream", e);
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

                data = JsonApiToData.getExploreStories(result, getActivity());
                if(data == null) {

                    Log.e(ExploreFragment.class.getSimpleName(), "Une erreur est survenue en parsant le JSON");

                } else {
                    storyExploreAdapter.swap(data);
                }

            }
        }
    }

    private class SetFollowersTask extends AsyncTask<Void, Void, String> {

        private Story mStory;

        public SetFollowersTask(Story s) {

            this.mStory = s;
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String storyListJson = null;
            //On récupère les informations de l'utilisateur
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.USERFILE, Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt(MainActivity.USERID, 0);
            String userMail = sharedPreferences.getString(MainActivity.USEREMAIL, MainActivity.USEREMAIL);
            String userPass = sharedPreferences.getString(MainActivity.USERPASS, MainActivity.USERPASS);
            int page = 1; //initialise à la page 1

            if (mStory.isFollowed()) // si la story est déjà followed, on l'enlève
                page = -1;

            try {

                String encoded = Base64.encodeToString((userMail + ":" + userPass).getBytes("UTF-8"), Base64.NO_WRAP);
                URL url = new URL("http://ycaillon.com/fanficAPI/public/api/v1/user/"+userId+"?follow="+mStory.getId()+"&page="+page);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", "Basic "+encoded);
                urlConnection.setRequestMethod("PUT");
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

                Log.e(ExploreFragment.class.getSimpleName(), "Error ", e);
                return null;

            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {

                    try {

                        reader.close();

                    } catch (final IOException e) {

                        Log.e(ExploreFragment.class.getSimpleName(), "Error closing stream", e);
                    }
                }
            }

            return storyListJson;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                if(!new JSONObject(s).getBoolean("error")) {

                    mStory.setFollowed(!mStory.isFollowed());
                    storyExploreAdapter.notifyDataSetChanged();
                    if (mStory.isFollowed()) {

                        Snackbar.make(rootView, getString(R.string.follow), Snackbar.LENGTH_SHORT).show();

                    } else {

                        Snackbar.make(rootView, getString(R.string.unFollow), Snackbar.LENGTH_SHORT).show();
                    }

                } else {

                    throw new Exception("Une erreur est survenue");
                }

            } catch (Exception e) {


            }

        }
    }
}
