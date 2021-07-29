package com.mesim.sc.service;

import lombok.Data;

import java.util.List;

@Data
public class UpdateMapperDto {

    private List<Object> insertList;
    private List<Object> updateList;
    private List<Object> deleteList;

}
