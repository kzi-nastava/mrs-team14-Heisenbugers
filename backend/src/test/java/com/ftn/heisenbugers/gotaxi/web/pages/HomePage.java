package com.ftn.heisenbugers.gotaxi.web.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

    @FindBy(id = "profile-button")
    private WebElement profileButton;

    @FindBy(id = "history-button")
    private WebElement historyButton;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HistoryPage goToHistory() {
        profileButton.click();
        historyButton.click();
        return new HistoryPage(driver);
    }
}
