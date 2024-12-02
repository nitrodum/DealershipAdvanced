package com.pluralsight;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

class SalesContractTest {

    @BeforeEach
    void init() {
        Vehicle test = new Vehicle("12345", 2024, "Toyota", "Corolla", "Sedan", "Silver", 10_000, 20_000);

    }

    @org.junit.jupiter.api.Test
    void getTotalPrice_NotFinanced() {
        Vehicle test = new Vehicle("12345", 2024, "Toyota", "Corolla", "Sedan", "Silver", 10_000, 995);
        SalesContract testContract = new SalesContract("today", "Test", "Test.com", test, 5, 100, false);

        double totalPrice = testContract.getTotalPrice();

        assertEquals(1439.75, totalPrice);
    }

    @org.junit.jupiter.api.Test
    void getTotalPrice_Financed() {
        Vehicle test = new Vehicle("12345", 2024, "Toyota", "Corolla", "Sedan", "Silver", 10_000, 20_000);
        SalesContract testContract = new SalesContract("today", "Test", "Test.com", test, 5, 100, true);

        double totalPrice = testContract.getTotalPrice();

        assertEquals(23520.68, totalPrice, .01);
    }

    @org.junit.jupiter.api.Test
    void getMonthlyPrice() {
        Vehicle test = new Vehicle("12345", 2024, "Toyota", "Corolla", "Sedan", "Silver", 10_000, 20_000);
        SalesContract testContract = new SalesContract("today", "Test", "Test.com", test, 5, 100, true);

        double monthlyPrice = testContract.getMonthlyPrice();

        assertEquals(490.01, monthlyPrice, .01);
    }
}