package com.example.reactivewings.utils;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;

public class FlightsControllerUtils
{
    public static final int MAX_PAGE_SIZE = 500;

    public static Query buildSearchQuery(int page, int size)
    {
        int limit = Math.min(size, MAX_PAGE_SIZE);
        int skip = Math.max(page, 0) * limit;
        return new Query()
            .skip(skip)
            .limit(limit)
            .with(Sort.by(Sort.Direction.DESC, "lastUpdated"));
    }
}
