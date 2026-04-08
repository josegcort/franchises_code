package com.tt.franchises.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Model for a franchise, which contains multiple branches.
 */
@Data
@AllArgsConstructor
public class Franchise {
    private String id;
    private String name;
    private List<Branch> branches;
}
