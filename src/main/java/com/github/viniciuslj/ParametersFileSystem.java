package com.github.viniciuslj;

public interface ParametersFileSystem {
    boolean isSplitMode();
    boolean isRecursiveMode();
    boolean isSingleMode();
    String getPdfName();
    String getRecursivePath();
}
