package com.decodetamination.reducurl;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("reduced_url")
public record ReducedUrl(@PrimaryKey String id, String url) {
}
