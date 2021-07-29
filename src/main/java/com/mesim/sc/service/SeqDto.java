package com.mesim.sc.service;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class SeqDto {

    protected int seq;

    abstract public Object toEntity();
}
