package com.learn.auto.pages;

import com.learn.auto.base.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class ProductDetailsPage extends BasePage
{
    private final Locator productName;
    private final Locator productPrice;
    private final Locator backToProductsLink;
    private final Locator addToCartButton;

    public ProductDetailsPage(Page page)
    {
        super(page);
        productName = page.locator(".inventory_details_name");
        productPrice = page.locator(".inventory_details_price");
        backToProductsLink = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Back to products"));
        addToCartButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to cart"));
    }

    public Locator productName()
    {
        return productName;
    }

    public Locator productPrice()
    {
        return productPrice;
    }

    public Locator backToProductsLink()
    {
        return backToProductsLink;
    }

    public Locator addToCartButton()
    {
        return addToCartButton;
    }

    public void clickBackToProducts()
    {
        backToProductsLink.click();
    }

    public void addToCart()
    {
        addToCartButton.click();
    }
}
