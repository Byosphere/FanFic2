package com.byos.yohann.fanfic.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byos.yohann.fanfic.R;
import com.byos.yohann.fanfic.Story;
import com.byos.yohann.fanfic.JsonApiToData;
import com.byos.yohann.fanfic.StoryWriteAdapter;

import java.util.ArrayList;

public class StoryWriteFragment extends Fragment {


    private static final String TAG = StoryWriteFragment.class.getSimpleName();
    protected RecyclerView mRecyclerView;
    protected StoryWriteAdapter storyAdapter;
    protected ArrayList<Story> data;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected View rootView;

    public StoryWriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_story_write, container, false);
        rootView.setTag(TAG);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_view_write);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
        // Inflate the layout for this fragment

        TextView texte = (TextView) rootView.findViewById(R.id.no_content);
        texte.setVisibility(View.INVISIBLE);
        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        storyAdapter = new StoryWriteAdapter(getActivity(), this, data);

        mRecyclerView.setAdapter(storyAdapter);
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_story_write, container, false);
    }

}
