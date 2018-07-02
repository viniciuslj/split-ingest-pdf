package com.github.viniciuslj;

import com.github.viniciuslj.commandlineoptions.CommandLineOptions;
import com.github.viniciuslj.elasticsearch.ElasticsearchClient;
import com.github.viniciuslj.inputfiles.InputFiles;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class App
{
    private static ElasticsearchClient client;
    private static long processedFileCounter = 0;
    private static List<String> unprocessedFileList;

    public static void main(String[] args) {
        CommandLineOptions options = processArgs(args);

        if(options == null) {
            return;
        }

        unprocessedFileList = new ArrayList<String>();

        try {
            client = ElasticsearchClient.createFromParameters(options);

            Consumer<Path> consumer = (path) -> processSingleFile(path);

            InputFiles.createFromParameters(options).forEach(consumer);

            System.out.println("processedFileCounter=" + processedFileCounter);

            client.close();
        } catch (UnknownHostException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        printResult();
    }

    private static void printResult() {
        System.out.println("Files processed: " + processedFileCounter);
        System.out.println("Unprocessed files: " + unprocessedFileList.size());
        unprocessedFileList.forEach(System.err::println);
    }

    private static CommandLineOptions processArgs(String[] args) {
        CommandLineOptions options = CommandLineOptions.createFromArgs(args);

        try {
            options.parser();
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            options.printHelp();
            return null;
        }

        if(options.hasHelp()) {
            options.printHelp();
            return null;
        }

        return options;
    }

    public static void processSingleFile(Path path) {
        return;
    }
}
