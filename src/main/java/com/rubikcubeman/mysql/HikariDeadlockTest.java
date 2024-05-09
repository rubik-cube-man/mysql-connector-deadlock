package com.rubikcubeman.mysql;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HikariDeadlockTest {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10, r -> {
        Thread thread = Executors.defaultThreadFactory().newThread(r);
        thread.setDaemon(true);
        return thread;
    });

    public static void main(String[] args) {
        HikariConfig hikariConfig = new HikariConfig();

        final MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl("jdbc:mysql:replication://localhost:3306/test");
        mysqlDataSource.setUser("root");
        mysqlDataSource.setPassword("password");
        hikariConfig.setDataSource(mysqlDataSource);

        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);


        // Create 10 threads which run tons of spam reads to the database.
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    Connection connection = hikariDataSource.getConnection();
                    while (true) {
                        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM test");

                        while (rs.next()) {
                            // Just print out the column as an example
                            System.out.println(rs.getString(2));
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
        }

        // Wait a bit for the above threads to start running
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Deadlock occurs here
        hikariDataSource.close();
    }
}