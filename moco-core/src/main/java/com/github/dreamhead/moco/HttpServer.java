package com.github.dreamhead.moco;

import com.github.dreamhead.moco.matcher.HttpMethodRequestMatcher;
import com.github.dreamhead.moco.mount.MountHandler;
import com.github.dreamhead.moco.mount.MountMatcher;
import com.github.dreamhead.moco.mount.MountPredicate;
import com.github.dreamhead.moco.mount.MountTo;
import com.github.dreamhead.moco.setting.BaseSetting;
import org.jboss.netty.handler.codec.http.HttpMethod;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.or;
import static com.google.common.collect.ImmutableList.copyOf;
import static java.lang.String.format;

public class HttpServer extends ResponseSetting {
    private final int port;
    private List<BaseSetting> settings = new ArrayList<BaseSetting>();

    public HttpServer(int port) {
        this.port = port;
    }

    public Setting request(RequestMatcher matcher) {
        BaseSetting setting = new BaseSetting(matcher);
        this.settings.add(setting);
        return setting;
    }

    public Setting request(RequestMatcher... matchers) {
        return request(or(matchers));
    }

    public Setting get(RequestMatcher matcher) {
        return request(and(new HttpMethodRequestMatcher(HttpMethod.GET), matcher));
    }

    public Setting post(RequestMatcher matcher) {
        return request(and(new HttpMethodRequestMatcher(HttpMethod.POST), matcher));
    }

    public int getPort() {
        return port;
    }

    public List<BaseSetting> getSettings() {
        return settings;
    }

    public ResponseHandler getAnyResponseHandler() {
        return this.handler;
    }

    public void mount(final String dir, final MountTo target, final MountPredicate... predicates) {
        File mountedDir = new File(dir);
        if (!mountedDir.exists()) {
            throw new IllegalArgumentException(format("Mounted directory %s does not exist", dir));
        }

        this.request(new MountMatcher(mountedDir, target, copyOf(predicates))).response(new MountHandler(mountedDir, target));
    }
}
