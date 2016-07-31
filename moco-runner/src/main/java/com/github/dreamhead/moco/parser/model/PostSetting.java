package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoEventAction;
import com.google.common.base.MoreObjects;

import static com.github.dreamhead.moco.Moco.get;
import static com.github.dreamhead.moco.Moco.post;
import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.parser.model.DynamicResponseHandlerFactory.toVariables;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PostSetting {
    private TextContainer url;
    private String content;

    public MocoEventAction createAction() {
        if (url.isRawText()) {
            return post(url.getText(), Moco.text(content));
        }

        if (url.isForTemplate()) {
            return post(template(url.getText(), toVariables(url.getProps())), Moco.text(content));
        }

        throw new IllegalArgumentException("Unknown " + url + " for get setting");
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("url", url)
                .add("content", content)
                .toString();
    }
}
