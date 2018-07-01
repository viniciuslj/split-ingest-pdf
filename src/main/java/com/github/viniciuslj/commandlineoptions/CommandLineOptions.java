package com.github.viniciuslj.commandlineoptions;

import com.github.viniciuslj.elasticsearch.ParametersElasticsearch;
import com.github.viniciuslj.ParametersFileSystem;
import org.apache.commons.cli.*;


public class CommandLineOptions implements
        ParametersElasticsearch,
        ParametersFileSystem {

    private static final String APP_NAME = "split-ingest-pdf";
    private Options options;
    private String[] args;
    private CommandLine commandLine;

    private CommandLineOptions(String[] args) {
        this.args = args;
        options = makeOptions(args);
    }

    public static CommandLineOptions createFromArgs(String[] args) {
        return new CommandLineOptions(args);
    }

    public void parser() throws ParseException {
        CommandLineParser parser = new DefaultParser();
        commandLine = parser.parse(options, args);

        if(isRecursiveMode() && isSingleMode()){
            throw new CommandLineOptionsException("Recursive mode or single mode, not both");
        }

        if(!isRecursiveMode() && !isSingleMode()){
            throw new CommandLineOptionsException("Recursive mode or single mode must be set");
        }
    }

    public boolean hasHelp() {
        return commandLine.hasOption("H");
    }

    public void printHelp() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("java -jar " + CommandLineOptions.APP_NAME, options, true);
    }

    private Options makeOptions(String[] args) {
        Options options = new Options();

        // elasticsearch host
        options.addOption(Option.builder("h")
                .argName("elasticsearch host")
                .longOpt("host")
                .hasArg()
                .desc("Elasticsearch Host (Default \"localhost\"")
                .build());

        // elasticsearch port
        options.addOption(Option.builder("p")
                .argName("elasticsearch port")
                .longOpt("port")
                .hasArg()
                .desc("ElasticSearch Port (Default \"9300\"")
                .build());

        // cluster name
        options.addOption(Option.builder("c")
                .argName("cluster name")
                .longOpt("cluster")
                .hasArg()
                .desc("Cluster name (Default \"docker-cluster\"")
                .build());

        // index name
        options.addOption(Option.builder("i")
                .required()
                .argName("index name")
                .longOpt("index")
                .hasArg()
                .desc("Index name in Elasticsearch")
                .build());

        // pdf file
        options.addOption(Option.builder("f")
                .argName("pdf file")
                .longOpt("pdf-file")
                .hasArg()
                .desc("Path with PDF name (Ex.: /path/to/single-pdf/file.pdf)")
                .build());

        // recursive path
        options.addOption(Option.builder("r")
                .argName("recursive path")
                .longOpt("recursive-path")
                .hasArg()
                .desc("Initial path for recursive processing (Ex.: /path/to/recursive-mode)")
                .build());

        // split mode
        options.addOption(Option.builder("s")
                .longOpt("split")
                .desc("Split each file into pages")
                .build());

        // help
        options.addOption(Option.builder("H")
                .longOpt("help")
                .desc("Show help")
                .build());

        return  options;
    }

    @Override
    public String getHost() {
        return commandLine.getOptionValue("h", "localhost");
    }

    @Override
    public int getPort()
    {
        return Integer.parseInt(commandLine.getOptionValue("p", "9300"));
    }

    @Override
    public String getClusterName() {
        return commandLine.getOptionValue("c", "docker-cluster");
    }

    @Override
    public String getIndexName() {
        return commandLine.getOptionValue("i");
    }

    @Override
    public boolean isSplitMode() {
        return commandLine.hasOption("s");
    }

    @Override
    public boolean isRecursiveMode() {
        return commandLine.hasOption("r");
    }

    @Override
    public boolean isSingleMode() {
        return commandLine.hasOption("f");
    }

    @Override
    public String getPdfName() {
        return commandLine.getOptionValue("f");
    }

    @Override
    public String getRecursivePath() {
        return commandLine.getOptionValue("f");
    }
}
