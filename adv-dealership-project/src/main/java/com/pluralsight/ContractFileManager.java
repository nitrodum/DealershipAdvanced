package com.pluralsight;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;

public class ContractFileManager {
    public ContractFileManager() {
    }

    public List<Contract> loadContracts() {
        List<Contract> contracts = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("contracts.csv"));
            String input;
            while (( input = bufferedReader.readLine()) != null) {
                String[] data = input.split("\\|");
                Vehicle v = new Vehicle(data[4], Integer.parseInt(data[5]), data[6], data[7],
                        data[8], data[9], Integer.parseInt(data[10]), Double.parseDouble(data[11]));
                if (data[0].equalsIgnoreCase("SALE")) {
                    boolean financed = data[14].equalsIgnoreCase("YES");

                    SalesContract salesContract = new SalesContract(data[1], data[2], data[3], v,
                            5, Double.parseDouble(data[13]), financed);

                    contracts.add(salesContract);
                } else if (data[0].equalsIgnoreCase("LEASE")) {
                    LeaseContract leaseContract = new LeaseContract(data[1], data[2], data[3], v);
                    contracts.add(leaseContract);
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("Error Reading File");
        }
        return contracts;
    }

    public void saveContract(Contract contract) {
        DecimalFormat df = new DecimalFormat("0.00"); // Create a DecimalFormat instance for two decimals
        try {
            FileWriter writer = new FileWriter("contracts.csv", true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.newLine();
            Vehicle v = contract.getSold();

            if (contract instanceof SalesContract s) {
                String financed = s.isFinanced() ? "YES" : "NO";

                bufferedWriter.write("SALE|" + s.getDate() + "|" + s.getCustomerName() + "|" + s.getCustomerEmail() +
                        "|" + v.getVin() + "|" + v.getYear() + "|" + v.getMake() + "|" + v.getModel() + "|" +
                        v.getVehicleType() + "|" + v.getColor() + "|" + v.getOdometer() + "|" +
                        df.format(v.getPrice()) + "|" +
                        df.format(s.calculateSalesTax()) + "|" +
                        df.format(s.getRecordingFee()) + "|" +
                        df.format(s.getProcessingFee()) + "|" +
                        df.format(s.getTotalPrice()) + "|" +
                        financed + "|" +
                        df.format(s.getMonthlyPrice()));

            } else if (contract instanceof LeaseContract l) {
                bufferedWriter.write("LEASE|" + l.getDate() + "|" + l.getCustomerName() + "|" + l.getCustomerEmail() +
                        "|" + v.getVin() + "|" + v.getYear() + "|" + v.getMake() + "|" + v.getModel() + "|" +
                        v.getVehicleType() + "|" + v.getColor() + "|" + v.getOdometer() + "|" +
                        df.format(v.getPrice()) + "|" +
                        df.format(l.getExpectedEndValue()) + "|" +
                        df.format(l.getLeaseFee()) + "|" +
                        df.format(l.getTotalPrice()) + "|" +
                        df.format(l.getMonthlyPrice()));
            }

            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println("Error writing to file");
        }
    }
}
