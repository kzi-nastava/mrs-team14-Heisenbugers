package com.ftn.heisenbugers.gotaxi.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HistoryPage extends BasePage {
    public HistoryPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "rate-btn-0")
    private WebElement rateBtn;

    @FindBy(id = "toast")
    private WebElement toastMessage;

    public RatePage clickRate() {
        rateBtn.click();
        return new RatePage(driver);
    }

    public String getToastMessage() {
        return toastMessage.getText();
    }

    public boolean isToastMessagePresent() {
        return !driver.findElements(By.id("toast")).isEmpty();
    }

    public boolean isRateModalPresent() {
        return !driver.findElements(By.id("rate-modal")).isEmpty();
    }


}
