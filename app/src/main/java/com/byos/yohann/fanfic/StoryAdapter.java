package com.byos.yohann.fanfic;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.ArrayList;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private static final String TAG = StoryAdapter.class.getSimpleName();

    private ArrayList<Story> mDataSet;
    private Activity activity;
    private StoryListFragment storyListFragment;


    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        public TextView titre;
        public TextView pages;
        public TextView auteur;
        public TextView pagesRestantes;
        public ProgressBar progressBar;
        public TextView reference;
        public RelativeLayout conteneur;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(this);
            v.setOnCreateContextMenuListener(this);
            titre = (TextView) v.findViewById(R.id.titre);
            pages = (TextView) v.findViewById(R.id.pages);
            pagesRestantes = (TextView) v.findViewById(R.id.pagesRestantes);
            auteur = (TextView) v.findViewById(R.id.auteur);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            reference = (TextView) v.findViewById(R.id.reference);
            conteneur = (RelativeLayout) v.findViewById(R.id.row_story);

        }

        public TextView getTitre() {
            return titre;
        }
        public TextView getPages() {
            return pages;
        }
        public TextView getAuteur() {
            return auteur;
        }
        public TextView getPagesRestantes() {
            return pagesRestantes;
        }
        public ProgressBar getProgressBar() {
            return progressBar;
        }
        public TextView getReference() {return reference;}
        public RelativeLayout getConteneur() {return conteneur;}

        @Override
        public void onClick(View v) {

            storyListFragment.onClickStory(mDataSet.get(getAdapterPosition()));
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuInflater inflater = activity.getMenuInflater();
            inflater.inflate(R.menu.context_story_menu, menu);
            menu.setHeaderTitle(R.string.context_menu_head);
            for (int i = 0; i<menu.size();i++) {

                menu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.context_delete:
                                storyListFragment.deleteStory(mDataSet.get(getAdapterPosition()));
                                break;
                        }

                        return false;
                    }
                });
            }
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public StoryAdapter(Activity activity, StoryListFragment storyListFragment, ArrayList<Story> dataSet) {

        this.activity = activity;
        this.storyListFragment = storyListFragment;
        mDataSet = dataSet;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_story, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        if(mDataSet.get(position).getPageActuelle() == mDataSet.get(position).getNbTotalPages()) {
            viewHolder.getConteneur().setAlpha((float) 0.3);
        } else {
            viewHolder.getConteneur().setAlpha((float) 1);
        }

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getTitre().setText(mDataSet.get(position).getTitre());

        viewHolder.getPages().setText(mDataSet.get(position).getNbTotalPages()+" "+activity.getResources().getString(R.string.pages));
        viewHolder.getAuteur().setText(mDataSet.get(position).getAuteur());

        viewHolder.getProgressBar().setProgress(mDataSet.get(position).getPageActuelle());
        viewHolder.getProgressBar().setMax(mDataSet.get(position).getNbTotalPages());

        int pagesR = mDataSet.get(position).getNbTotalPages() - mDataSet.get(position).getPageActuelle();
        viewHolder.getPagesRestantes().setText(pagesR+" "+activity.getResources().getString(R.string.pages));

        viewHolder.getReference().setText(mDataSet.get(position).getReference());


    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
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
