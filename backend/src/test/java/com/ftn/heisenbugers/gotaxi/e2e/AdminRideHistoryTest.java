package com.ftn.heisenbugers.gotaxi.e2e;

import com.ftn.heisenbugers.gotaxi.e2e.pages.*;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AdminRideHistoryTest extends TestBase {

    static final String ADMIN_EMAIL = "admin@admin.a";
    static final String ADMIN_PASS  = "123456";
    private final String HOME_URL   = "http://localhost:4200/home";

    //happy paths

    @Test
    public void adminPassengerHistory_sortByStartedAtDesc() {
        ensureAdminOnHistory();

        AdminRidesPage admin = new AdminRidesPage(driver);
        admin.openPassengersTab();

        AdminUserRidesModal modal = openFirstUserWithRides(admin, 8, "startedAt", "desc");
        //AdminUserRidesModal modal = openFirstUserWithRides(admin, 5, "createdAt", "desc");

        var rows = modal.rideRows();
        Assert.assertTrue(rows.size() >= 2, "Need at least 2 rides to validate sorting.");

        var pair = firstTwoWithStartedAt(modal);
        assertStartedAtDesc(modal.rideRows());
        String a = AdminUserRidesModal.startedAt(pair[0]);
        String b = AdminUserRidesModal.startedAt(pair[1]);
        //Assert.assertTrue(a.compareTo(b) >= 0, "Expected startedAt desc: first >= second");

        Assert.assertTrue(a != null && b != null, "startedAt should not be null for first two rides.");
        Assert.assertTrue(a.compareTo(b) >= 0, "Expected startedAt desc: first >= second");

        modal.close();
    }

    @Test
    public void adminDriverHistory_sortByPriceAsc() {
        ensureAdminOnHistory();

        AdminRidesPage admin = new AdminRidesPage(driver);
        admin.openDriversTab();

        AdminUserRidesModal modal = openFirstUserWithRides(admin, 8, "price", "asc");

        var rows = modal.rideRows();
        Assert.assertTrue(rows.size() >= 2, "Need at least 2 rides to validate sorting.");
        assertPriceAsc(modal.rideRows());

        double p1 = AdminUserRidesModal.parsePrice(rows.get(0));
        double p2 = AdminUserRidesModal.parsePrice(rows.get(1));

        Assert.assertTrue(!Double.isNaN(p1) && !Double.isNaN(p2), "Price should be numeric for first two rides.");
        Assert.assertTrue(p1 <= p2, "Expected price asc: first <= second");

        modal.close();
    }


    //error - exceptional

    @Test
    public void adminHistory_invalidFromDate_showsError() {
        ensureAdminOnHistory();

        AdminRidesPage admin = new AdminRidesPage(driver);
        admin.openPassengersTab();
        admin.openFirstUser();

        AdminUserRidesModal modal = new AdminUserRidesModal(driver);
        try {
            modal.setFrom("aaaa");
            modal.apply();

            Assert.assertTrue(modal.hasError(), "Expected rides-error to be visible for invalid date.");
        } finally {
            if (isModalStillOpen()) {
                try { modal.close(); } catch (Exception ignored) {}
            }
        }
    }



    //proverka ui
    /*

    @Test
    public void adminHistory_openRideDetails_andClose() {

        ensureAdminOnHistory();

        AdminRidesPage admin = new AdminRidesPage(driver);
        admin.openPassengersTab();


        AdminUserRidesModal modal = openFirstUserWithRides(admin, 5, "startedAt", "desc");

        Assert.assertFalse(modal.isEmpty(), "Need a user with rides.");

        var rows = modal.rideRows();
        Assert.assertTrue(rows.size() >= 1, "Need at least 1 ride row.");

        rows.get(0).click();

        AdminRideDetailsModal details = new AdminRideDetailsModal(driver);
        Assert.assertTrue(details.isOpen(), "Ride details modal should open.");

        details.close();
        Assert.assertFalse(details.isOpen(), "Ride details modal should close.");
    }
    */




    private AdminUserRidesModal openFirstUserWithRides(AdminRidesPage admin, int maxTries, String sortField, String sortDir) {
        int total = admin.getUserCount();
        int limit = Math.min(maxTries, total);

        for (int i = 0; i < limit; i++) {
            admin.openUserByIndex(i);
            AdminUserRidesModal modal = new AdminUserRidesModal(driver);


            modal.clearFrom();
            modal.clearTo();

            modal.setSortField(sortField);
            modal.setSortDir(sortDir);
            modal.apply();

            if (modal.hasError()) {
                String err = modal.errorText();
                modal.close();
                throw new AssertionError("Rides modal error for user index " + i + ": " + err);
            }

            if (!modal.isEmpty()) {
                return modal;
            }

            modal.close();
        }

        throw new AssertionError("Could not find a user with rides in first " + limit + " users.");
    }

    private void ensureAdminOnHistory() {
        driver.get(HOME_URL);

        if (!isLoggedIn()) {
            HomePage home = new HomePage(driver);
            home.clickOnLogin();

            LoginPage login = new LoginPage(driver);
            login.enterEmail(ADMIN_EMAIL);
            login.enterPassword(ADMIN_PASS);
            login.submit();

            waitVisible(By.id("profile-button"), 12);
        }

        HeaderComponent header = new HeaderComponent(driver);
        //header.goToHistory();
        header.goToAdminDashboard();
        AdminDashboardPage dash = new AdminDashboardPage(driver);
        dash.goToRideHistory();


        waitVisible(By.cssSelector("[data-testid='admin-rides-root']"), 12);
    }

    private boolean isLoggedIn() {
        try {
            waitVisible(By.id("profile-button"), 2);
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    private boolean isModalStillOpen() {
        return !driver.findElements(By.cssSelector("[data-testid='user-rides-modal']")).isEmpty();
    }

    private void waitVisible(By locator, int sec) {
        new WebDriverWait(driver, Duration.ofSeconds(sec))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private WebElement[] firstTwoWithStartedAt(AdminUserRidesModal modal) {
        var rows = modal.rideRows();
        WebElement first = null, second = null;

        for (var r : rows) {
            String s = AdminUserRidesModal.startedAt(r);
            if (s == null || s.isBlank()) continue;
            if (first == null) first = r;
            else { second = r; break; }
        }
        if (first == null || second == null)
            throw new AssertionError("Need at least 2 rides with non-null startedAt to validate sorting.");
        return new WebElement[]{first, second};
    }

    private void assertStartedAtDesc(List<WebElement> rows) {
        String prev = null;
        for (WebElement r : rows) {
            String cur = AdminUserRidesModal.startedAt(r);
            if (cur == null || cur.isBlank()) continue;
            if (prev != null) {
                Assert.assertTrue(prev.compareTo(cur) >= 0, "Expected startedAt desc order");
            }
            prev = cur;
        }
    }

    private void assertPriceAsc(List<WebElement> rows) {
        Double prev = null;
        for (WebElement r : rows) {
            double cur = AdminUserRidesModal.parsePrice(r);
            if (Double.isNaN(cur)) continue;
            if (prev != null) {
                Assert.assertTrue(prev <= cur, "Expected price asc order");
            }
            prev = cur;
        }
    }
}
