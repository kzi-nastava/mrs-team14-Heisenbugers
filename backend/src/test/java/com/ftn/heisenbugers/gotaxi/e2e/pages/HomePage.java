package com.ftn.heisenbugers.gotaxi.e2e.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {
    private WebDriver driver;

    private static String PAGE_URL="http://localhost:4200/home";

    @FindBy(id = "login-button")
    private WebElement loginButton;

    public HomePage(WebDriver driver){
        this.driver=driver;
        driver.get(PAGE_URL);

        PageFactory.initElements(driver, this);
    }

    public void clickOnLogin(){
        loginButton.click();
    }
}
