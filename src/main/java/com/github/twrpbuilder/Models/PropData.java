package com.github.twrpbuilder.Models;

import java.io.Serializable;

public class PropData implements Serializable {

    private String command;
    private String type;

    public PropData(){}
    public PropData(String command,String type){
        this.command=command;
        this.type=type;
    }

    public PropData(String type){
        this.command=new String();
        this.type=type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
