package com.learn.auto.tests;

import java.util.concurrent.ThreadLocalRandom;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.learn.auto.base.BaseTest;
import com.learn.auto.model.ProductItem;
import com.learn.auto.pages.LoginPage;
import com.learn.auto.pages.ProductCatalogPage;
import com.learn.auto.pages.ProductDetailsPage;
import com.learn.auto.utilities.JsonReader;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ProductDetailsTest extends BaseTest
{
    @Test
    public void verifyProductDetailsPage()
    {
        JsonNode users = JsonReader.readJsonFile("logincreds.json").path("users");
        JsonNode firstUser = users.get(0);

        LoginPage loginPage = new LoginPage(page);
        loginPage.login(firstUser.path("username").asText(), firstUser.path("password").asText());

        ProductItem selectedProduct = chooseRandomProduct();

        ProductCatalogPage catalogPage = new ProductCatalogPage(page);
        catalogPage.openProduct(selectedProduct.getName());

        ProductDetailsPage detailsPage = new ProductDetailsPage(page);

        assertThat(detailsPage.productName()).hasText(selectedProduct.getName());
        assertThat(detailsPage.productPrice()).hasText(selectedProduct.getPrice());
        assertThat(detailsPage.backToProductsLink()).hasText("Back to products");
        assertThat(detailsPage.addToCartButton()).hasText("Add to cart");

        assertEquals(detailsPage.productName().textContent().trim(), selectedProduct.getName(),
                "Product name should match the selected product");
        assertEquals(detailsPage.productPrice().textContent().trim(), selectedProduct.getPrice(),
                "Product price should match the selected product");
        assertEquals(detailsPage.backToProductsLink().textContent().trim(), "Back to products",
                "Back to products link should be visible");
        assertEquals(detailsPage.addToCartButton().textContent().trim(), "Add to cart",
                "Add to cart button should be visible");
    }

    private ProductItem chooseRandomProduct()
    {
        JsonNode products = JsonReader.readJsonFile("products.json").path("products");
        int randomIndex = ThreadLocalRandom.current().nextInt(products.size());
        JsonNode product = products.get(randomIndex);
        return ProductItem.fromJson(product);
    }
}
