package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.MocoEventAction;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class MocoRequestAction implements MocoEventAction {
    private final String url;

    public MocoRequestAction(String url) {
        this.url = url;
    }

    @Override
    public void execute() {
        HttpClient client = new DefaultHttpClient();
        try {
            client.execute(new HttpGet(url));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
