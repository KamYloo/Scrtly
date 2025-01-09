package com.kamylo.Scrtly_backend.response;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PagedResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean firstPage;
    private boolean last;
    private List<SortInfo> sort;

    public static <T> PagedResponse<T> of(Page<T> page) {
        PagedResponse<T> response = new PagedResponse<>();
        response.setContent(page.getContent());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirstPage(page.isFirst());
        response.setLast(page.isLast());
        response.setSort(page.getSort().stream()
                .map(order -> new SortInfo(order.getProperty(), order.getDirection().name()))
                .toList());
        return response;
    }
}
