package com.example.ordersservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Schema {
    private String type;
    private boolean optional;
    private String name;
    private List<Field> fields;
}
