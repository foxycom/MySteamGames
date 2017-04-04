package com.joffreylagut.mysteamgames.mysteamgames.models;

/**
 * GameBundle.java
 * Purpose: Blueprint for a GameBundle object.
 *
 * @author Joffrey LAGUT
 * @version 1.5 2017-04-10
 */

public class GameBundle {

    private int id;
    private String name;
    private double price;

    public GameBundle() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
