package com.byos.yohann.fanfic;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yohann on 10/02/2016.
 */
public class Story implements Parcelable {

    private int id;
    private int nbTotalPages;
    private String titre;
    private String auteur;
    private int pageActuelle;
    private String reference;

    public Story(int id, int nbTotalPages, String titre, String auteur, int pageActuelle, String ref){
        this.id = id;
        this.nbTotalPages = nbTotalPages;
        this.titre = titre;
        this.auteur = auteur;
        this.pageActuelle = pageActuelle;
        this.reference = ref;

    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getPageActuelle() {
        return pageActuelle;
    }

    public void setPageActuelle(int pageActuelle) {
        this.pageActuelle = pageActuelle;
    }

    public int getNbTotalPages() {
        return nbTotalPages;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setNbTotalPages(int nbPages) {
        this.nbTotalPages = nbPages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeInt(nbTotalPages);
        dest.writeString(titre);
        dest.writeString(auteur);
        dest.writeInt(pageActuelle);
        dest.writeString(reference);

    }
    public static final Creator<Story> CREATOR = new Creator<Story>()
    {
        @Override
        public Story createFromParcel(Parcel source)
        {
            return new Story(source);
        }

        @Override
        public Story[] newArray(int size)
        {
            return new Story[size];
        }
    };

    public Story(Parcel in) {

        this.id = in.readInt();
        this.nbTotalPages = in.readInt();
        this.titre = in.readString();
        this.auteur = in.readString();
        this.pageActuelle = in.readInt();
        this.reference = in.readString();
    }

    public int getId() {
        return id;
    }
}
