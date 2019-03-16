package com.example.bhaumik.spendsmart.helper;

public class ExpenseDataModel {

    String itemName;
    double itemPrice;
    String[] friendIds;
    String userId;

    public ExpenseDataModel(String itemName, double itemPrice, String[] friendIds, String userId) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.friendIds = friendIds;
        this.userId = userId;
    }

    public ExpenseDataModel(String itemName, double itemPrice) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public String[] getFriendIds() {
        return friendIds;
    }

    public  String getUserId() {
        return userId;
    }
}
