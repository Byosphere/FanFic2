package com.byos.yohann.fanfic.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byos.yohann.fanfic.FullPageActivity;
import com.byos.yohann.fanfic.JsonApiToData;
import com.byos.yohann.fanfic.MainActivity;
import com.byos.yohann.fanfic.Page;
import com.byos.yohann.fanfic.R;
import com.byos.yohann.fanfic.Story;
import com.byos.yohann.fanfic.StoryAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class StoryListFragment extends Fragment {

    private static final String TAG = StoryListFragment.class.getSimpleName();
    protected RecyclerView mRecyclerView;
    protected StoryAdapter storyAdapter;
    protected ArrayList<Story> data;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected View rootView;
    protected StoryManagerTask storyManagerTask;
    protected Story currentStory;

    public StoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {

            data = new ArrayList<>();
            storyAdapter = new StoryAdapter(getActivity(), this, data);
            storyManagerTask = new StoryManagerTask(this);
            storyManagerTask.execute(StoryManagerTask.GET_USER_FOLLOWED);
        } else {
            data = savedInstanceState.getParcelableArrayList(TAG);
            storyAdapter = new StoryAdapter(getActivity(), this, data);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_story_list, container, false);
        rootView.setTag(TAG);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_view_story);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
        mRecyclerView.setAdapter(storyAdapter);
        if (savedInstanceState == null) {

            ProgressBar loader = (ProgressBar) rootView.findViewById(R.id.loader_story_list);
            loader.setVisibility(View.VISIBLE);
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_story_menu, menu);
    }

    public void onClickStory(Story story) {

        currentStory = story;
        Intent intent = new Intent(getActivity(), FullPageActivity.class);
        intent.putExtra(FullPageActivity.PAGE_ACTUELLE, story.getPageActuelle());
        intent.putExtra(FullPageActivity.TOTAL_PAGES, story.getNbTotalPages());
        intent.putExtra(FullPageActivity.STORY_ID, story.getId());
        intent.putExtra(FullPageActivity.STORY_TITLE, story.getTitre());
        startActivityForResult(intent, 0);

    }

    public void deleteStory(Story story) {

        currentStory = story;
        storyManagerTask = new StoryManagerTask(this);
        storyManagerTask.execute(StoryManagerTask.DELETE_STORY_FOLLOWED, story.getId()+"");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList(TAG, data);
    }

    public class StoryManagerTask extends AsyncTask<String, Void, HashMap<String, String>> {

        private final String TAG = StoryManagerTask.class.getSimpleName();
        private static final String GET_USER_FOLLOWED = "get_user_followed";
        private static final String DELETE_STORY_FOLLOWED = "delete_story_followed";
        //private static final String GET_CURRENT_PAGE = "get_current_page";
        private Fragment fragment;

        public StoryManagerTask(Fragment fragment) {

            this.fragment = fragment;
        }

        @Override
        protected HashMap<String, String> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String storyListJson = null;

            //On récupère les informations de l'utilisateur
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.USERFILE, Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt(MainActivity.USERID, 0);
            String userMail = sharedPreferences.getString(MainActivity.USEREMAIL, MainActivity.USEREMAIL);
            String userPass = sharedPreferences.getString(MainActivity.USERPASS, MainActivity.USERPASS);

            try {
                String encoded = Base64.encodeToString((userMail+":"+userPass).getBytes("UTF-8"), Base64.NO_WRAP);
                URL url = null;


                switch (params[0]) {

                    case GET_USER_FOLLOWED:

                        url = new URL("http://ycaillon.com/fanficAPI/public/api/v1/user/"+userId);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Authorization", "Basic "+encoded);
                        urlConnection.setRequestMethod("GET");
                        break;

                    case DELETE_STORY_FOLLOWED:

                        url = new URL("http://ycaillon.com/fanficAPI/public/api/v1/user/"+userId+"?follow="+params[1]+"&page=-1");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Authorization", "Basic "+encoded);
                        urlConnection.setRequestMethod("PUT");
                        break;
                    /*
                    case GET_CURRENT_PAGE:

                        url = new URL("http://ycaillon.com/fanficAPI/public/api/v1/story/"+params[1]);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Authorization", "Basic "+encoded);
                        urlConnection.setRequestMethod("GET");
                        break;
                    */
                    default:
                        throw new Exception("Action de connexion inconnue");
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
                storyListJson = buffer.toString();


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
            HashMap<String, String> reponse = new HashMap<String, String>();
            reponse.put(params[0], storyListJson);
            return reponse;

        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {

            storyManagerTask = null;
            TextView tv = (TextView) (fragment.getActivity().findViewById(R.id.no_content));
            if(result == null) {

                tv.setText(getString(R.string.no_internet));
                tv.setVisibility(View.VISIBLE);
                (fragment.getActivity().findViewById(R.id.loader_story_list)).setVisibility(View.INVISIBLE);
                return;
            }
            (fragment.getActivity().findViewById(R.id.loader_story_list)).setVisibility(View.INVISIBLE);
            if(result.containsKey(GET_USER_FOLLOWED)) {

                ArrayList<Story> data = JsonApiToData.getFollowedStories(result.get(GET_USER_FOLLOWED));
                if(data.size()>0) {

                    storyAdapter.swap(data);

                } else {

                    tv.setText(getString(R.string.no_content));
                    tv.setVisibility(View.VISIBLE);
                }

            } else if(result.containsKey(DELETE_STORY_FOLLOWED)){

                try {

                    Snackbar snackbar = Snackbar.make(rootView, new JSONObject(result.get(DELETE_STORY_FOLLOWED)).getString("message"), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    if(!(new JSONObject(result.get(DELETE_STORY_FOLLOWED)).getBoolean("error"))) {

                        data.remove(currentStory);
                        storyAdapter.notifyDataSetChanged();

                    }

                } catch (JSONException e) {


                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_parsing), Toast.LENGTH_SHORT);
                    toast.show();

                }

            }

        }

        @Override
        protected void onCancelled() {

            storyManagerTask = null;
            (fragment.getActivity().findViewById(R.id.loader_story_list)).setVisibility(View.INVISIBLE);
        }
    }
    // --------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            currentStory.setPageActuelle(data.getIntExtra(FullPageActivity.PAGE_ACTUELLE, currentStory.getPageActuelle()));
            storyAdapter.notifyDataSetChanged();
        }


    }
}
