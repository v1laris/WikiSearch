package com.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Request {
    // еуые
    @NonNull
    private String query;
}

