package com.github.dreamhead.moco.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.dreamhead.moco.parser.model.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.lang.String.format;

public class SettingParser {
    private static Logger logger = LoggerFactory.getLogger(SettingParser.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeFactory factory = TypeFactory.defaultInstance();

    public List<GlobalSetting> parse(InputStream is) {
        try {
            return mapper.readValue(is, factory.constructCollectionType(List.class, GlobalSetting.class));
        } catch (UnrecognizedPropertyException upe) {
            logger.info("Unrecognized field: {}", upe.getMessage());
            throw new RuntimeException(format("Unrecognized field [ %s ], please check!", upe.getUnrecognizedPropertyName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
