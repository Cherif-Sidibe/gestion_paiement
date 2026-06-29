package edu.ism.badwallet.shared.response;

import java.util.List;

import org.springframework.data.domain.Page;

public record PageResponse<T>(List<T> data, long totalElements, int totalPages, int currentPage, int pageSize, boolean isFirst, boolean isLast) {
    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize(), page.isFirst(), page.isLast());
    }
}
