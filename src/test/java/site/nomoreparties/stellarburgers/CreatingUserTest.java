package site.nomoreparties.stellarburgers;

import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static site.nomoreparties.stellarburgers.NewUser.getRandomUser;

public class CreatingUserTest {

    BurgersApiClient client;
    NewUser newUser;

    @Before
    public void setUp() {
        client = new BurgersApiClient();
        newUser = getRandomUser();
    }

    @After
    public void deleteUser() {
        ExistingUser existingUser = new ExistingUser(newUser.getEmail(), newUser.getPassword());
        Response responseLogin = client.loginUser(existingUser);
        String accessToken = responseLogin.body().jsonPath().getString("accessToken");
        assertEquals(SC_OK, responseLogin.statusCode());
        client.getUserInfo(accessToken);
        client.deleteUser(accessToken);
    }

    // Создание нового пользователя
    @Test
    public void creatingNewUser() {
        Response responseCreate = client.createUser(newUser);
        assertEquals(SC_OK, responseCreate.statusCode());
        String responseSuccess = responseCreate.body().jsonPath().getString("success");
        MatcherAssert.assertThat(responseSuccess, true);
    }

    // Создание пользователя, который уже зарегистрирован
    @Test
    public void createExistingUser() {
        Response responseCreate = client.createUser(newUser);
        assertEquals(SC_OK, responseCreate.statusCode());

        Response anotherResponseCreate = client.createUser(newUser);
        assertEquals(SC_FORBIDDEN, anotherResponseCreate.statusCode());

        String responseMessage = anotherResponseCreate.body().jsonPath().getString("message");
        assertEquals(responseMessage, "User already exists");
    }

}
