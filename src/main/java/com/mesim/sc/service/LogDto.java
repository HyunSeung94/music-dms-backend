package com.mesim.sc.service;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class LogDto {

    protected int seq;
    protected String regDate;

    public LogDto() {}



    abstract public Object toEntity();
}
