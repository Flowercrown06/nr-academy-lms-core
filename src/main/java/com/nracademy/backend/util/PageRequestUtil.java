package com.nracademy.backend.util;

import com.nracademy.backend.exception.common.InvalidPageNumberException;
import com.nracademy.backend.exception.common.InvalidPaginationParametersException;
import com.nracademy.backend.exception.common.PageSizeExceededException;
import com.nracademy.backend.exception.common.UnsupportedSortDirectionException;
import com.nracademy.backend.exception.common.UnsupportedSortFieldException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class PageRequestUtil {

    private static final int MAX_SIZE = 100;

    private PageRequestUtil() {
    }

    public static Pageable create(int page, int size, List<String> sortParams, Set<String> allowedFields, String defaultSort) {
        if (page < 0) {
            throw new InvalidPageNumberException();
        }
        if (size < 1) {
            throw new InvalidPaginationParametersException( "Page size must be greater than 0.");
        }
        if (size > MAX_SIZE) {
            throw new PageSizeExceededException(size,MAX_SIZE);
        }

        List<Sort.Order> orders = parseSort(sortParams, allowedFields, defaultSort);
        if (orders.stream().noneMatch(o -> "id".equals(o.getProperty()))) {
            orders.add(Sort.Order.asc("id"));
        }
        return PageRequest.of(page, size, Sort.by(orders));
    }

    public static List<String> resolvedSort(List<String> sortParams, String defaultSort) {
        if (sortParams == null || sortParams.isEmpty()) {
            return List.of(defaultSort);
        }
        return sortParams;
    }

    private static List<Sort.Order> parseSort(List<String> sortParams, Set<String> allowedFields, String defaultSort) {
        List<String> effective = sortParams == null || sortParams.isEmpty()
                ? List.of(defaultSort)
                : sortParams;

        List<Sort.Order> orders = new ArrayList<>();
        for (String param : effective) {
            String[] parts = param.split(",", 2);
            String field = parts[0].trim();
            if (!allowedFields.contains(field)) {
                throw new UnsupportedSortFieldException(field);
            }
            Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            if (parts.length > 1 && !"asc".equalsIgnoreCase(parts[1].trim()) && direction != Sort.Direction.DESC) {
                throw new UnsupportedSortDirectionException(direction.name());
            }
            orders.add(new Sort.Order(direction, field));
        }
        return orders;
    }
}
