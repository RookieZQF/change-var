package com.perfma.xlab.change.var.feature;

/**
 * @author: ZQF
 * @date: 2021-04-25
 * @description: desc
 */
public class CommandInfo {
    private String cmd;
    private String[] args;

    public CommandInfo(String cmd, String[] args){
        this.cmd = cmd;
        this.args = args;
    }

    public String getCmd() {
        return cmd;
    }

    public String[] getArgs(){
        return args;
    }
}
