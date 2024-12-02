package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DealershipDAO {
    private Scanner scanner = new Scanner(System.in);
    private BasicDataSource dataSource;
    private Dealership dealership = null;

    public DealershipDAO(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Dealership getDealership() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM dealerships");
             ResultSet results = statement.executeQuery()) {
            System.out.printf("%-14s%-30s%-50s%-12s\n", "DealershipID", "Name", "Address", "Phone");
            System.out.printf("%-14s%-30s%-50s%-12s\n", "-".repeat(13), "-".repeat(29), "-".repeat(49), "-".repeat(11));
            while (results.next()) {
                System.out.printf("%-14s%-30s%-50s%-12s\n", results.getInt("DealershipID"), results.getString("Name"), results.getString("Address"), results.getString("Phone"));
            }
            System.out.println("Enter the dealership id of the dealership you would like to view: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            try (PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM dealerships WHERE DealershipID = ?")) {
                selectStatement.setInt(1, id);
                try (ResultSet selectResults = selectStatement.executeQuery()) {
                    while (selectResults.next()) {
                        dealership = new Dealership(selectResults.getInt("DealershipID"), selectResults.getString("Name"), selectResults.getString("Address"), selectResults.getString("Phone"));
                    }
                }
            }
            try (PreparedStatement loadStatement = connection.prepareStatement("""
                    SELECT v.* FROM dealerships as d
                    INNER JOIN inventory as i
                    ON d.dealershipID = i.dealershipID
                    INNER JOIN vehicles as v
                    ON i.VIN = v.VIN
                    WHERE d.dealershipID = ? AND v.Sold = False""")) {
                loadStatement.setInt(1, id);
                try (ResultSet loadResults = loadStatement.executeQuery()) {
                    while (loadResults.next()) {
                        Vehicle v = new Vehicle(loadResults.getString("VIN"), loadResults.getInt("Year"), loadResults.getString("Make"), loadResults.getString("Model"),
                                loadResults.getString("VehicleType"), loadResults.getString("Color"), loadResults.getInt("Odometer"), loadResults.getDouble("Price"));
                        if (dealership != null) {
                            dealership.addVehicle(v);
                        }
                    }
                }
            }
            return dealership;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void sellCar(Vehicle v) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE vehicles
                     SET Sold = True
                     WHERE VIN = ?""")
        ) {
            statement.setString(1, v.getVin());
            int rows = statement.executeUpdate();
            System.out.printf("Rows updated %d\n", rows);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addVehicle(Vehicle v) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO vehicles
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""")
        ) {
                statement.setString(1, v.getVin());
                statement.setInt(2, v.getYear());
                statement.setString(3, v.getMake());
                statement.setString(4, v.getModel());
                statement.setString(5, v.getVehicleType());
                statement.setString(6, v.getColor());
                statement.setInt(7, v.getOdometer());
                statement.setDouble(8, v.getPrice());
                statement.setBoolean(9, false);

                int rows = statement.executeUpdate();

                System.out.printf("Rows updated %d\n", rows);
        } try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO inventory
                    VALUES (?, ?)""")
            ) {
                statement.setInt(1, dealership.getId());
                statement.setString(2, v.getVin());

                int rows = statement.executeUpdate();

                System.out.printf("Rows updated %d\n", rows);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removeVehicle(Vehicle v) {

    }

    public void saveDealership(Dealership dealership) {
        try {
            FileWriter writer = new FileWriter("inventory.csv");
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(dealership.getName() + "|" + dealership.getAddress() + "|" + dealership.getPhoneNumber());
            bufferedWriter.newLine();
            for (Vehicle vehicle : dealership.getAllVehicles()) {
                bufferedWriter.write(vehicle.getVin() + "|" + vehicle.getYear() + "|" + vehicle.getMake() + "|" +
                        vehicle.getModel() + "|" + vehicle.getVehicleType() + "|" + vehicle.getColor() + "|" +
                        vehicle.getOdometer() + "|" + vehicle.getPrice());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println("Error writing to file");
        }
    }
}
