package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;
import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.setting.Setting;
import com.google.common.collect.ImmutableList;

import java.util.Optional;

public class Responser<T extends ResponseSetting> {
    private SettingFetcher<T> fetcher;

    public Responser(final SettingFetcher<T> fetcher) {
        this.fetcher = fetcher;
    }

    public Optional<Response> getResponse(final SessionContext context) {
        Request request = context.getRequest();
        ImmutableList<Setting<T>> settings = fetcher.getSettings();
        for (Setting<?> setting : settings) {
            if (setting.match(request)) {
                setting.writeToResponse(context);
                return Optional.of(context.getResponse());
            }
        }

        Setting<T> anySetting = fetcher.getAnySetting();
        if (anySetting.match(request)) {
            anySetting.writeToResponse(context);
            return Optional.of(context.getResponse());
        }

        return Optional.empty();
    }
}
