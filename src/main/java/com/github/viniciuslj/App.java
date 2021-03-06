package com.github.viniciuslj;

import com.github.viniciuslj.commandlineoptions.CommandLineOptions;
import com.github.viniciuslj.elasticsearch.ElasticsearchClient;
import com.github.viniciuslj.inputfiles.InputFiles;
import com.github.viniciuslj.pdf.PDFManipulator;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class App
{
    private static CommandLineOptions options;
    private static ElasticsearchClient client;
    private static PDFManipulator pdfManipulator;
    private static InputFiles inputFiles;
    private static long processedFileCounter = 0;
    private static List<String> unprocessedFileList;

    public static void main(String[] args) {
        // https://pdfbox.apache.org/2.0/getting-started.html
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        disableLogPDFBox();

        options = processArgs(args);
        if(options == null) {
            return;
        }

        unprocessedFileList = new ArrayList<>();

        try {
            client = ElasticsearchClient.createFromParameters(options);

            pdfManipulator = new PDFManipulator();
            Consumer<Path> consumer = (path) -> processSingleFile(path);
            inputFiles = InputFiles.createFromParameters(options);
            inputFiles.forEach(consumer);

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

    private static void processSplit(Path path) {
        long pageNumber = 0;

        try {
            List<Map<String, Object>> pagesMap = pdfManipulator.getPagesMap(path);
            client.prepareBulk();
            for (Map<String, Object> pageMap: pagesMap) {
                pageNumber++;
                client.addDocumentInBulk(pageMap, path.toAbsolutePath().toString() + "." + pageNumber);
            }

            client.sendBulk();

            processedFileCounter++;
        } catch (Exception e) {
            unprocessedFileList.add(e.getMessage() + ": " + path.toString() + " - page " + pageNumber);
        }
    }

    private static void processFull(Path path) {
        try {
            Map<String, Object> map = pdfManipulator.getDocumentMap(path);
            client.addDocumnet(map, path.toAbsolutePath().toString());

            processedFileCounter++;
        } catch (Exception e) {
            unprocessedFileList.add(e.getMessage() + ": " + path.toString());
        }
    }

    private static void processSingleFile(Path path) {
        System.out.println((processedFileCounter + 1) + ": " + path.toAbsolutePath());

        if(options.isSplitMode()) {
            processSplit(path);
        } else {
            processFull(path);
        }

        System.gc();
    }

    private static void disableLogPDFBox() {
        // https://stackoverflow.com/questions/47554201/how-to-disable-pdfbox-warn-logging
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
    }
}
