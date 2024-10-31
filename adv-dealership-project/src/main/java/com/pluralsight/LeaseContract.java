package com.pluralsight;

import java.text.DecimalFormat;

public class LeaseContract extends Contract {
    private final int LEASE_TERM = 36;
    private final double LEASE_RATE = 7.0 / 100;
    private final double INTEREST_RATE = 4.0;

    public LeaseContract(String date, String customerName, String customerEmail, Vehicle sold) {
        super(date, customerName, customerEmail, sold);

    }

    @Override
    public double getTotalPrice() {
        return getMonthlyPrice()*LEASE_TERM;
    }

    @Override
    public double getMonthlyPrice() {
        double monthlyDeprecation = getExpectedEndValue()/LEASE_TERM;
        double financeCharge = (getExpectedEndValue() + getSold().getPrice())*INTEREST_RATE/2400;
        double tax = (financeCharge + monthlyDeprecation) * LEASE_RATE;
        return monthlyDeprecation + financeCharge + tax;

    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.00");

        return "Contract Type: LEASE | Date: " + getDate() + " | Customer Name: " + getCustomerName() +
                " | Customer Email: " + getCustomerEmail() + " | " + getSold() +
                " | Expected End Value: " + df.format(getExpectedEndValue()) + " | Lease Fee: " + df.format(getLeaseFee()) +
                " | Total: " + df.format(getTotalPrice()) + " | Monthly: " + df.format(getMonthlyPrice());
    }

    public double getExpectedEndValue() {
        return getSold().getPrice()/2;
    }

    public double getLeaseFee() {
        return getSold().getPrice() * LEASE_RATE;
    }

}
