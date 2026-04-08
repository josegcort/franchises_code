package com.tt.franchises.infrastructure.adapter.out.mongo.document;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Document(collection = "franchises")
public class FranchiseDocument {
    private String id;
    private String name;
    private List<BranchDocument> branches;
}
