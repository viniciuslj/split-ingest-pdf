package com.github.viniciuslj;

import com.github.viniciuslj.commandlineoptions.CommandLineOptions;
import org.apache.commons.cli.ParseException;

public class App
{
    public static void main(String[] args) {
        CommandLineOptions options = CommandLineOptions.createFromArgs(args);
        try {
            options.parser();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            options.printHelp();
            return;
        }

        if(options.hasHelp()) {
            options.printHelp();
            return;
        }
    }
}
