package com.learn.auto.base;

import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.learn.auto.utilities.ConfigManager;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * Parent class for all test classes. Each test method gets its own isolated Playwright lifecycle
 * to make parallel TestNG execution safe and deterministic.
 */
public class BaseTest
{
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    protected Page page;

    /**
     * This method is executed before each test method. It creates a brand-new browser context
     * and page so tests can run independently in parallel.
     */
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod()
    {
        ConfigManager config = ConfigManager.getInstance();
        String browserType = config.getProperty("DEFAULT_BROWSER", "chromium");
        boolean headless = Boolean.parseBoolean(config.getProperty("HEADLESS", "false"));
        String appUrl = config.getProperty("APP_URL");
        int timeout = Integer.parseInt(config.getProperty("TIMEOUT", "15"));

        playwright = Playwright.create();

        List<String> browserArgs = headless ? List.of() : List.of("--start-maximized");

        switch (browserType.toLowerCase())
        {
            case "firefox":
                browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(headless).setArgs(browserArgs));
                break;
            case "webkit":
                browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(headless).setArgs(browserArgs));
                break;
            case "chromium":
            default:
                browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless).setArgs(browserArgs));
                break;
        }

        context = browser.newContext(new Browser.NewContextOptions().setViewportSize(null));
        context.setDefaultNavigationTimeout(timeout * 1000);
        context.setDefaultTimeout(timeout * 1000);

        page = context.newPage();
        if (appUrl != null && !appUrl.isEmpty())
        {
            page.navigate(appUrl, new Page.NavigateOptions().setTimeout(10000));
        }
    }

    /**
     * This method is executed after each test method. It closes the page, context, browser, and Playwright
     * instance used by that test only.
     */
    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        if (page != null)
        {
            page.close();
            page = null;
        }

        if (context != null)
        {
            context.close();
            context = null;
        }

        if (browser != null)
        {
            browser.close();
            browser = null;
        }

        if (playwright != null)
        {
            playwright.close();
            playwright = null;
        }
    }

    public Page getPage()
    {
        return page;
    }
}
