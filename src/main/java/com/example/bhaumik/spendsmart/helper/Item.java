package com.example.bhaumik.spendsmart.helper;

public class Item {

    private String item_name;
    private String item_price;
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Item() {
    }

    public Item(String name, String price, String category)
    {
       this.item_name=name;
       this.category=category;
       this.item_price=price;
    }
    public Item(String name, String price)
    {
        this.item_name=name;
        this.item_price=price;
    }
    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_price() {
        return item_price;
    }

    public void setItem_price(String item_price) {
        this.item_price = item_price;
    }
}
