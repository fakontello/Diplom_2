package site.nomoreparties.stellarburgers;

import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static site.nomoreparties.stellarburgers.User.getRandomUser;

public class CreatingUserTest {

    BurgersApiUserClient client;
    User user;

    @Before
    public void setUp() {
        client = new BurgersApiUserClient();
        user = getRandomUser();
    }

    @After
    public void deleteUser() {
        User existingUser = new User(user.getEmail(), user.getPassword(), user.getName());
        Response responseLogin = client.loginUser(existingUser);
        String accessToken = responseLogin.body().jsonPath().getString("accessToken");
        assertEquals(SC_OK, responseLogin.statusCode());
        Response responseDeleteUser = client.deleteUser(accessToken);
        assertEquals(SC_ACCEPTED, responseDeleteUser.statusCode());
        String responseMessage = responseDeleteUser.body().jsonPath().getString("message");
        assertEquals(responseMessage, "User successfully removed");
    }

    // Создание нового пользователя
    @Test
    public void creatingUser() {
        Response responseCreate = client.createUser(user);
        assertEquals(SC_OK, responseCreate.statusCode());
        String responseSuccess = responseCreate.body().jsonPath().getString("success");
        MatcherAssert.assertThat(responseSuccess, true);
    }

    // Создание пользователя, который уже зарегистрирован
    @Test
    public void createUser() {
        Response responseCreate = client.createUser(user);
        assertEquals(SC_OK, responseCreate.statusCode());

        Response anotherResponseCreate = client.createUser(user);
        assertEquals(SC_FORBIDDEN, anotherResponseCreate.statusCode());

        String responseMessage = anotherResponseCreate.body().jsonPath().getString("message");
        assertEquals(responseMessage, "User already exists");
    }

}
