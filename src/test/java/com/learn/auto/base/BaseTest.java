package com.learn.auto.base;

import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.learn.auto.utilities.ConfigManager;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * Parent class for all the test classes. It contains the setup and teardown methods for Playwright.
 */
public class BaseTest
{
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    protected Page page;

    /**
     * This method is executed before the test suite starts. It initializes the Playwright instance
     * and launches a browser based on the configuration from default.properties.
     */
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite()
    {
        ConfigManager config = ConfigManager.getInstance();
        String browserType = config.getProperty("DEFAULT_BROWSER", "chromium");
        boolean headless = Boolean.parseBoolean(config.getProperty("HEADLESS", "false"));

        playwright = Playwright.create();

        switch (browserType.toLowerCase())
        {
            case "firefox":
                browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(headless).setArgs(List.of("--start-maximized")));
                break;
            case "webkit":
                browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(headless).setArgs(List.of("--start-maximized")));
                break;
            case "chromium":
            default:
                browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless).setArgs(List.of("--start-maximized")));
                break;
        }

        context = browser.newContext(new Browser.NewContextOptions().setViewportSize(null));
    }

    /**
     * This method is executed before each test method. It creates a new page in the browser
     * and navigates to the URL specified in default.properties.
     */
    @BeforeMethod
    public void beforeMethod()
    {
        String appUrl = ConfigManager.getInstance().getProperty("APP_URL");
        int timeout = Integer.parseInt(ConfigManager.getInstance().getProperty("TIMEOUT", "15"));

        context.setDefaultNavigationTimeout(timeout * 1000);
        context.setDefaultTimeout(timeout * 1000);

        page = context.newPage();
        if (appUrl != null && !appUrl.isEmpty())
        {
            page.navigate(appUrl, new Page.NavigateOptions().setTimeout( 10000));
        }
    }

    /**
     * This method is executed after each test method. It closes the page.
     */
    @AfterMethod
    public void afterMethod()
    {
        page.close();
    }

    public Page getPage()
    {
        return page;
    }

    /**
     * This method is executed after the test suite ends. It closes the Playwright instance and releases resources.
     */
    @AfterSuite(alwaysRun = true)
    public void afterSuite()
    {
        if (playwright != null)
        {
            playwright.close();
            playwright = null;
        }
    }
}
