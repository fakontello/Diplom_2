package site.nomoreparties.stellarburgers;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static site.nomoreparties.stellarburgers.NewUser.getRandomUser;

public class LoginUserTest {
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

    // Логин нового пользователя
    @Test
    public void LoginNewUser() {
        Response responseCreate = client.createUser(newUser);
        assertEquals(SC_OK, responseCreate.statusCode());
        ExistingUser existingUser = new ExistingUser(newUser.getEmail(), newUser.getPassword());
        Response responseLogin = client.loginUser(existingUser);
        assertEquals(SC_OK, responseLogin.statusCode());
        String responseSuccess = responseCreate.body().jsonPath().getString("success");
        MatcherAssert.assertThat(responseSuccess, true);

    }

    // Логин нового пользователя c неверным паролем
    @Test
    public void LoginNewUserWithWrongPassword() {
        Response responseCreate = client.createUser(newUser);
        assertEquals(SC_OK, responseCreate.statusCode());
        ExistingUser existingUser = new ExistingUser(newUser.getEmail(), RandomStringUtils.randomAlphabetic(10));
        Response responseLogin = client.loginUser(existingUser);
        assertEquals(SC_UNAUTHORIZED, responseLogin.statusCode());
        String responseMessage = responseLogin.body().jsonPath().getString("message");
        assertEquals(responseMessage, "email or password are incorrect");
    }
}
