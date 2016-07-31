package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.MocoEventAction;
import com.google.common.base.MoreObjects;

import static com.github.dreamhead.moco.Moco.get;
import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.parser.model.DynamicResponseHandlerFactory.toVariables;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class GetSetting {
    private TextContainer url;

    public MocoEventAction createAction() {
        if (url.isRawText()) {
            return get(url.getText());
        }

        if (url.isForTemplate()) {
            return get(template(url.getText(), toVariables(url.getProps())));
        }

        throw new IllegalArgumentException("Unknown " + url + " for get setting");
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("url", url)
                .toString();
    }
}
