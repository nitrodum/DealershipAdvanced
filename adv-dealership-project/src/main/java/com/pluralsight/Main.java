package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

public class Main {
    private static BasicDataSource dataSource;

    public static void main(String[] args) {
        init(args);
        UserInterface ui = new UserInterface(dataSource);
        ui.display();
    }

    private static void init(String[] args) {
        String username = args[0];
        String password = args[1];
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/car_dealership_database");
        dataSource.setUsername(username);
        dataSource.setPassword(password);
    }

}