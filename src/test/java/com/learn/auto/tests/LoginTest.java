package com.learn.auto.tests;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.learn.auto.base.BaseTest;
import com.learn.auto.pages.LoginPage;
import com.learn.auto.utilities.JsonReader;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Test class for login functionality. This class extends BaseTest to inherit the setup and teardown methods for Playwright.
 */
public class LoginTest extends BaseTest {
    
    @DataProvider(name = "loginData")
    public Object[][] loginDataProvider() {
        JsonNode users = JsonReader.readJsonFile("logincreds.json").path("users");
        Object[][] loginData = new Object[users.size()][2];

        for (int index = 0; index < users.size(); index++) {
            JsonNode user = users.get(index);
            loginData[index][0] = user.path("username").asText();
            loginData[index][1] = user.path("password").asText();
        }

        return loginData;
    }

    @Test(dataProvider = "loginData")
    public void testLogin(String username, String password) {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(username, password);

        assertThat(loginPage.openMenuButton()).isVisible();
    }

    @Test
    @Parameters({"username", "password"})
    public void testLoginWithLockedCredentials(String username, String password) {
        page.onConsoleMessage(handler -> { System.out.printf("Console log found : %s : %s", handler.type(), handler.text()); });
        page.onPageError(error -> { System.out.printf("Error found : %s", error); });

        LoginPage loginPage = new LoginPage(page);
        loginPage.login(username, password);

        assertThat(loginPage.lockedUserError()).isVisible();

        var crossIcons = loginPage.errorIcons();
        for (var crossIcon : crossIcons) {
            assertThat(crossIcon).isVisible();
        }
    }
}
