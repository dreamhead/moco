package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.MoreObjects;

import static com.github.dreamhead.moco.Moco.get;
import static com.github.dreamhead.moco.Moco.post;
import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.parser.model.DynamicResponseHandlerFactory.toVariables;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PostSetting {
    private TextContainer url;
    private String content;

    public MocoEventAction createAction() {
        return post(urlResource(this.url), Moco.text(content));
    }

    private Resource urlResource(final TextContainer url) {
        if (url.isRawText()) {
            return text(url.getText());
        }

        if (url.isForTemplate()) {
            return template(url.getText(), toVariables(url.getProps()));
        }

        throw new IllegalArgumentException("Unknown " + url + " for post setting");
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
