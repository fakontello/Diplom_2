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

public class ChangeUserDataTest {

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

    // Изменение имени пользователя и имейла с авторизацией
    @Test
    public void changeUserNameAndEmailAuth() {
        Response responseCreate = client.createUser(user);
        assertEquals(SC_OK, responseCreate.statusCode());
        String accessToken = responseCreate.body().jsonPath().getString("accessToken");

        User userNameAndPass = new User(user.setEmail(RandomStringUtils.randomAlphabetic
                (10) + "@yandex.ru"), user.getPassword(), user.setName(RandomStringUtils.randomAlphabetic
                (10)));
        Response patchUpdateMessage = client.updateUserInfo(accessToken, userNameAndPass);
        assertEquals(SC_OK, patchUpdateMessage.statusCode());
        String responseMessage = patchUpdateMessage.body().jsonPath().getString("success");
        MatcherAssert.assertThat(responseMessage, true);
    }

}
