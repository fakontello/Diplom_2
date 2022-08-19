package site.nomoreparties.stellarburgers;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static site.nomoreparties.stellarburgers.NewUser.getRandomUser;

public class ChangeUserDataUnauthTest {

    BurgersApiClient client;
    NewUser newUser;

    @Before
    public void setUp() {
        client = new BurgersApiClient();
        newUser = getRandomUser();
    }

    // Изменение имени пользователя и имейла без авторизации
    @Test
    public void changeUserDataWithoutAuth() {
        Response responseCreate = client.createUser(newUser);
        assertEquals(SC_OK, responseCreate.statusCode());
        ExistingUser existingUser = new ExistingUser(newUser.getEmail(), newUser.getPassword(), newUser.getName());
        ExistingUser newUserNameAndPass = new ExistingUser(newUser.setEmail(RandomStringUtils.randomAlphabetic(10) +
                "@yandex.ru"), newUser.getPassword(), newUser.setName(RandomStringUtils.randomAlphabetic(10)));
        Response responeUpdateUserData = client.updateUserInfo(RandomStringUtils.randomAlphabetic(10),
                newUserNameAndPass);
        assertEquals(SC_UNAUTHORIZED, responeUpdateUserData.statusCode());
        String responseMessage = responeUpdateUserData.body().jsonPath().getString("message");
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
