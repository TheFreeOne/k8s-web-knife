package org.freeone.k8s.web.knife.entity.vo;

public class EnvVarVo {
    private String name;

    private String value;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
