package com.github.viniciuslj.commandlineoptions;

import org.apache.commons.cli.ParseException;

public class CommandLineOptionsException extends ParseException {
    public CommandLineOptionsException(String message) {
        super(message);
    }
}
