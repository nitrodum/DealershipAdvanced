package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DealershipDAO {
    private Scanner scanner = new Scanner(System.in);
    private BasicDataSource dataSource;

    public DealershipDAO(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Dealership getDealership() {
        Dealership dealership = null;
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
                        dealership = new Dealership(selectResults.getString("Name"), selectResults.getString("Address"), selectResults.getString("Phone"));
                    }
                }
            }
            try (PreparedStatement loadStatement = connection.prepareStatement("""
                    SELECT v.* FROM dealerships as d
                    INNER JOIN inventory as i
                    ON d.dealershipID = i.dealershipID
                    INNER JOIN vehicles as v
                    ON i.VIN = v.VIN
                    WHERE d.dealershipID = ?
                    """)) {
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
