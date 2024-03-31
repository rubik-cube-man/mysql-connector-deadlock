package com.rubikcubeman.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.*;

public class MySQLDeadlockTest {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10, r -> {
        Thread thread = Executors.defaultThreadFactory().newThread(r);
        thread.setDaemon(true);
        return thread;
    });

    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql:replication://localhost:3306/test", "root", "password");

            Statement stmt = conn.createStatement();

            // Create 10 threads which run tons of spam reads to the database.
            for (int i = 0; i < 10; i++) {
                executorService.submit(() -> {
                    try {
                        while (true) {
                            ResultSet rs = stmt.executeQuery("SELECT * FROM test");

                            while (rs.next()) {
                                // Just print out the first column as an example
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
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}