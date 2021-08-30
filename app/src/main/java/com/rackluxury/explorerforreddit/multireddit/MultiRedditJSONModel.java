package com.rackluxury.explorerforreddit.multireddit;

import com.google.gson.Gson;

import java.util.ArrayList;

public class MultiRedditJSONModel {

    public MultiRedditJSONModel() {}

    public MultiRedditJSONModel(String display_name, String description_md, boolean isPrivate,
                                ArrayList<String> subreddits) {
        String visibility;
        if (isPrivate) {
            visibility = "private";
        } else {
            visibility = "public";
        }

        if (subreddits != null) {
            SubredditInMultiReddit[] subreddits1 = new SubredditInMultiReddit[subreddits.size()];
            for (int i = 0; i < subreddits.size(); i++) {
                SubredditInMultiReddit subredditInMultiReddit = new SubredditInMultiReddit(subreddits.get(i));
                subreddits1[i] = subredditInMultiReddit;
            }
        }
    }

    public String createJSONModel() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
