package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.util.List;
import java.util.Scanner;

public class UserInterface {
    BasicDataSource dataSource;
    private Dealership dealership;
    private List<Contract> contracts;
    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    public UserInterface(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void init() {
        DealershipDAO loader = new DealershipDAO(dataSource);
        this.dealership = loader.getDealership();
        ContractFileManager contractLoader = new ContractFileManager();
        this.contracts = contractLoader.loadContracts();
    }

    public void close() {
//        DealershipDAO saver = new DealershipDAO(dataSource);
//        saver.saveDealership(this.dealership);
//        scanner.close();
    }

    public void display() {
        init();
        while (running) {
            menu();
        }
        close();
    }

    private void menu() {
        System.out.println("Welcome to our Dealership!\n" +
                "Please enter the number that corresponds to the command:\n" +
                "1) Search by Price\n" +
                "2) Search by Make and Model\n" +
                "3) Search by Year\n" +
                "4) Search by Color\n" +
                "5) Search by Mileage\n" +
                "6) Search by Vehicle Type\n" +
                "7) Search All\n" +
                "8) Add Vehicle\n" +
                "9) Remove Vehicle\n" +
                "A) Admin Menu\n" +
                "B) Buy Car\n" +
                "0) Exit");

        String choice = scanner.nextLine().toLowerCase();
        switch (choice) {
            case "1":
                processGetByPriceRequest();
                break;
            case "2":
                processGetByMakeModelRequest();
                break;
            case "3":
                processGetByYearRequest();
                break;
            case "4":
                processGetByColorRequest();
                break;
            case "5":
                processGetByMileageRequest();
                break;
            case "6":
                processGetByVehicleTypeRequest();
                break;
            case "7":
                processGetAllVehiclesRequest();
                break;
            case "8":
                processAddVehicleRequest();
                break;
            case "9":
                processRemoveVehicleRequest();
                break;
            case "a":
                AdminMenu();
                break;
            case "b":
                processAddContract();
                break;
            case "0":
                running = false;
                break;
            default:
                System.out.println("Invalid Option, Please Choose a Valid Option");
        }
        System.out.println("Enter any input to continue");
        String buffer = scanner.nextLine();
    }

    private void processAddContract() {
        processGetAllVehiclesRequest();
        System.out.println("Enter the vin of the car you would like to purchase:");
        String vin = scanner.nextLine();

        Vehicle carToBuy = null;
        for (Vehicle v : dealership.getAllVehicles()){
            if (vin.equals(v.getVin())) {
                carToBuy = v;
            }
        }

        System.out.println("Enter the date of the purchase");
        String date = scanner.nextLine();

        System.out.println("Enter your name");
        String name = scanner.nextLine();

        System.out.println("Enter your email");
        String email = scanner.nextLine();

        System.out.println("Would you like to buy the car or lease the car? (buy/lease)");
        String choice = scanner.nextLine();
        ContractFileManager saver = new ContractFileManager();
        if (choice.equalsIgnoreCase("buy")) {
            System.out.println("Would you like to finance this car? (yes/no)");
            choice = scanner.nextLine();

            boolean finance = false;
            if (choice.equalsIgnoreCase("yes")) {
                finance = true;
            }

            SalesContract salesContract = new SalesContract(date, name, email, carToBuy, 5, 100, finance);
            contracts.add(salesContract);
             saver.saveContract(salesContract);
        } else {
            LeaseContract leaseContract = new LeaseContract(date, name, email, carToBuy);
            contracts.add(leaseContract);
            saver.saveContract(leaseContract);
        }
//        DealershipDAO dealershipSaver = new DealershipDAO();
//        dealershipSaver.saveDealership(dealership);
//        dealership.removeVehicle(carToBuy);
    }

    private void AdminMenu() {
        AdminUserInterface.display();
    }

    private void processGetByPriceRequest() {
        System.out.println("Enter the minimum price to search for:");
        int min = scanner.nextInt();
        System.out.println("Enter the maximum price to search for:");
        int max = scanner.nextInt();
        scanner.nextLine();

        List<Vehicle> filteredByPrice = dealership.getVehiclesByPrice(min, max);
        displayVehicles(filteredByPrice);
    }

    private void processGetByMakeModelRequest() {
        System.out.println("Enter the make of the car:");
        String make = scanner.nextLine();
        System.out.println("Enter the model of the car");
        String model = scanner.nextLine();

        List<Vehicle> filteredByMakeModel = dealership.getVehiclesByMakeModel(make, model);
        displayVehicles(filteredByMakeModel);
    }

    private void processGetByYearRequest() {
        System.out.println("Enter the start year to search for:");
        int min = scanner.nextInt();
        System.out.println("Enter the end year to search for:");
        int max = scanner.nextInt();
        scanner.nextLine();

        List<Vehicle> filteredByYear = dealership.getVehiclesByYear(min, max);
        displayVehicles(filteredByYear);
    }

    private void processGetByColorRequest() {
        System.out.println("Enter the color to search for:");
        String color = scanner.nextLine();

        List<Vehicle> filteredByColor = dealership.getVehiclesByColor(color);
        displayVehicles(filteredByColor);
    }

    private void processGetByMileageRequest() {
        System.out.println("Enter the minimum mileage to search for:");
        int min = scanner.nextInt();
        System.out.println("Enter the maximum mileage to search for:");
        int max = scanner.nextInt();
        scanner.nextLine();

        List<Vehicle> filteredByMileage = dealership.getVehiclesByMileage(min, max);
        displayVehicles(filteredByMileage);
    }

    private void processGetByVehicleTypeRequest() {
        System.out.println("Enter the vehicle type to search for:");
        String type = scanner.nextLine();

        List<Vehicle> filteredByType = dealership.getVehiclesByType(type);
        displayVehicles(filteredByType);
    }

    private void processGetAllVehiclesRequest() {
        List<Vehicle> allVehicles = dealership.getAllVehicles();
        displayVehicles(allVehicles);
    }

    private void processAddVehicleRequest() {
        System.out.println("Enter the vin of the car:");
        String vin = scanner.nextLine();
        System.out.println("Enter the year of the car:");
        int year = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the make of the car:");
        String make = scanner.nextLine();
        System.out.println("Enter the model of the car:");
        String model = scanner.nextLine();
        System.out.println("Enter the type of the car:");
        String type = scanner.nextLine();
        System.out.println("Enter the color of the car:");
        String color = scanner.nextLine();
        System.out.println("Enter the mileage of the car:");
        int mileage = scanner.nextInt();
        System.out.println("Enter the price of the car:");
        double price = scanner.nextDouble();
        scanner.nextLine();

        Vehicle vehicle = new Vehicle(vin, year, make, model, type, color, mileage, price);
        dealership.addVehicle(vehicle);

//        DealershipDAO saver = new DealershipDAO();
//        saver.saveDealership(this.dealership);
    }

    private void processRemoveVehicleRequest() {
        System.out.println("Enter the VIN of the vehicle you want to remove");
        String vin = scanner.nextLine();
        Vehicle toRemove = null;
        for (Vehicle vehicle : dealership.getAllVehicles()) {
            if (vin.equals(vehicle.getVin())) {
                toRemove = vehicle;
            }
        }
        if (toRemove != null) {
            dealership.removeVehicle(toRemove);
        }

//        DealershipDAO saver = new DealershipDAO();
//        saver.saveDealership(this.dealership);
    }

    private void displayVehicles(List<Vehicle> vehicles) {
        for (Vehicle vehicle : vehicles) {
            System.out.println(vehicle);
        }
    }
}
