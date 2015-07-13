package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.JsonResponseHandler;
import com.github.dreamhead.moco.handler.SequenceContentHandler;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResponsesInFileSetting extends BaseResourceSetting {
    public ResponseHandler getResponseHandler() {
        String filePath = file.getName().getText();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Map> responseSettings = mapper.readValue(new File(filePath), TypeFactory.defaultInstance().constructCollectionType(List.class, Map.class));
            ImmutableList.Builder<ResponseHandler> builder = ImmutableList.builder();
            for (Map responseSetting : responseSettings) {
                builder.add(new JsonResponseHandler(responseSetting));
            }
            return new SequenceContentHandler(builder.build());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
