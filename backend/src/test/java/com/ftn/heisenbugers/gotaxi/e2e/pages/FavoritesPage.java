package com.ftn.heisenbugers.gotaxi.e2e.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FavoritesPage {
    private WebDriver driver;

    @FindBy(id = "route-use-button-0")
    private WebElement useFavoriteRouteButton;

    public FavoritesPage(WebDriver driver) {
        this.driver=driver;

        PageFactory.initElements(driver, this);
    }

    public void clickUseButton(){
        (new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                ExpectedConditions.elementToBeClickable(useFavoriteRouteButton)
        )).click();
    }

}
