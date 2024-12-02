package com.pluralsight;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeaseContractTest {

    @Test
    void getTotalPrice() {
        Vehicle test = new Vehicle("37846", 2021, "Chevorlet", "Silverado", "Truck", "Black", 2750, 31995);
        LeaseContract testContract = new LeaseContract("Today", "Test", "Test.com", test);

        double total = testContract.getTotalPrice();

        assertEquals(18337.15, total, .01);
    }

    @Test
    void getMonthlyPrice() {
        Vehicle test = new Vehicle("37846", 2021, "Chevorlet", "Silverado", "Truck", "Black", 2750, 31995);
        LeaseContract testContract = new LeaseContract("Today", "Test", "Test.com", test);

        double monthly = testContract.getMonthlyPrice();

        assertEquals(541.39, monthly, .01);
    }
}