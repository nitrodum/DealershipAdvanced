package com.pluralsight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class DealershipFileManager {
    public DealershipFileManager() {

    }

    public Dealership getDealership() {
        try {
            BufferedReader bufferedReader = new BufferedReader( new FileReader("inventory.csv"));
            String input = bufferedReader.readLine();
            String[] dealershipInfo = input.split("\\|");
            Dealership dealership = new Dealership(dealershipInfo[0], dealershipInfo[1], dealershipInfo[2]);
            while ((input = bufferedReader.readLine()) != null) {
                String[] data = input.split("\\|");
                Vehicle vehicle = new Vehicle(Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2], data[3],
                        data[4], data[5], Integer.parseInt(data[6]), Double.parseDouble(data[7]));
                dealership.addVehicle(vehicle);
            }
            bufferedReader.close();
            return dealership;
        } catch (Exception e) {
            System.out.println("Error reading file");
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
