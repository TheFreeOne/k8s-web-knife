package org.freeone.k8s.web.knife.entity.vo;

import java.math.BigDecimal;

public class QuantityVo {
    private String name;

    private String numberString;


    private BigDecimal number;

    private String format;

    public String getFormat() {
        return this.format;
    }

    public QuantityVo setFormat(String format) {
        this.format = format;
        return this;
    }

    public BigDecimal getNumber() {
        return this.number;
    }

    public QuantityVo setNumber(BigDecimal number) {
        this.number = number;
        return this;
    }

    public String getNumberString() {
        return this.numberString;
    }

    public QuantityVo setNumberString(String numberString) {
        this.numberString = numberString;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public QuantityVo setName(String name) {
        this.name = name;
        return this;
    }
}
