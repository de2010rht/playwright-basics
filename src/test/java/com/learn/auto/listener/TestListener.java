package com.learn.auto.listener;

import java.io.ByteArrayInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IConfigurationListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.learn.auto.base.BaseTest;
import com.microsoft.playwright.Page;

import io.qameta.allure.Allure;

/**
 * TestNG listener that captures all the events related to TestNG execution.
 * These events are captured and printed to the console so that they can be
 * fed to test reporting tools (e.g. ExtentReports, Allure, etc.).
 *
 * <p>This listener implements the following TestNG interfaces to capture
 * every relevant event:</p>
 * <ul>
 *   <li>{@link ISuiteListener} - suite level events (start/finish)</li>
 *   <li>{@link ITestListener} - test method level events (start/success/failure/skip)</li>
 *   <li>{@link IConfigurationListener} - configuration method events (before/after)</li>
 * </ul>
 */
public class TestListener implements ISuiteListener, ITestListener, IConfigurationListener
{
    private static final Logger LOGGER = LogManager.getLogger(TestListener.class);

    // ============================== ISuiteListener ==============================

    /**
     * Invoked when a test suite starts.
     *
     * @param suite the suite that is starting
     */
    @Override
    public void onStart(ISuite suite)
    {
        LOGGER.info("==================================================");
        LOGGER.info("[SUITE START] Suite Name: {}", suite.getName());
        LOGGER.info("==================================================");
    }

    /**
     * Invoked when a test suite finishes.
     *
     * @param suite the suite that has finished
     */
    @Override
    public void onFinish(ISuite suite)
    {
        LOGGER.info("==================================================");
        LOGGER.info("[SUITE FINISH] Suite Name: {}", suite.getName());
        LOGGER.info("==================================================");
    }

    // ============================== ITestListener ==============================

    /**
     * Invoked when a test (i.e. a <test> tag in testng.xml) starts.
     *
     * @param context the test context
     */
    @Override
    public void onStart(ITestContext context)
    {
        LOGGER.info("--------------------------------------------------");
        LOGGER.info("[TEST START] Test Name: {}", context.getName());
        LOGGER.info("--------------------------------------------------");
    }

    /**
     * Invoked when a test (i.e. a <test> tag in testng.xml) finishes.
     *
     * @param context the test context
     */
    @Override
    public void onFinish(ITestContext context)
    {
        LOGGER.info("--------------------------------------------------");
        LOGGER.info("[TEST FINISH] Test Name: {}", context.getName());
        LOGGER.info("  Passed: {}", context.getPassedTests().size());
        LOGGER.info("  Failed: {}", context.getFailedTests().size());
        LOGGER.info("  Skipped: {}", context.getSkippedTests().size());
        LOGGER.info("--------------------------------------------------");
    }

    /**
     * Invoked when a test method is about to start.
     *
     * @param result the result of the test method
     */
    @Override
    public void onTestStart(ITestResult result)
    {
        LOGGER.info("  [TEST METHOD START] {}", getTestDescription(result));
    }

    /**
     * Invoked when a test method succeeds.
     *
     * @param result the result of the test method
     */
    @Override
    public void onTestSuccess(ITestResult result)
    {
        LOGGER.info("  [TEST METHOD SUCCESS] {} | Duration: {} ms", getTestDescription(result),
                (result.getEndMillis() - result.getStartMillis()));
    }

    /**
     * Invoked when a test method fails.
     *
     * @param result the result of the test method
     */
    @Override
    public void onTestFailure(ITestResult result)
    {
        LOGGER.error("  [TEST METHOD FAILURE] {} | Duration: {} ms", getTestDescription(result),
                (result.getEndMillis() - result.getStartMillis()));
        if (result.getThrowable() != null)
        {
            LOGGER.error("    Exception: {}", result.getThrowable().getMessage());
        }
        attachScreenshot(result);
    }

    /**
     * Invoked when a test method is skipped.
     *
     * @param result the result of the test method
     */
    @Override
    public void onTestSkipped(ITestResult result)
    {
        LOGGER.warn("  [TEST METHOD SKIPPED] {}", getTestDescription(result));
        if (result.getThrowable() != null)
        {
            LOGGER.warn("    Reason: {}", result.getThrowable().getMessage());
        }
    }

    /**
     * Invoked when a test method fails but is within the success percentage.
     *
     * @param result the result of the test method
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result)
    {
        LOGGER.warn("  [TEST METHOD FAILED WITHIN SUCCESS %] {}", getTestDescription(result));
    }

    /**
     * Invoked when a test method fails due to a timeout.
     *
     * @param result the result of the test method
     */
    @Override
    public void onTestFailedWithTimeout(ITestResult result)
    {
        LOGGER.error("  [TEST METHOD FAILED WITH TIMEOUT] {}", getTestDescription(result));
        attachScreenshot(result);
    }

    // ============================== IConfigurationListener ==============================

    /**
     * Invoked when a configuration method (e.g. @BeforeMethod, @AfterMethod) succeeds.
     *
     * @param result the result of the configuration method
     */
    @Override
    public void onConfigurationSuccess(ITestResult result)
    {
        LOGGER.info("    [CONFIG SUCCESS] {}", getTestDescription(result));
    }

    /**
     * Invoked when a configuration method (e.g. @BeforeMethod, @AfterMethod) fails.
     *
     * @param result the result of the configuration method
     */
    @Override
    public void onConfigurationFailure(ITestResult result)
    {
        LOGGER.error("    [CONFIG FAILURE] {}", getTestDescription(result));
        if (result.getThrowable() != null)
        {
            LOGGER.error("      Exception: {}", result.getThrowable().getMessage());
        }
    }

    /**
     * Invoked when a configuration method (e.g. @BeforeMethod, @AfterMethod) is skipped.
     *
     * @param result the result of the configuration method
     */
    @Override
    public void onConfigurationSkip(ITestResult result)
    {
        LOGGER.warn("    [CONFIG SKIPPED] {}", getTestDescription(result));
    }

    // ============================== Helper Methods ==============================

    /**
     * Builds a human readable description of a test result for logging purposes.
     *
     * @param result the test result
     * @return a string describing the test class and method
     */
    private String getTestDescription(ITestResult result)
    {
        String className = result.getTestClass().getRealClass().getSimpleName();
        String methodName = result.getMethod().getMethodName();
        return className + "." + methodName;
    }

    private void attachScreenshot(ITestResult result)
    {
        if (!(result.getInstance() instanceof BaseTest baseTest))
        {
            return;
        }

        Page page = baseTest.getPage();
        if (page == null || page.isClosed())
        {
            return;
        }

        try
        {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            Allure.addAttachment("Failure screenshot", "image/png", new ByteArrayInputStream(screenshot), ".png");
        }
        catch (RuntimeException exception)
        {
            LOGGER.warn("    Could not attach failure screenshot: {}", exception.getMessage());
        }
    }
}