package com.github.twrpbuilder;

import com.github.twrpbuilder.Interface.Tools;
import com.github.twrpbuilder.Models.OptionsModel;
import com.github.twrpbuilder.Task.RunCode;
import com.github.twrpbuilder.mkTree.MakeTree;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;


public class MainActivity extends Tools {
    public static String rName;
    private static String applicationName = "TwrpBuilder";
    public static void usePosixParser(final String[] commandLineArguments) {
        final CommandLineParser cmdLinePosixParser = new DefaultParser();
        final Options posixOptions = constructPosixOptions();
        final OptionsModel optionsModel=new OptionsModel();
        CommandLine commandLine;
        try {
            commandLine = cmdLinePosixParser.parse(posixOptions, commandLineArguments);
            if (commandLine.hasOption("aik")) {
                optionsModel.setAndroidImageKitchen(true);
            }

            if (commandLine.hasOption("f")) {
                String g = commandLine.getOptionValue("f");
                optionsModel.setExtract(true);
                if (new File(g).exists()) {
                    if (!g.contains(" ")) {
                        System.out.println("Building tree using: " + g);
                        if (commandLine.hasOption("t")) {
                            String t = commandLine.getOptionValue("t");
                            if (t.equals("mrvl")) {
                                new RunCode(g, "mrvl",optionsModel).start();
                                new RunCode(g, "mrvl",optionsModel).start();
                            } else if (t.equals("samsung")) {
                                new RunCode(g, "samsung",optionsModel).start();
                            } else if (t.equals("mtk") || t.equals("mt")) {
                                new RunCode(g, "mtk",optionsModel).start();
                            }
                        } else {
                            new Thread(new RunCode(g,optionsModel)).start();
                        }
                    } else {
                        System.out.println("Please remove spaces from filename. ");
                    }
                } else {
                    System.out.println(g + " does not exist .");
                }
            }

            if (commandLine.hasOption("h")) {
                System.out.println("-- USAGE --");
                printUsage(applicationName, constructPosixOptions(), System.out);
                System.out.println("-- HELP --");
                printHelp(
                        constructPosixOptions(), 80, "HELP", "End of Help",
                        3, 5, true, System.out);

            }

            if (commandLine.hasOption("otg")) {
                optionsModel.setOtg(true);
            }

            if (commandLine.hasOption("r")) {
                String g = commandLine.getOptionValue("r");
                if (new File(g).exists()) {
                    if (!g.contains(" ")) {
                        System.out.println("Building tree using: " + g);
                        rName = g;
                        if (commandLine.hasOption("t")) {
                            String t = commandLine.getOptionValue("t");
                            if (t.equals("mrvl")) {
                                new RunCode(g, "mrvl",optionsModel).start();
                            } else if (t.equals("samsung")) {
                                new RunCode(g, "samsung",optionsModel).start();
                            } else if (t.equals("mtk") || t.equals("mt")) {
                                new RunCode(g, "mtk",optionsModel).start();
                            }
                        } else {
                            new RunCode(g,optionsModel).start();
                        }
                    } else {
                        System.out.println("Please remove spaces from filename. ");
                    }
                } else {
                    System.out.println(g + " does not exist .");
                }
            }
            if (commandLine.hasOption("l")) {
                optionsModel.setLandscape(true);
            }
        } catch (ParseException parseException)  // checked exception
        {
            System.err.println(
                    "Encountered exception while parsing using PosixParser:\n"
                            + parseException.getMessage());
        }
    }

    public static Options constructPosixOptions() {
        final Options option = new Options();
        option.addOption("f", "file", true, "build using backup file (made from app).");
        option.addOption("t", "type", true, "supported option :- \n mt , samsung,mrvl");
        option.addOption("l", "land-scape", false, "enable landscape mode");
        option.addOption("aik", "Android_Image_Kitchen", false, "Extract backup or recovery.img using Android Image kitchen");
        option.addOption("otg", "otg-support", false, "add otg support to fstab");
        option.addOption("r", "recovery", true, "build using recovery image file");
        option.addOption("h", "help", false, "print this help");
        return option;
    }

    public static void displayProvidedCommandLineArguments(
            final String[] commandLineArguments,
            final OutputStream out) {
        final StringBuffer buffer = new StringBuffer();
        for (final String argument : commandLineArguments) {
            buffer.append(argument).append(" ");
        }
        try {
            out.write((buffer.toString() + "\n").getBytes());
        } catch (IOException ioEx) {
            System.err.println(
                    "WARNING: Exception encountered trying to write to OutputStream:\n"
                            + ioEx.getMessage());
            System.out.println(buffer.toString());
        }
    }


    public static void printUsage(
            final String applicationName,
            final Options options,
            final OutputStream out) {
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter usageFormatter = new HelpFormatter();
        usageFormatter.printUsage(writer, 80, applicationName, options);
        writer.flush();
    }


    public static void printHelp(
            final Options options,
            final int printedRowWidth,
            final String header,
            final String footer,
            final int spacesBeforeOption,
            final int spacesBeforeOptionDescription,
            final boolean displayUsage,
            final OutputStream out) {
        final String commandLineSyntax = "java -jar " + new File(MainActivity.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName() + " -f backupfile.tar.gz";
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(
                writer,
                printedRowWidth,
                commandLineSyntax,
                header,
                options,
                spacesBeforeOption,
                spacesBeforeOptionDescription,
                footer,
                displayUsage);
        writer.flush();
    }

    public static void main(final String[] commandLineArguments) {
        if (commandLineArguments.length < 1) {
            System.out.println("-- USAGE --");
            printUsage(applicationName, constructPosixOptions(), System.out);
            System.out.println("-- HELP --");
            printHelp(
                    constructPosixOptions(), 80, "HELP", "End of Help",
                    3, 5, true, System.out);
        }
        usePosixParser(commandLineArguments);
    }
}
