package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.runner.JsonRunner;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("moco [configuration file]");
            System.exit(1);
        }

        new JsonRunner().run(new FileInputStream(args[0]));
    }
}
