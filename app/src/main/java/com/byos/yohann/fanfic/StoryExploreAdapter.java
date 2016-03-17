package com.byos.yohann.fanfic;

import android.app.Activity;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byos.yohann.fanfic.fragments.ExploreFragment;
import com.byos.yohann.fanfic.fragments.StoryWriteFragment;

import java.util.ArrayList;

/**
 * Created by Yohann on 15/03/2016.
 */
public class StoryExploreAdapter extends RecyclerView.Adapter<StoryExploreAdapter.ViewHolder> {


    private ArrayList<Story> mDataSet;
    private Activity activity;
    private ExploreFragment exploreFragment;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView titre;
        private TextView pagesTotal;
        private ImageView toogleFav;
        private TextView auteur;
        private TextView reference;
        private RelativeLayout container;

        public ViewHolder(View v) {
            super(v);

            v.setOnClickListener(this);
            toogleFav = (ImageView) v.findViewById(R.id.toggle_fav);
            titre = (TextView) v.findViewById(R.id.titre_explore);
            pagesTotal = (TextView) v.findViewById(R.id.pages_explore);
            auteur = (TextView) v.findViewById(R.id.auteur_explore);
            reference = (TextView) v.findViewById(R.id.reference_explore);
            container = (RelativeLayout) v.findViewById(R.id.explore_row);

        }

        public TextView getTitre() {return titre;}
        public TextView getPages() {return pagesTotal;}
        public ImageView getToggleFav() {return toogleFav;}
        public TextView getAuteur() {return auteur;}
        public TextView getReference() {return reference;}
        public RelativeLayout getContainer() {return container;}

        @Override
        public void onClick(View v) {
            container.setClickable(false);
            container.setAlpha(new Float(0.4));
            exploreFragment.onClickStory(mDataSet.get(getAdapterPosition()));
        }
    }

    public StoryExploreAdapter(Activity activity, ExploreFragment exploreFragment, ArrayList<Story> data) {

        this.activity = activity;
        this.exploreFragment = exploreFragment;
        this.mDataSet = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_explore, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.getTitre().setText(mDataSet.get(position).getTitre());
        holder.getContainer().setClickable(true);
        holder.getContainer().setAlpha(new Float(1));
        if(mDataSet.get(position).isFollowed()) {
            holder.getToggleFav().setImageDrawable(activity.getDrawable(R.drawable.ic_fav));
        } else {
            holder.getToggleFav().setImageDrawable(activity.getDrawable(R.drawable.ic_fav_off));
        }

        holder.getAuteur().setText(mDataSet.get(position).getAuteur());
        holder.getReference().setText(mDataSet.get(position).getReference());
        holder.getPages().setText(mDataSet.get(position).getNbTotalPages() + " " + activity.getResources().getString(R.string.pages));

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
