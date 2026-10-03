package com.learn.auto.model;

import com.fasterxml.jackson.databind.JsonNode;

public class ProductItem
{
    private final String name;
    private final String price;

    public ProductItem(String name, String price)
    {
        this.name = name;
        this.price = price;
    }

    public static ProductItem fromJson(JsonNode product)
    {
        return new ProductItem(product.path("title").asText(), product.path("price").asText());
    }

    public String getName()
    {
        return name;
    }

    public String getPrice()
    {
        return price;
    }

    public double getPriceValue()
    {
        return Double.parseDouble(price.replace("$", ""));
    }
}
