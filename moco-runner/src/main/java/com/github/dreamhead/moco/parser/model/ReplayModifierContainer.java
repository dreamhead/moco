package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.ResponseElement;
import com.github.dreamhead.moco.parser.deserializer.ReplayModifierContainerDeserializer;

import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.Moco.with;

@JsonDeserialize(using = ReplayModifierContainerDeserializer.class)
public class ReplayModifierContainer {
    private String text;
    private ResponseSetting setting;

    public ReplayModifierContainer(final String text) {
        this.text = text;
    }

    public ReplayModifierContainer(final ResponseSetting setting) {
        this.setting = setting;
    }

    public final ResponseElement getResponseHandler() {
        if (text != null) {
            return with(template(text));
        }

        return this.setting.getResponseHandler();
    }
}
