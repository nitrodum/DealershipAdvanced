package com.pluralsight;
import java.text.DecimalFormat;

public class SalesContract extends Contract{
    public static final double INTEREST_RATE_FOR_OVER_10000 = 4.25 / 100;
    public static final int LOAN_TERM_FOR_OVER_10000 = 48;
    public static final double INTEREST_RATE_FOR_UNDER_10000 = 5.25 / 100;
    public static final int LOAN_TERM_FOR_UNDER_10000 = 24;
    private double salesTax;
    private double recordingFee;
    private boolean financed;

    public SalesContract(String date, String customerName, String customerEmail, Vehicle sold, double salesTax, double recordingFee, boolean financed) {
        super(date, customerName, customerEmail, sold);
        this.salesTax = salesTax;
        this.recordingFee = recordingFee;
        this.financed = financed;
    }

    @Override
    public double getTotalPrice() {
        if (!financed) {
            return getTotalPriceNotFinanced();
        } else {
            return getTotalPriceFinanced();
        }
    }

    private double getTotalPriceNotFinanced() {
        double total = getSold().getPrice();
        return total + calculateSalesTax() + recordingFee + getProcessingFee();
    }

    private double getTotalPriceFinanced() {
        if (getSold().getPrice() >= 10_000) {
            return LOAN_TERM_FOR_OVER_10000 * getMonthlyPrice();
        } else {
            return LOAN_TERM_FOR_UNDER_10000 * getMonthlyPrice();
        }
    }

    @Override
    public double getMonthlyPrice() {
        if (!financed) {
            return 0;
        }
        double loanAmount = getTotalPriceNotFinanced();
        if (getSold().getPrice() >= 10_000) {
            return (loanAmount * (INTEREST_RATE_FOR_OVER_10000 / 12))/(1 - (Math.pow((1+(INTEREST_RATE_FOR_OVER_10000 / 12)), -LOAN_TERM_FOR_OVER_10000)));
        } else {
            return (loanAmount * (INTEREST_RATE_FOR_UNDER_10000 / 12))/(1 - (Math.pow((1+(INTEREST_RATE_FOR_UNDER_10000 / 12)), -LOAN_TERM_FOR_UNDER_10000)));
        }
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.00");

        String isFinanced = financed ? "YES" : "NO";

        return "Contract Type: SALE | Date: " + getDate() + " | Customer Name: " + getCustomerName() +
                " | Customer Email: " + getCustomerEmail() + " | " + getSold() +
                " | Sales Tax: " + df.format(calculateSalesTax()) + " | Recording Fee: " + df.format(recordingFee) +
                " | Processing Fee: " + df.format(getProcessingFee()) + " | Total: " + df.format(getTotalPrice()) +
                " | Financed: " + isFinanced + " | Monthly: " + df.format(getMonthlyPrice());
    }

    public double getSalesTax() {
        return salesTax;
    }

    public void setSalesTax(double salesTax) {
        this.salesTax = salesTax;
    }

    public double calculateSalesTax() {
        return getSold().getPrice() * salesTax/100;
    }

    public double getRecordingFee() {
        return recordingFee;
    }

    public void setRecordingFee(double recordingFee) {
        this.recordingFee = recordingFee;
    }

    public double getProcessingFee() {
        if (getSold().getPrice() >= 10_000) {
            return 495;
        } else {
            return 295;
        }
    }

    public boolean isFinanced() {
        return financed;
    }

    public void setFinanced(boolean financed) {
        this.financed = financed;
    }
}
