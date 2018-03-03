package com.ucpaas.smsp.model;

import java.math.BigDecimal;

public class SmsClientTariff {
    private Integer id;

    private Integer prefix;

    private String areaname;

    private String intername;

    private String intercode;

    private BigDecimal fee;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPrefix() {
        return prefix;
    }

    public void setPrefix(Integer prefix) {
        this.prefix = prefix;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getIntername() {
        return intername;
    }

    public void setIntername(String intername) {
        this.intername = intername == null ? null : intername.trim();
    }

    public String getIntercode() {
        return intercode;
    }

    public void setIntercode(String intercode) {
        this.intercode = intercode == null ? null : intercode.trim();
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
}