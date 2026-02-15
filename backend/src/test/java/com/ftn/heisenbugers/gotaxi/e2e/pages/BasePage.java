package com.ftn.heisenbugers.gotaxi.e2e.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {
    private WebDriver driver;

    @FindBy(id = "favorites-button")
    private WebElement favoritesButton;

    @FindBy(id = "order-ride-button")
    private WebElement orderRideButton;

    @FindBy(id = "standard-vehicle-button")
    private WebElement standardVehicleButton;

    @FindBy(id = "time-calculation")
    private WebElement timeCalculationField;

    public BasePage(WebDriver driver) {
        this.driver=driver;

        PageFactory.initElements(driver, this);
    }

    public void clickOnFavorites(){
        (new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.elementToBeClickable(favoritesButton)
        )).click();
    }

    public void clickOnStandardVehicle(){
        (new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.elementToBeClickable(standardVehicleButton)
        )).click();
    }

    public boolean isTimeCalculated(){
        boolean isCalculated = (new WebDriverWait(driver, Duration.ofSeconds(10)))
                .until(ExpectedConditions.textToBePresentInElement(timeCalculationField, "MIN"));

        return isCalculated;
    }

    public void clickOnOrderRide(){
        orderRideButton.click();
    }


}
