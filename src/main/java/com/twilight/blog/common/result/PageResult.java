package com.twilight.blog.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * 统一分页响应，后台接口共用，前端只需认一种结构。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private List<T> list;
    private long total;
    private int page;
    private int size;
    private int pages;

    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages()
        );
    }

    /** 页码从 1 开始的分页结果（后台接口统一使用这种约定）。 */
    public static <T> PageResult<T> ofOneBased(Page<T> page) {
        return new PageResult<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages()
        );
    }

    /** 需要把实体转换成 DTO 时使用。 */
    public static <S, T> PageResult<T> of(Page<S> page, Function<S, T> mapper) {
        return new PageResult<>(
                page.getContent().stream().map(mapper).toList(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages()
        );
    }

    /** 非分页数据包装成分页结构（用于内存组装的结果）。 */
    public static <T> PageResult<T> of(List<T> list, long total, int page, int size) {
        int pages = size <= 0 ? 0 : (int) Math.ceil((double) total / size);
        return new PageResult<>(list, total, page, size, pages);
    }
}
