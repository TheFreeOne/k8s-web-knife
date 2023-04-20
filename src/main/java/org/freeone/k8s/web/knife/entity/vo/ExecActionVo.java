package org.freeone.k8s.web.knife.entity.vo;

import java.util.List;

public class ExecActionVo {

    private List<String> command = null;


    public List<String> getCommand() {
        return this.command;
    }

    public void setCommand(List<String> command) {
        this.command = command;
    }
}
