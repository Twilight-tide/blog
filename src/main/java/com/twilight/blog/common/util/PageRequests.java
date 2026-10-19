package com.twilight.blog.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 后台分页参数处理：页码从 1 开始，每页条数限制在 1-100。
 */
public final class PageRequests {

    private PageRequests() {
    }

    public static Pageable of(int page, int size) {
        return of(page, size, Sort.unsorted());
    }

    public static Pageable of(int page, int size, Sort sort) {
        int pageIndex = Math.max(page, 1) - 1;
        int pageSize = Math.min(Math.max(size, 1), 100);
        return PageRequest.of(pageIndex, pageSize, sort);
    }
}
