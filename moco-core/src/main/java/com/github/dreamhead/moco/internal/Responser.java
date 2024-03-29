package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;
import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.setting.Setting;
import com.google.common.collect.ImmutableList;

import java.util.Optional;

public class Responser<T extends ResponseSetting<T>> {
    private final SettingFetcher<T> fetcher;

    public Responser(final SettingFetcher<T> fetcher) {
        this.fetcher = fetcher;
    }

    public final Optional<Response> getResponse(final SessionContext context) {
        Request request = context.getRequest();
        ImmutableList<Setting<T>> settings = fetcher.getSettings();
        final Optional<Setting<T>> firstSetting = settings.stream()
                .filter(setting -> setting.match(request))
                .findFirst();
        if (firstSetting.isPresent()) {
            firstSetting.get().writeToResponse(context);
            return Optional.of(context.getResponse());
        }

        return getAnyResponse(context);
    }

    private Optional<Response> getAnyResponse(final SessionContext context) {
        Request request = context.getRequest();
        Setting<T> anySetting = fetcher.getAnySetting();
        if (anySetting.match(request)) {
            anySetting.writeToResponse(context);
            return Optional.of(context.getResponse());
        }

        return Optional.empty();
    }
}
