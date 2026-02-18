package com.ftn.heisenbugers.gotaxi.web.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class RatePage extends BasePage {
    public RatePage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//ng-icon[starts-with(@id,'driver-star-')]")
    private List<WebElement> driverStars;

    @FindBy(xpath = "//ng-icon[starts-with(@id,'vehicle-star-')]")
    private List<WebElement> vehicleStars;

    @FindBy(id = "comment-field")
    private WebElement commentField;

    @FindBy(id = "submit-button")
    private WebElement submitButton;

    public void rateDriver(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        driverStars.get(rating - 1).click();
    }

    public void rateVehicle(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        vehicleStars.get(rating - 1).click();
    }

    public void enterComment(String comment) {
        commentField.sendKeys(comment);
    }

    public void submitRating() {
        submitButton.click();
    }

    public boolean isSubmitButtonEnabled() {
        return submitButton.isEnabled();
    }
}
