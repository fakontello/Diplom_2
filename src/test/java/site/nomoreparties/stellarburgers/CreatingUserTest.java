package site.nomoreparties.stellarburgers;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
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
    }

    // Создание нового пользователя
    @Test
    public void creatingNewCourier() {
        newUser = getRandomUser();
        Response responseCreate = client.createUser(newUser);
        assertEquals(SC_OK, responseCreate.statusCode());
        String responseSuccess = responseCreate.body().jsonPath().getString("success");
        MatcherAssert.assertThat(responseSuccess, true);
    }

    // Создание пользователя, который уже зарегистрирован
    @Test
    public void createExistingUser() {
        newUser = getRandomUser();
        Response responseCreate = client.createUser(newUser);
        assertEquals(SC_OK, responseCreate.statusCode());

        Response anotherResponseCreate = client.createUser(newUser);
        assertEquals(SC_FORBIDDEN, anotherResponseCreate.statusCode());

        String responseMessage = anotherResponseCreate.body().jsonPath().getString("message");
        assertEquals(responseMessage, "User already exists");
    }

    // Создание пользователя с одним не заполненным обязательным полем
    @Test
    public void attemptToCreateCourierWithoutImportantFields() {

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
