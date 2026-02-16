package com.ftn.heisenbugers.gotaxi.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class AdminRideDetailsModal {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By modal = By.cssSelector("[data-testid='ride-details-modal']");
    private final By closeBtn = By.cssSelector("[data-testid='ride-details-close']");

    public AdminRideDetailsModal(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        wait.until(ExpectedConditions.visibilityOfElementLocated(modal));
    }

    public void close() {
        wait.until(ExpectedConditions.elementToBeClickable(closeBtn)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modal));
    }

    public boolean isOpen() {
        return !driver.findElements(modal).isEmpty();
    }
}
