package org.example;

import domain.Exception.CheckException;
import ui.UiRestaurant;

public class Main {
    public static void main(String[] args) throws CheckException{
        var Open = new UiRestaurant();

        Open.start();
    }
}