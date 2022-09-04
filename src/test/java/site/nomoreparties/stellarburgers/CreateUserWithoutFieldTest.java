package site.nomoreparties.stellarburgers;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class CreateUserWithoutFieldTest {

    BurgersApiUserClient client;
    User user;

    @Before
    public void setUp() {
        client = new BurgersApiUserClient();
    }

    // Создание пользователя с одним не заполненным обязательным полем
    @Test
    public void attemptToCreateUserWithoutImportantFields() {

        User newUser = new User(RandomStringUtils.randomAlphabetic(5) + "@" +
                RandomStringUtils.randomAlphabetic(5) + ".ru", RandomStringUtils.randomAlphabetic(10),
                "");

        Response responseCreate = client.createUser(newUser);
        assertEquals(SC_FORBIDDEN, responseCreate.statusCode());

        String responseWrongMessage = responseCreate.body().jsonPath().getString("message");
        assertEquals(responseWrongMessage, "Email, password and name are required fields");

        // Добавил проверку что если вдруг обязательное поле заполнится то созданный юзер удалится
        if (responseCreate.statusCode() == SC_OK) {
            User newSuccessUser = new User(newUser.getEmail(), newUser.getPassword(),
                    newUser.getName());
            Response responseLogin = client.loginUser(newSuccessUser);
            String accessToken = responseLogin.body().jsonPath().getString("accessToken");
            assertEquals(SC_OK, responseLogin.statusCode());
            Response responseDeleteUser = client.deleteUser(accessToken);
            assertEquals(SC_ACCEPTED, responseDeleteUser.statusCode());
            String responseMessage = responseDeleteUser.body().jsonPath().getString("message");
            assertEquals(responseMessage, "User successfully removed");
        }
    }
}
