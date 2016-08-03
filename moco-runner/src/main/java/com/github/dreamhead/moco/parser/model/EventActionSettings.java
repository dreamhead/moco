package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.resource.Resource;

import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.parser.model.DynamicResponseHandlerFactory.toVariables;

public class EventActionSettings {
    public static Resource urlResource(final TextContainer url) {
        if (url.isRawText()) {
            return text(url.getText());
        }

        if (url.isForTemplate()) {
            if (url.hasProperties()) {
                return template(url.getText(), toVariables(url.getProps()));
            }

            return template(url.getText());
        }

        throw new IllegalArgumentException("Unknown " + url + " for event action setting");
    }

    private EventActionSettings() {
    }
}
