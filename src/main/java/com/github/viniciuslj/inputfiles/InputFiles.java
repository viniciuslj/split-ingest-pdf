package com.github.viniciuslj.inputfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class InputFiles {
    private ParametersInputFiles parameters;

    private InputFiles(ParametersInputFiles parameters) {
        this.parameters = parameters;
    }

    public static InputFiles createFromParameters(ParametersInputFiles parameters) {
        return new InputFiles(parameters);
    }

    public void forEach(Consumer<Path> consumer) throws IOException {
        if(parameters.isSingleMode()) {
            consumer.accept(Paths.get(parameters.getPdfName()));
            return;
        }

        Files.walk(Paths.get(parameters.getRecursivePath()))
                .filter(path -> path.toString().toLowerCase().endsWith(".pdf"))
                .forEach(path -> consumer.accept(path));
    }
}
