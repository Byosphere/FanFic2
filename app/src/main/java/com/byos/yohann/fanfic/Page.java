package com.byos.yohann.fanfic;

/**
 * Created by Yohann on 17/02/2016.
 */
public class Page {

    private String text;
    private int num;
    private int storyId;

    public Page(int num, String text, int storyId) {
        this.text = text;
        this.num = num;
        this.storyId = storyId;
    }

    public int getNum() {
        return num;
    }

    public int getStoryId() {
        return storyId;
    }

    public String getText() {
        return text;
    }
}
