package com.byos.yohann.fanfic;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Yohann on 17/02/2016.
 */
public class JsonApiToData {

    public JsonApiToData() {


    }

    public static User loginUser(String json) {

        try {

            boolean error = new JSONObject(json).getBoolean("error");

            if(!error) {
                JSONObject jsonUser = new JSONObject(json).getJSONObject("user");
                User user = new User(
                        jsonUser.getInt("id"),
                        jsonUser.getString("email"),
                        jsonUser.getString("name")
                );
                Log.d("user", user.toString());
                return user;

            } else {

                return null;
            }

        } catch (Exception e) {

            Log.e("error", e.getMessage());
            return null;
        }
    }

    public static ArrayList<Story> getUserStories(int userId) {

        // TODO: 17/02/2016 connexion à la base de données et récupération des stories
        ArrayList<Story> data = new ArrayList<Story>();
        data.add(new Story(1, 5, "Star wars le retour", "Yohann Caillon", 2, "Star Wars"));
        data.add(new Story(2, 35, "Harry Potter à la clinique", "Yohann Caillon", 17, "Harry Potter"));
        data.add(new Story(3, 16, "Sacha love", "Yohann Caillon", 15, "Pokemon"));
        data.add(new Story(4, 8, "Batman 35", "Yohann Caillon", 2, "Batman"));

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

    public Page getPageFromStory(int storyId, int numPage) {

        return new Page(1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque quis nisi ut risus egestas lobortis et tincidunt orci. Suspendisse eget lacus sed ligula commodo euismod sed non tortor. Aenean eget est tortor. Maecenas quis hendrerit eros. Vestibulum condimentum arcu non sem ultricies tempus. Sed nec lectus molestie arcu cursus posuere quis vel urna. Nullam dapibus, lorem et aliquet dictum, lacus est pellentesque lacus, luctus condimentum metus lectus in quam. Mauris dignissim consectetur justo a vulputate. Mauris justo nunc, mollis ut vehicula sit amet, hendrerit egestas metus. Maecenas condimentum sed nulla sed viverra. Maecenas vestibulum nisi volutpat laoreet scelerisque. Nunc gravida tempor lectus sed rhoncus" +
                "Suspendisse feugiat odio et turpis tincidunt, et pellentesque est tristique. Morbi sollicitudin felis in euismod volutpat. Sed sollicitudin purus nisl, sed ultricies sem sodales non. Nulla at tellus non elit varius lacinia. Duis quis nulla vel dolor consequat dictum. Duis ut quam lectus. Proin et nibh tincidunt, sodales sem ac, laoreet purus. Proin porttitor, turpis at placerat ornare, ex odio lobortis purus, eget condimentum nunc risus ut mauris. Curabitur eget tortor vehicula, aliquet sapien in, luctus diam. Curabitur sollicitudin enim eget interdum varius. Nam nec magna sit amet felis tempor accumsan. Vestibulum quis sapien a libero tempus elementum. Proin sagittis libero sed purus ultrices, sit amet auctor nunc vulputate" +
                "Aliquam a massa erat. Nullam at vestibulum sem. Curabitur vel nunc pulvinar, elementum nibh at, maximus nunc. Vivamus egestas venenatis massa, bibendum lacinia eros porta et. Fusce non rutrum tortor. Maecenas ultricies risus facilisis molestie ultricies. Mauris non posuere dui, ac lacinia felis. Sed dignissim tristique ante ac cursus. Maecenas tempor malesuada eros vel consectetur. Fusce nec nibh rutrum, porta felis sit amet, sodales augue. Donec aliquam ullamcorper risus non facilisis.\n" +
                "Donec blandit orci lectus, et finibus tellus interdum non. Etiam rhoncus nunc tortor, vitae condimentum ipsum facilisis nec. Nulla vel massa non nisl bibendum sagittis quis imperdiet velit. In hac habitasse platea dictumst. Quisque fermentum auctor mi eu bibendum. Nunc mollis volutpat metus, vel maximus ante molestie vel. Mauris in mauris sed lacus pulvinar fermentum in at sem.Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Morbi id porttitor sapien, a hendrerit dui. Aliquam euismod congue sem id vulputate. Nam non metus et ipsum blandit luctus. Donec at lectus mauris. Sed sagittis cursus mattis. Ut vitae mi est. Praesent et magna id justo elementum ornare. Nunc varius rhoncus posuere. Nulla sit amet metus convallis, congue tortor vel, suscipit purus. Integer aliquam consequat ante ultrices hendrerit. Integer id convallis eros, maximus lobortis tortor. Donec placerat consequat ligula, et mattis quam volutpat sit amet.", 1);
    }

}
