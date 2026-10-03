package com.learn.auto.pages;

import java.util.List;

import com.learn.auto.base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ProductCatalogPage extends BasePage
{
    private final Locator pageTitle;
    private final Locator sortDropdown;
    private final Locator productNames;
    private final Locator productPrices;

    public ProductCatalogPage(Page page)
    {
        super(page);
        pageTitle = page.locator(".title");
        sortDropdown = page.locator("select[data-test='product-sort-container']");
        productNames = page.locator(".inventory_item_name");
        productPrices = page.locator(".inventory_item_price");
    }

    public Locator pageTitle()
    {
        return pageTitle;
    }

    public void sortBy(String sortValue)
    {
        sortDropdown.selectOption(sortValue);
    }

    public List<String> productNames()
    {
        return productNames.allTextContents();
    }

    public List<String> productPrices()
    {
        return productPrices.allTextContents();
    }

    public Locator productByName(String productName)
    {
        return productNames.filter(new Locator.FilterOptions().setHasText(productName));
    }

    public void openProduct(String productName)
    {
        productByName(productName).click();
    }
}