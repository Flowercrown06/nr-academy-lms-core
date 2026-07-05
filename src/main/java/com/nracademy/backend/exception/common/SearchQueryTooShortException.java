package com.nracademy.backend.exception.common;

import com.nracademy.backend.entity.enums.StatusCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class SearchQueryTooShortException extends AppException {
    public SearchQueryTooShortException(int minLength) {
        super("Search query must be at least " + minLength + " characters",
            StatusCode.SEARCH_QUERY_TOO_SHORT, List.of(), HttpStatus.BAD_REQUEST);
    }
}
