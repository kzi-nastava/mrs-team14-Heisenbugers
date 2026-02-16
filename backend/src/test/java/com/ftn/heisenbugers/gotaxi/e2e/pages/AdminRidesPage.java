package com.ftn.heisenbugers.gotaxi.e2e.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class AdminRidesPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By root = By.cssSelector("[data-testid='admin-rides-root']");
    private final By tabPassengers = By.cssSelector("[data-testid='tab-passengers']");
    private final By tabDrivers = By.cssSelector("[data-testid='tab-drivers']");
    private final By usersList = By.cssSelector("[data-testid='users-list']");
    private final By userItem = By.cssSelector("[data-testid='user-item']");
    private final By modal = By.cssSelector("[data-testid='user-rides-modal']");

    private final By userSearch = By.cssSelector("[data-testid='user-search']");

    public AdminRidesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        wait.until(ExpectedConditions.visibilityOfElementLocated(root));
    }

    public void openPassengersTab() {
        wait.until(ExpectedConditions.elementToBeClickable(tabPassengers)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(usersList));
    }

    public void openDriversTab() {
        wait.until(ExpectedConditions.elementToBeClickable(tabDrivers)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(usersList));
    }

    public void openFirstUser() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(usersList));
        List<WebElement> items = wait.until(d -> d.findElements(userItem));
        if (items.isEmpty()) throw new AssertionError("No users found to open.");
        items.get(0).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(modal));
    }

    public void openUserByIndex(int index) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(usersList));
        List<WebElement> items = wait.until(d -> d.findElements(userItem));
        if (items.size() <= index) throw new AssertionError("Not enough users.");
        items.get(index).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(modal));
    }

    public int getUserCount() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(usersList));
        return driver.findElements(userItem).size();
    }


    public void searchUser(String text) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(userSearch));
        el.click();
        el.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        el.sendKeys(Keys.DELETE);
        el.sendKeys(text);
        wait.until(ExpectedConditions.visibilityOfElementLocated(usersList));
    }

}
