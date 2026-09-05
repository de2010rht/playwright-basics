package com.learn.auto.tests;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.learn.auto.base.BaseTest;
import com.learn.auto.pages.LoginPage;
import com.learn.auto.pages.ProductCatalogPage;
import com.learn.auto.utilities.JsonReader;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Test class for product catalog functionality. This class extends BaseTest to inherit the setup and teardown methods for Playwright.
 */
public class ProductCatalogTest extends BaseTest {

    @DataProvider(name = "productDisplayOptions")
    public Object[][] productDisplayOptions() {
        return new Object[][] {
            { "Name (A to Z)" },
            { "Name (Z to A)" },
            { "Price (low to high)" },
            { "Price (high to low)" }
        };
    }

    @Test(dataProvider = "productDisplayOptions")
    public void verifyProductDisplayOptions(String sortOption) {
        JsonNode users = JsonReader.readJsonFile("logincreds.json").path("users");
        JsonNode firstUser = users.get(0);

        LoginPage loginPage = new LoginPage(page);
        loginPage.login(firstUser.path("username").asText(), firstUser.path("password").asText());

        ProductCatalogPage catalogPage = new ProductCatalogPage(page);
        assertThat(catalogPage.pageTitle()).hasText("Products");

        String sortValue = getSortOptionValue(sortOption);
        catalogPage.sortBy(sortValue);

        List<String> actualProductNames = catalogPage.productNames()
                .stream()
                .map(String::trim)
                .collect(Collectors.toList());

        List<Double> actualProductPrices = catalogPage.productPrices()
                .stream()
                .map(priceText -> Double.valueOf(priceText.replace("$", "")))
                .collect(Collectors.toList());

        List<ProductItem> expectedCatalog = buildExpectedCatalogFromJson(sortOption);

        List<String> expectedProductNames = expectedCatalog.stream().map(ProductItem::name).collect(Collectors.toList());
        List<Double> expectedProductPrices = expectedCatalog.stream().map(ProductItem::price).collect(Collectors.toList());

        switch (sortOption) {
            case "Name (A to Z)" -> {
                assertEquals(actualProductNames, expectedProductNames, "Names should be sorted A to Z from products.json");
                assertEquals(actualProductPrices, expectedProductPrices, "Prices should match the A to Z product order from products.json");
            }
            case "Name (Z to A)" -> {
                assertEquals(actualProductNames, expectedProductNames, "Names should be sorted Z to A from products.json");
                assertEquals(actualProductPrices, expectedProductPrices, "Prices should match the Z to A product order from products.json");
            }
            case "Price (low to high)" -> {
                assertEquals(actualProductPrices, expectedProductPrices, "Prices should be sorted low to high from products.json");
                assertEquals(actualProductNames, expectedProductNames, "Names should follow low-to-high product price order from products.json");
            }
            case "Price (high to low)" -> {
                assertEquals(actualProductPrices, expectedProductPrices, "Prices should be sorted high to low from products.json");
                assertEquals(actualProductNames, expectedProductNames, "Names should follow high-to-low product price order from products.json");
            }
            default -> fail("Unsupported sort option: " + sortOption);
        }
    }

    private String getSortOptionValue(String sortOption) {
        return switch (sortOption) {
            case "Name (A to Z)" -> "az";
            case "Name (Z to A)" -> "za";
            case "Price (low to high)" -> "lohi";
            case "Price (high to low)" -> "hilo";
            default -> throw new IllegalArgumentException("Unsupported sort option: " + sortOption);
        };
    }

    private List<ProductItem> buildExpectedCatalogFromJson(String sortOption) {
        JsonNode productsJson = JsonReader.readJsonFile("products.json").path("products");
        List<ProductItem> items = new ArrayList<>();

        for (JsonNode product : productsJson) {
            items.add(new ProductItem(
                    product.path("title").asText(),
                    Double.valueOf(product.path("price").asText().replace("$", ""))));
        }

        Comparator<ProductItem> comparator = switch (sortOption) {
            case "Name (A to Z)" -> Comparator.comparing(ProductItem::name, Comparator.naturalOrder());
            case "Name (Z to A)" -> Comparator.comparing(ProductItem::name, Comparator.reverseOrder());
            case "Price (low to high)" -> (left, right) -> {
                int result = Double.compare(left.price, right.price);
                return (result != 0) ? result : left.name.compareTo(right.name);
            };
            case "Price (high to low)" -> (left, right) -> {
                int result = Double.compare(right.price, left.price);
                return (result != 0) ? result : left.name.compareTo(right.name);
            };
            default -> throw new IllegalArgumentException("Unsupported sort option: " + sortOption);
        };

        return items.stream().sorted(comparator).collect(Collectors.toList());
    }

    private static class ProductItem {
        private final String name;
        private final Double price;

        ProductItem(String name, Double price) {
            this.name = name;
            this.price = price;
        }

        String name() {
            return name;
        }

        Double price() {
            return price;
        }
    }
}
