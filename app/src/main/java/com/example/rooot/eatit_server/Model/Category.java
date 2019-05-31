package com.example.rooot.eatit_server.Model;

public class Category {

    private String Hint;
    private String Link;
    private String Name;
    private String Price;

    public Category(String hint, String link, String name, String price) {
        Hint = hint;
        Link = link;
        Name = name;
        Price = price;
    }

    public Category() {
    }

    public String getHint() {
        return Hint;
    }

    public void setHint(String hint) {
        Hint = hint;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
