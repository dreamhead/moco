package com.github.dreamhead.moco.recorder;

public interface RecorderConfig {
    String TAPE = "tape";
    String IDENTIFIER = "identifier";
    String MODIFIER = "modifier";

    boolean isFor(String name);
}
