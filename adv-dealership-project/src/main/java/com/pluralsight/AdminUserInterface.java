package com.pluralsight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;

public class AdminUserInterface {
    private static Scanner scanner = new Scanner(System.in);
    private static List<Contract> contractList;

    public static void display() {
        System.out.println("""
                Welcome to the admin menu
                Choose one of the options
                1) Log in
                2) Create new account""");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> handleLogIn();
            case 2 -> handleAccountCreation();
        }
    }

    public static void adminMenu() {
        ContractFileManager loader = new ContractFileManager();
        contractList = loader.loadContracts();
        System.out.println("Choose an option:\n" +
                "1) View all contracts");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> displayAllContracts();
        }
    }

    private static void displayAllContracts() {
        for (Contract c : contractList) {
            System.out.println(c);
        }
    }

    private static void handleLogIn() {
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();
        String hash = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("user_data.txt"));
            String input;
            while ((input = bufferedReader.readLine()) != null) {
                String[] data = input.split("\\|");
                if (username.equals(data[0])) {
                    hash = data[1];
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("Error Reading File");
        }

        if (hash != null && hash.equals(hashPassword(password))) {
            System.out.println("Successfully logged in!");
            adminMenu();
        } else {
            System.out.println("Incorrect Credentials!");
        }
    }

    private static void handleAccountCreation() {
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();
        String hashedPassword = hashPassword(password);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("user_data.txt"));
            bufferedWriter.write(username + "|" + hashedPassword);
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println("Error Writing to File");
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
