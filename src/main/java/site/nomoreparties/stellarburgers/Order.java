package site.nomoreparties.stellarburgers;

import java.util.ArrayList;

public class Order {

    private ArrayList<String> ingredients;

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public ArrayList<String> setIngredients(ArrayList<String> ingredients) {
        return this.ingredients = ingredients;
    }

    public Order(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

}
