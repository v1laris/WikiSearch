package com.wks.wikisearch.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Request {
    @NonNull
    private String query;
}

