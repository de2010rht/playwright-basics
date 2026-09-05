package com.learn.auto.pages;

import java.util.List;

import com.learn.auto.base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class LoginPage extends BasePage
{
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator lockedUserError;
    private final Locator openMenuButton;
    private final Locator errorIcons;

    public LoginPage(Page page)
    {
        super(page);
        usernameInput = page.getByPlaceholder("Username");
        passwordInput = page.getByPlaceholder("Password");
        loginButton = page.locator("#login-button");
        lockedUserError = page.getByText("Epic sadface: Sorry, this user has been locked out.");
        openMenuButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Open Menu"));
        errorIcons = page.locator("svg[data-prefix='fas']");
    }

    public void login(String username, String password)
    {
        usernameInput.fill(username);
        passwordInput.fill(password);
        loginButton.click();
    }

    public Locator openMenuButton()
    {
        return openMenuButton;
    }

    public Locator lockedUserError()
    {
        return lockedUserError;
    }

    public List<Locator> errorIcons()
    {
        return errorIcons.all();
    }
}