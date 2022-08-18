package site.nomoreparties.stellarburgers;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.junit.Assert.assertEquals;

public class CreateUserWithoutFieldTest {

    BurgersApiClient client;

    @Before
    public void setUp() {
        client = new BurgersApiClient();
    }

    // Создание пользователя с одним не заполненным обязательным полем
    @Test
    public void attemptToCreateUserWithoutImportantFields() {

        final NewUser newUser = new NewUser(RandomStringUtils.randomAlphabetic(5) + "@" +
                RandomStringUtils.randomAlphabetic(5) + ".ru",
                null,
                RandomStringUtils.randomAlphabetic(10));

        Response responseCreate = client.createUser(newUser);
        assertEquals(SC_FORBIDDEN, responseCreate.statusCode());

        String responseMessage = responseCreate.body().jsonPath().getString("message");
        assertEquals(responseMessage, "Email, password and name are required fields");
    }
}
