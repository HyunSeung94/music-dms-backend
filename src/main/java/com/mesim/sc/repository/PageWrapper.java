package com.mesim.sc.repository;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageWrapper {

    private List<Object> list = new ArrayList<>();

    private int totalPages;         // 전체 페이지 개수
    private long totalElements;     // 전체 페이지 레코드 개수
    private int number;             // 현재 페이지
    private int numberOfElements;   // 현재 페이지 레코드 개수

    public PageWrapper() {}

    public PageWrapper(Page page) {
        if (page != null && page.getSize() > 0) {
            this.setTotalPages(page.getTotalPages());
            this.setTotalElements(page.getTotalElements());
            this.setNumber(page.getNumber());
            this.setNumberOfElements(page.getNumberOfElements());
        }
    }
}
