package com.ftn.heisenbugers.gotaxi.e2e.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AdminDashboardPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    //private final By root = By.cssSelector("[data-testid='admin-dashboard-root']");
    private final By rideHistoryBtn = By.cssSelector("[data-testid='admin-ride-history-btn']");
    private final By adminRidesRoot = By.cssSelector("[data-testid='admin-rides-root']");

    public AdminDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        wait.until(ExpectedConditions.visibilityOfElementLocated(rideHistoryBtn));
    }

    public void goToRideHistory() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(rideHistoryBtn));

        try {
            btn.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", btn);
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(adminRidesRoot));
    }
}
