package com.byos.yohann.fanfic;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.byos.yohann.fanfic.fragments.StoryWriteFragment;

import java.util.ArrayList;

/**
 * Created by Yohann on 04/03/2016.
 */
public class StoryWriteAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    public StoryWriteAdapter(Activity activity, StoryWriteFragment storyWriteFragment, ArrayList<Story> data) {
        super();
    }

    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(StoryAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
