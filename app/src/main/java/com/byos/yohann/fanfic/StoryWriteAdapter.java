package com.byos.yohann.fanfic;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byos.yohann.fanfic.fragments.StoryListFragment;
import com.byos.yohann.fanfic.fragments.StoryWriteFragment;

import java.util.ArrayList;

/**
 * Created by Yohann on 04/03/2016.
 */
public class StoryWriteAdapter extends RecyclerView.Adapter<StoryWriteAdapter.ViewHolder> {

    private ArrayList<Story> mDataSet;
    private Activity activity;
    private StoryWriteFragment storyWriteFragment;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView titre;
        public TextView pages;
        public RelativeLayout conteneur;

        public ViewHolder(View v) {
            super(v);

            v.setOnClickListener(this);
            titre = (TextView) v.findViewById(R.id.titre_writings);
            pages = (TextView) v.findViewById(R.id.pages_write);
            conteneur = (RelativeLayout) v.findViewById(R.id.row_writings);

        }

        public TextView getTitre() {return titre;}
        public TextView getPages() {return pages;}
        public RelativeLayout getConteneur() {return conteneur;}

        @Override
        public void onClick(View v) {

            storyWriteFragment.onClickStory(mDataSet.get(getAdapterPosition()).getId());

        }
    }

    public StoryWriteAdapter(Activity activity, StoryWriteFragment storyWriteFragment, ArrayList<Story> data) {

        this.activity = activity;
        this.storyWriteFragment = storyWriteFragment;
        this.mDataSet = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_writings, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.getTitre().setText(mDataSet.get(position).getTitre());
        holder.getPages().setText(mDataSet.get(position).getNbTotalPages()+" "+activity.getResources().getString(R.string.pages));

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void swap(ArrayList<Story> datas){
        mDataSet.clear();
        mDataSet.addAll(datas);
        notifyDataSetChanged();
    }
}
