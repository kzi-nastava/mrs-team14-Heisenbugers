package com.ftn.heisenbugers.gotaxi.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HeaderComponent {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By profileBtn = By.id("profile-button");
    private final By historyBtn = By.id("history-button");
    private final By profileMenu = By.id("profile-menu-container");
    private final By adminDashboardBtn = By.cssSelector("[data-testid='admin-dashboard-button']");


    public HeaderComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openProfileMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(profileBtn)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(profileMenu));
    }

    public void goToHistory() {
        openProfileMenu();
        wait.until(ExpectedConditions.elementToBeClickable(historyBtn)).click();
    }
    public void goToAdminDashboard() {
        wait.until(ExpectedConditions.elementToBeClickable(adminDashboardBtn)).click();
    }

}
