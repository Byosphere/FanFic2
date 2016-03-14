package com.byos.yohann.fanfic;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Yohann on 17/02/2016.
 */
public class JsonApiToData {

    public JsonApiToData() {


    }

    public static User loginOrRegisterUser(String json) {

        try {

            boolean error = new JSONObject(json).getBoolean("error");

            if(!error) {
                JSONObject jsonUser = new JSONObject(json).getJSONObject("user");
                User user = new User(
                        jsonUser.getInt("id"),
                        jsonUser.getString("email"),
                        jsonUser.getString("name"),
                        new HashMap<Integer, String>()
                );
                Log.d("user", user.toString());
                return user;

            } else {
                String message = new JSONObject(json).getJSONArray("message").getString(0);
                HashMap<Integer, String> hm = new HashMap<Integer, String>();
                hm.put(User.ERROR, message);
                User user = new User(0, null, null, hm);
                return user;
            }

        } catch (Exception e) {

            Log.e("error", e.getMessage());
            HashMap<Integer, String> hm = new HashMap<Integer, String>();
            hm.put(User.ERROR, e.getMessage());
            User user = new User(0, null, null, hm);
            return user;
        }
    }

    public static ArrayList<Story> getUserStories(String json) {

        ArrayList<Story> data = new ArrayList<Story>();
        try {

            JSONArray jsonFollowed = new JSONObject(json).getJSONObject("user").getJSONArray("stories");

            for (int i = 0; i< jsonFollowed.length(); i++) {

                JSONObject storyJson = jsonFollowed.getJSONObject(i);
                data.add(new Story(
                        storyJson.getInt("id"),
                        storyJson.getInt("nbPages"),
                        storyJson.getString("titre"),
                        storyJson.getString("author"),
                        0,
                        storyJson.getString("reference")));

            }



        } catch (Exception e) {

            Log.e("error", e.getMessage());
        }

        return data;
    }

    public static ArrayList<Story> getFollowedStories(String json) {

        ArrayList<Story> data = new ArrayList<Story>();
        try {

            JSONArray jsonFollowed = new JSONObject(json).getJSONObject("user").getJSONArray("lectures");

            for (int i = 0; i< jsonFollowed.length(); i++) {

                JSONObject storyJson = jsonFollowed.getJSONObject(i);
                data.add(new Story(
                        storyJson.getInt("id"),
                        storyJson.getInt("nbPages"),
                        storyJson.getString("titre"),
                        storyJson.getString("author"),
                        storyJson.getJSONObject("pivot").getInt("pageActuelle"),
                        storyJson.getString("reference")));

            }



        } catch (Exception e) {

            Log.e("error", e.getMessage());
        }

        return data;
    }


    public static ArrayList<Page> getPages(String s) {

        ArrayList<Page> listePage = new ArrayList<Page>();
        try {
            JSONArray tabRaw = new JSONObject(s).getJSONArray("pages");

            for (int i= 0; i<tabRaw.length(); i++) {

                JSONObject unePage = (JSONObject) tabRaw.get(i);
                listePage.add(new Page(unePage.getInt("id"), unePage.getString("texte"), unePage.getInt("story_id")));
            }

        }catch (Exception e) {


        }
        return listePage;
    }

}
