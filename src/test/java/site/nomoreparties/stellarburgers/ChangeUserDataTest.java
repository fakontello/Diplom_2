package site.nomoreparties.stellarburgers;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static site.nomoreparties.stellarburgers.NewUser.getRandomUser;

public class ChangeUserDataTest {

    BurgersApiClient client;
    NewUser newUser;

    @Before
    public void setUp() {
        client = new BurgersApiClient();
        newUser = getRandomUser();
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

    // Изменение имени пользователя и имейла с авторизацией
    @Test
    public void changeUserNameAndEmailAuth() {
        Response responseCreate = client.createUser(newUser);
        assertEquals(SC_OK, responseCreate.statusCode());
        String accessToken = responseCreate.body().jsonPath().getString("accessToken");

        ExistingUser newUserNameAndPass = new ExistingUser(newUser.setEmail(RandomStringUtils.randomAlphabetic
                (10) + "@yandex.ru"), newUser.getPassword(), newUser.setName(RandomStringUtils.randomAlphabetic
                (10)));
        client.updateUserInfo(accessToken, newUserNameAndPass);
    }

}
