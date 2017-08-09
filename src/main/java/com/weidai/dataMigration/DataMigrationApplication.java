package com.weidai.dataMigration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, BatchAutoConfiguration.class})
public class DataMigrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataMigrationApplication.class, args);
    }
}
