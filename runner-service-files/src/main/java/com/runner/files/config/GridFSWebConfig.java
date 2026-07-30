package com.runner.files.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class GridFSWebConfig {

    @Value("${spring.data.mongodb.database}")
    private String mongodb;

    @Bean
    public GridFSBucket getGridFSBucket(MongoTemplate mongoTemplate) {
        MongoDatabase database = mongoTemplate.getDb();
        return GridFSBuckets.create(database);
    }
}