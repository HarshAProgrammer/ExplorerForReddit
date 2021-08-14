package com.rackluxury.explorerforreddit.events;

import com.rackluxury.explorerforreddit.post.Post;

public class PostUpdateEventToPostDetailFragment {
    public final Post post;

    public PostUpdateEventToPostDetailFragment(Post post) {
        this.post = post;
    }
}
