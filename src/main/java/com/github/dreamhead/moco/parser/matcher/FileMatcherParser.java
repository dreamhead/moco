package com.github.dreamhead.moco.parser.matcher;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.parser.model.RequestSetting;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.stream;

public class FileMatcherParser implements MatcherParser {
    @Override
    public RequestMatcher parse(RequestSetting request) {
        String file = request.getFile();
        if (file == null) {
            return null;
        }

        try {
            return by(stream(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
