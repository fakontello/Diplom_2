package site.nomoreparties.stellarburgers;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static site.nomoreparties.stellarburgers.NewUser.getRandomUser;

public class NewOrderWithAuthTests {

    BurgersApiClient client;
    NewUser newUser;

    @Before
    public void setUp() {
        client = new BurgersApiClient();
        newUser = getRandomUser();

        Response responseCreate = client.createUser(newUser);
        assertEquals(SC_OK, responseCreate.statusCode());
        String responseSuccess = responseCreate.body().jsonPath().getString("success");
        assertThat(responseSuccess, true);
    }

    @After
    public void deleteUser() {
        ExistingUser existingUser = new ExistingUser(newUser.getEmail(), newUser.getPassword(), newUser.getName());
        Response responseLogin = client.loginUser(existingUser);
        String accessToken = responseLogin.body().jsonPath().getString("accessToken");
        assertEquals(SC_OK, responseLogin.statusCode());
        Response responseDeleteUser = client.deleteUser(accessToken);
        assertEquals(SC_ACCEPTED, responseDeleteUser.statusCode());
        String responseMessage = responseDeleteUser.body().jsonPath().getString("message");
        assertEquals(responseMessage, "User successfully removed");
    }

    // Создание заказа с ингредиентами, с авторизацией
    @Test
    public void newOrderTest() {
        ExistingUser existingUser = new ExistingUser(newUser.getEmail(), newUser.getPassword(), newUser.getName());
        Response responseLogin = client.loginUser(existingUser);
        String accessToken = responseLogin.body().jsonPath().getString("accessToken");

        Response getOrders = client.getIngredients();
        assertEquals(SC_OK, getOrders.statusCode());
        String getFirstOrderById = getOrders.body().jsonPath().getString("data[0]._id");
        String getSecondOrderById = getOrders.body().jsonPath().getString("data[1]._id");
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(getFirstOrderById);
        ingredients.add(getSecondOrderById);

        NewOrder newOrder = new NewOrder(ingredients);
        Response newOrderStatus = client.newAuthOrder(newOrder, accessToken);
        assertEquals(SC_OK, newOrderStatus.statusCode());

        Response clientOrder = client.getUserOrders(accessToken);
        String clientOrderMessage = clientOrder.body().jsonPath().getString("orders");
        assertNotNull(clientOrderMessage);
    }

    // Создание заказа без ингредиентов, с авторизацией
    @Test
    public void newOrderWithoutIngredients() {
        ExistingUser existingUser = new ExistingUser(newUser.getEmail(), newUser.getPassword(), newUser.getName());
        Response responseLogin = client.loginUser(existingUser);
        String accessToken = responseLogin.body().jsonPath().getString("accessToken");

        ArrayList<String> ingredients = new ArrayList<>();
        NewOrder newOrder = new NewOrder(ingredients);
        Response newOrderStatus = client.newAuthOrder(newOrder, accessToken);
        assertEquals(SC_BAD_REQUEST, newOrderStatus.statusCode());
        String noIngredientsMessage = newOrderStatus.body().jsonPath().getString("message");
        assertEquals(noIngredientsMessage, "Ingredient ids must be provided");

        Response clientOrder = client.getUserOrders(accessToken);
        String clientOrderMessage = clientOrder.body().jsonPath().getString("orders");
        assertFalse(clientOrderMessage.isEmpty());
    }

    // Создание заказа с неверным хэшем ингредиентов, с авторизацией
    @Test
    public void wrongHashIngredients() {
        ExistingUser existingUser = new ExistingUser(newUser.getEmail(), newUser.getPassword(), newUser.getName());
        Response responseLogin = client.loginUser(existingUser);
        String accessToken = responseLogin.body().jsonPath().getString("accessToken");

        Response getOrders = client.getIngredients();
        assertEquals(SC_OK, getOrders.statusCode());
        String getFirstOrderById = getOrders.body().jsonPath().getString("data[0]._id");
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(getFirstOrderById);
        ingredients.add(RandomStringUtils.randomAlphabetic(10));

        NewOrder newOrder = new NewOrder(ingredients);
        Response newOrderStatus = client.newAuthOrder(newOrder, accessToken);
        assertEquals(SC_INTERNAL_SERVER_ERROR, newOrderStatus.statusCode());
    }

    // Создание и получение заказа пользователя без авторизации
    @Test
    public void getUserOrderWithoutAuth() {
        ExistingUser existingUser = new ExistingUser(newUser.getEmail(), newUser.getPassword(), newUser.getName());
        Response responseLogin = client.loginUser(existingUser);
        String accessToken = responseLogin.body().jsonPath().getString("accessToken");

        Response getOrders = client.getIngredients();
        assertEquals(SC_OK, getOrders.statusCode());
        String getFirstOrderById = getOrders.body().jsonPath().getString("data[0]._id");
        String getSecondOrderById = getOrders.body().jsonPath().getString("data[1]._id");
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(getFirstOrderById);
        ingredients.add(getSecondOrderById);

        NewOrder newOrder = new NewOrder(ingredients);
        Response newOrderStatus = client.newAuthOrder(newOrder, accessToken);
        assertEquals(SC_OK, newOrderStatus.statusCode());

        Response clientOrder = client.getUserOrders(RandomStringUtils.randomAlphabetic(10));
        assertEquals(SC_UNAUTHORIZED, clientOrder.statusCode());
        String clientOrderMessage = clientOrder.body().jsonPath().getString("message");
        assertEquals(clientOrderMessage, "You should be authorised");

    }

}
