package site.nomoreparties.stellarburgers;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static site.nomoreparties.stellarburgers.User.getRandomUser;

public class LoginUserTest {
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

    // Логин нового пользователя
    @Test
    public void loginUser() {
        Response responseCreate = client.createUser(user);
        assertEquals(SC_OK, responseCreate.statusCode());
        User existingUser = new User(user.getEmail(), user.getPassword(), user.getName());
        Response responseLogin = client.loginUser(existingUser);
        assertEquals(SC_OK, responseLogin.statusCode());
        String responseSuccess = responseLogin.body().jsonPath().getString("success");
        MatcherAssert.assertThat(responseSuccess, true);

    }

    // Логин нового пользователя c неверным паролем
    @Test
    public void loginUserWithWrongPassword() {
        Response responseCreate = client.createUser(user);
        assertEquals(SC_OK, responseCreate.statusCode());
        User existingUser = new User(user.getEmail(), RandomStringUtils.randomAlphabetic(10),
                user.getName());
        Response responseLogin = client.loginUser(existingUser);
        assertEquals(SC_UNAUTHORIZED, responseLogin.statusCode());
        String responseMessage = responseLogin.body().jsonPath().getString("message");
        assertEquals(responseMessage, "email or password are incorrect");
    }
}
