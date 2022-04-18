package com.test.demo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageData<T> {

    private Integer pageNumber;

    private Integer pageSize;

    private Integer totalCount;

    private List<T> data;

    public static <T> PageData<T> empty(Integer pageNumber, Integer pageSize) {
        return new PageData<>(pageNumber, pageSize, 0, Collections.emptyList());
    }

    public static <T> PageData<T> page(Integer pageNumber, Integer pageSize, Integer totalCount, List<T> data) {
        return new PageData<>(pageNumber, pageSize, totalCount, data);
    }
}
