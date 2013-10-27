package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoEventAction;

import static com.github.dreamhead.moco.Moco.post;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PostSetting {
    private String url;
    private String content;

    public MocoEventAction createAction() {
        return post(url, Moco.text(content));
    }
}
