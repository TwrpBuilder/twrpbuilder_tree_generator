package com.github.twrpbuilder.Interface;

import java.io.File;
import java.util.LinkedList;

public interface ToolsInterface {
    String newLine = "\n";
    String seprator = File.separator;
    String travisYml = ".travis.yml";
    String home = System.getProperty("user.home") + seprator;
    String pwd = System.getProperty("user.dir") + seprator;

    boolean fexist(String name);

    String command(String run);

    LinkedList command(String run, boolean LinkList);

    boolean mkdir(String name);

    boolean rm(String name);

    String CopyRight();

    void cp(String from, String to);

    String propFile();

    void Write(String name, String data);
    void Clean();
    void extract(String name);
}
