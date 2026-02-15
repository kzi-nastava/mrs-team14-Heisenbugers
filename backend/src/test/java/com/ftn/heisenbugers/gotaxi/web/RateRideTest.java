package com.ftn.heisenbugers.gotaxi.web;


import com.ftn.heisenbugers.gotaxi.web.pages.HistoryPage;
import com.ftn.heisenbugers.gotaxi.web.pages.HomePage;
import com.ftn.heisenbugers.gotaxi.web.pages.LoginPage;
import com.ftn.heisenbugers.gotaxi.web.pages.RatePage;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RateRideTest extends BaseTest {
    @Test
    void successfulLogin() throws InterruptedException {

        // Log in and navigate to history page
        HomePage homePage = new LoginPage(driver)
                .open("http://localhost:4200/auth/login")
                .enterUsername("acafaca00k@gmail.com")
                .enterPassword("password")
                .submit();
        HistoryPage historyPage = homePage.goToHistory();

        // Verify that the rate modal is not present before clicking the rate button
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        assertFalse(historyPage.isRateModalPresent());
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Click the rate button and verify that the modal appears
        RatePage ratePage = historyPage.clickRate();
        assertTrue(historyPage.isRateModalPresent());

        // Verify that the submit button is initially disabled and becomes enabled after providing ratings
        assertFalse(ratePage.isSubmitButtonEnabled());
        ratePage.rateDriver(3);
        assertFalse(ratePage.isSubmitButtonEnabled());
        ratePage.rateVehicle(2);
        assertTrue(ratePage.isSubmitButtonEnabled());

        // Enter a comment and submit the rating
        ratePage.enterComment("It was an average ride.");
        assertTrue(ratePage.isSubmitButtonEnabled());
        ratePage.submitRating();

        // Verify that the modal is closed and a toast message is displayed
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        assertFalse(historyPage.isRateModalPresent());
        assertTrue(historyPage.isToastMessagePresent());
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Verify that the toast message indicates a successful rating or that the user has already rated this ride
        assertTrue(
                historyPage.getToastMessage().contains("Rating recorded successfully!")
                        || historyPage.getToastMessage().contains("You have already rated this ride")
        );
    }

}
