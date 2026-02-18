package com.ftn.heisenbugers.gotaxi.e2e;

import com.ftn.heisenbugers.gotaxi.e2e.pages.BasePage;
import com.ftn.heisenbugers.gotaxi.e2e.pages.FavoritesPage;
import com.ftn.heisenbugers.gotaxi.e2e.pages.HomePage;
import com.ftn.heisenbugers.gotaxi.e2e.pages.LoginPage;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class OrderRideFromFavoritesTest extends TestBase{

    static final String EMAIL = "vasilicbosko@gmail.com";
    static final String PASSWORD = "bosko123";

    @Test
    public void orderRideFromFavorites(){
        HomePage home = new HomePage(driver);
        home.clickOnLogin();

        LoginPage login = new LoginPage(driver);
        login.enterEmail(EMAIL);
        login.enterPassword(PASSWORD);
        login.submit();

        BasePage base = new BasePage(driver);
        Assert.assertTrue(base.isLoggedIn());
        base.clickOnFavorites();

        FavoritesPage favorites = new FavoritesPage(driver);
        favorites.clickUseButton();

        Assert.assertTrue(base.isTimeCalculated());
        base.clickOnStandardVehicle();
        base.clickOnOrderRide();
    }

    @Test
    public void scheduleRideFromFavorites(){
        HomePage home = new HomePage(driver);
        home.clickOnLogin();

        LoginPage login = new LoginPage(driver);
        login.enterEmail(EMAIL);
        login.enterPassword(PASSWORD);
        login.submit();

        BasePage base = new BasePage(driver);
        Assert.assertTrue(base.isLoggedIn());
        base.clickOnFavorites();

        FavoritesPage favorites = new FavoritesPage(driver);
        favorites.clickUseButton();

        Assert.assertTrue(base.isTimeCalculated());
        base.clickOnStandardVehicle();
        base.clickOnScheduleRide();
        base.clickOnScheduleConfirmButton();

        Assert.assertTrue(base.isRideOrdered());
    }
}
