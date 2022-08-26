package site.nomoreparties.stellarburgers;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static site.nomoreparties.stellarburgers.User.getRandomUser;

public class ChangeUserDataNoAuthTest {

    BurgersApiUserClient client;
    User user;

    @Before
    public void setUp() {
        client = new BurgersApiUserClient();
        user = getRandomUser();
    }

    // Изменение имени пользователя и имейла без авторизации
    @Test
    public void changeUserDataWithoutAuth() {
        Response responseCreate = client.createUser(user);
        assertEquals(SC_OK, responseCreate.statusCode());
        User existingUser = new User(user.getEmail(), user.getPassword(), user.getName());
        User userNameAndPass = new User(user.setEmail(RandomStringUtils.randomAlphabetic(10) +
                "@yandex.ru"), user.getPassword(), user.setName(RandomStringUtils.randomAlphabetic(10)));
        Response responseUpdateUserData = client.updateUserInfo(RandomStringUtils.randomAlphabetic(10),
                userNameAndPass);
        assertEquals(SC_UNAUTHORIZED, responseUpdateUserData.statusCode());
        String responseMessage = responseUpdateUserData.body().jsonPath().getString("message");
        assertEquals(responseMessage, "You should be authorised");

        Response responseLogin = client.loginUser(existingUser);
        String accessToken = responseLogin.body().jsonPath().getString("accessToken");
        assertEquals(SC_OK, responseLogin.statusCode());
        Response responseDeleteUser = client.deleteUser(accessToken);
        assertEquals(SC_ACCEPTED, responseDeleteUser.statusCode());
        String newResponseMessage = responseDeleteUser.body().jsonPath().getString("message");
        assertEquals(newResponseMessage, "User successfully removed");
    }
}
