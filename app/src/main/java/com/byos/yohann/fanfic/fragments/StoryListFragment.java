package com.byos.yohann.fanfic.fragments;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
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

import com.byos.yohann.fanfic.JsonApiToData;
import com.byos.yohann.fanfic.MainActivity;
import com.byos.yohann.fanfic.R;
import com.byos.yohann.fanfic.Story;
import com.byos.yohann.fanfic.StoryAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


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

    public StoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        data = new ArrayList<>();
        storyAdapter = new StoryAdapter(getActivity(), this, data);
        StoryManagerTask stm = new StoryManagerTask(this);
        stm.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_story_list, container, false);
        rootView.setTag(TAG);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_view_story);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
        mRecyclerView.setAdapter(storyAdapter);
        ProgressBar loader = (ProgressBar) rootView.findViewById(R.id.loader_story_list);
        loader.setVisibility(View.VISIBLE);
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

    }

    public void deleteStory(Story story) {

        data.remove(story);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
        Toast.makeText(getActivity(), story.getTitre() + " " + getString(R.string.deleted), Toast.LENGTH_SHORT).show();
    }


    public class StoryManagerTask extends AsyncTask<String, Void, String> {

        private final String TAG = StoryManagerTask.class.getSimpleName();
        private Fragment fragment;

        public StoryManagerTask(Fragment fragment) {

            this.fragment = fragment;
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String storyListJson = null;

            try {

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.USERFILE, Context.MODE_PRIVATE);
                int userId = sharedPreferences.getInt(MainActivity.USERID, 0);
                String userMail = sharedPreferences.getString(MainActivity.USEREMAIL, MainActivity.USEREMAIL);
                String userPass = sharedPreferences.getString(MainActivity.USERPASS, MainActivity.USERPASS);

                URL url = new URL("http://ycaillon.com/fanficAPI/public/api/v1/user/"+userId);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                String encoded = Base64.encodeToString((userMail+":"+userPass).getBytes("UTF-8"), Base64.NO_WRAP);
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
            return storyListJson;

        }
        @Override
        protected void onPostExecute(String result) {

            (fragment.getActivity().findViewById(R.id.loader_story_list)).setVisibility(View.INVISIBLE);
            TextView tv = (TextView) (fragment.getActivity().findViewById(R.id.no_content));
            if (result == null) {

                tv.setText(getString(R.string.no_internet));
                tv.setVisibility(View.VISIBLE);

            } else {

                ArrayList<Story> data = JsonApiToData.getFollowedStories(result);
                if(data.size()>0) {

                    storyAdapter.swap(data);

                } else {

                    tv.setText(getString(R.string.no_content));
                    tv.setVisibility(View.VISIBLE);
                }

            }

        }

    }
    // --------------
}
