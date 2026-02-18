package com.ftn.heisenbugers.gotaxi.e2e.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class AdminUserRidesModal {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By modal = By.cssSelector("[data-testid='user-rides-modal']");
    private final By fromInput = By.cssSelector("[data-testid='filter-from']");
    private final By toInput = By.cssSelector("[data-testid='filter-to']");
    private final By sortField = By.cssSelector("[data-testid='sort-field']");
    private final By sortDir = By.cssSelector("[data-testid='sort-dir']");
    private final By applyBtn = By.cssSelector("[data-testid='apply-filter']");

    private final By ridesList = By.cssSelector("[data-testid='rides-list']");
    private final By rideRow = By.cssSelector("[data-testid='ride-row']");
    private final By ridesEmpty = By.cssSelector("[data-testid='rides-empty']");
    private final By ridesError = By.cssSelector("[data-testid='rides-error']");

    private final By closeBtn = By.cssSelector("[data-testid='modal-close']");


    public AdminUserRidesModal(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        wait.until(ExpectedConditions.visibilityOfElementLocated(modal));
    }
    public void close() {
        wait.until(ExpectedConditions.elementToBeClickable(closeBtn)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modal));
    }


    public void clearFrom() { setFrom(""); }
    public void clearTo() { setTo(""); }

    public void setSortField(String value) {
        new Select(wait.until(ExpectedConditions.elementToBeClickable(sortField))).selectByValue(value);
    }

    public void setSortDir(String value) {
        new Select(wait.until(ExpectedConditions.elementToBeClickable(sortDir))).selectByValue(value);
    }

    public void apply() {
        wait.until(ExpectedConditions.elementToBeClickable(applyBtn)).click();
        wait.until(d ->
                !d.findElements(ridesList).isEmpty() ||
                        !d.findElements(ridesEmpty).isEmpty() ||
                        !d.findElements(ridesError).isEmpty()
        );
    }

    public List<WebElement> rideRows() {
        wait.until(d ->
                !d.findElements(rideRow).isEmpty() ||
                        !d.findElements(ridesEmpty).isEmpty() ||
                        !d.findElements(ridesError).isEmpty()
        );
        return driver.findElements(rideRow);
    }

    public boolean isEmpty() {
        return !driver.findElements(ridesEmpty).isEmpty();
    }

    public boolean hasError() {
        return !driver.findElements(ridesError).isEmpty();
    }

    public String errorText() {
        List<WebElement> els = driver.findElements(ridesError);
        return els.isEmpty() ? "" : els.get(0).getText();
    }

    public static double parsePrice(WebElement row) {
        String raw = row.getAttribute("data-price");
        if (raw == null || raw.isBlank()) return Double.NaN;
        return Double.parseDouble(raw);
    }

    public static String startedAt(WebElement row) {
        return row.getAttribute("data-startedat");
    }
    public static String createdAt(WebElement row) {
        return row.getAttribute("data-createdat");
    }


    public void setFrom(String value) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(fromInput));
        el.click();
        el.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        el.sendKeys(Keys.DELETE);
        if (value != null && !value.isEmpty()) el.sendKeys(value);
    }

    public void setTo(String value) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(toInput));
        el.click();
        el.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        el.sendKeys(Keys.DELETE);
        if (value != null && !value.isEmpty()) el.sendKeys(value);
    }
}
