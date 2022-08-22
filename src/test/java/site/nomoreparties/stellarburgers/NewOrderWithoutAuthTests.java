package site.nomoreparties.stellarburgers;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class NewOrderWithoutAuthTests {

    BurgersApiClient client;

    @Before
    public void setUp() {
        client = new BurgersApiClient();

    }

    // Создание заказа с ингредиентами, без авторизации
    @Test
    public void newOrderTest() {
        Response getOrders = client.getIngredients();
        assertEquals(SC_OK, getOrders.statusCode());
        String getFirstOrderById = getOrders.body().jsonPath().getString("data[0]._id");
        String getSecondOrderById = getOrders.body().jsonPath().getString("data[1]._id");
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(getFirstOrderById);
        ingredients.add(getSecondOrderById);

        NewOrder newOrder = new NewOrder(ingredients);
        Response newOrderStatus = client.newOrder(newOrder);
        assertEquals(SC_OK, newOrderStatus.statusCode());
    }

    // Создание заказа без ингредиентов, без авторизации
    @Test
    public void newOrderWithoutIngredients() {
        ArrayList<String> ingredients = new ArrayList<>();
        NewOrder newOrder = new NewOrder(ingredients);
        Response newOrderStatus = client.newOrder(newOrder);
        assertEquals(SC_BAD_REQUEST, newOrderStatus.statusCode());
        String noIngredientsMessage = newOrderStatus.body().jsonPath().getString("message");
        assertEquals(noIngredientsMessage, "Ingredient ids must be provided");
    }

    // Создание заказа с неверным хэшем ингредиентов, без авторизации
    @Test
    public void wrongHashIngredients() {
        Response getOrders = client.getIngredients();
        assertEquals(SC_OK, getOrders.statusCode());
        String getFirstOrderById = getOrders.body().jsonPath().getString("data[0]._id");
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(getFirstOrderById);
        ingredients.add(RandomStringUtils.randomAlphabetic(10));

        NewOrder newOrder = new NewOrder(ingredients);
        Response newOrderStatus = client.newOrder(newOrder);
        assertEquals(SC_INTERNAL_SERVER_ERROR, newOrderStatus.statusCode());
    }
}
