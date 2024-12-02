package com.pluralsight;


import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;

public class ContractDAO {
    private BasicDataSource dataSource;

    public ContractDAO(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Contract> loadContracts() {
        List<Contract> contracts = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM salescontracts""");
                 ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Vehicle v = null;
                    try (PreparedStatement carStatement = connection.prepareStatement("""
                            SELECT * FROM vehicles
                            WHERE VIN = ?""")) {
                        carStatement.setString(1, rs.getString("VIN"));
                        try (ResultSet car = carStatement.executeQuery()) {
                            while (car.next()) {
                                v = new Vehicle(car.getString("VIN"), car.getInt("Year"), car.getString("Make"), car.getString("Model"),
                                        car.getString("VehicleType"), car.getString("Color"), car.getInt("Odometer"), car.getDouble("Price"));
                            }
                        }
                        SalesContract sc = new SalesContract(rs.getString("SaleDate"), rs.getString("CustomerName"), rs.getString("CustomerEmail"), v, 5, rs.getDouble("RecordingFee"),
                                rs.getBoolean("Financed"));
                        contracts.add(sc);
                    }

                }
            }

            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM leasecontracts""");
                 ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Vehicle v = null;
                    try (PreparedStatement carStatement = connection.prepareStatement("""
                            SELECT * FROM vehicles
                            WHERE VIN = ?""")) {
                        carStatement.setString(1, rs.getString("VIN"));
                        try (ResultSet car = carStatement.executeQuery()) {
                            while (car.next()) {
                                v = new Vehicle(car.getString("VIN"), car.getInt("Year"), car.getString("Make"), car.getString("Model"),
                                        car.getString("VehicleType"), car.getString("Color"), car.getInt("Odometer"), car.getDouble("Price"));
                            }
                        }
                        LeaseContract lc = new LeaseContract(rs.getString("LeaseDate"), rs.getString("CustomerName"), rs.getString("CustomerEmail"), v);
                        contracts.add(lc);
                    }

                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return contracts;
    }

    public void addContract(Contract contract) {
        Vehicle v = contract.getSold();

        try (Connection connection = dataSource.getConnection()) {
            if (contract instanceof SalesContract s) {
                try (PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO SalesContracts (SaleDate, CustomerName, CustomerEmail, VIN, SalesTax, RecordingFee,
                        Financed, TotalPrice, MonthlyPrice)
                        VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)""")) {
                    statement.setString(1, s.getDate());
                    statement.setString(2, s.getCustomerName());
                    statement.setString(3, s.getCustomerEmail());
                    statement.setString(4, v.getVin());
                    statement.setDouble(5, s.calculateSalesTax());
                    statement.setDouble(6, s.getRecordingFee());
                    statement.setBoolean(7, s.isFinanced());
                    statement.setDouble(8, s.getTotalPrice());
                    statement.setDouble(9, s.getMonthlyPrice());

                    int rows = statement.executeUpdate();
                    System.out.printf("Rows updated %d\n", rows);
                }
            } else if (contract instanceof LeaseContract l) {
                try (PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO LeaseContracts (LeaseDate, CustomerName, CustomerEmail, VIN, ExpectedEndValue,
                        TotalPrice, MonthlyPrice)
                        VALUES(?, ?, ?, ?, ?, ?, ?)""")) {
                    statement.setString(1, l.getDate());
                    statement.setString(2, l.getCustomerName());
                    statement.setString(3, l.getCustomerEmail());
                    statement.setString(4, v.getVin());
                    statement.setDouble(5, l.getExpectedEndValue());
                    statement.setDouble(6, l.getTotalPrice());
                    statement.setDouble(7, l.getMonthlyPrice());

                    int rows = statement.executeUpdate();
                    System.out.printf("Rows updated %d\n", rows);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
