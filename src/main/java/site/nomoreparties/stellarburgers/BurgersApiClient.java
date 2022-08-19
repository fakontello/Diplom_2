package site.nomoreparties.stellarburgers;

import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.List;

import static io.restassured.RestAssured.given;

public class BurgersApiClient {
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/";

    private static final Filter requestFilter = new RequestLoggingFilter();
    private static final Filter responseFilter = new ResponseLoggingFilter();

    public Response createUser(NewUser newUser) {
        return RestAssured.with()
                .filters(List.of(requestFilter, responseFilter))
                .baseUri(BASE_URL)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/auth/register");
    }

    public Response loginUser(ExistingUser existingUser) {
        return RestAssured.with()
                .filters(List.of(requestFilter, responseFilter))
                .baseUri(BASE_URL)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(existingUser)
                .when()
                .post("/auth/login");
    }

    public Response deleteUser(String accessToken) {
        return RestAssured.with()
                .filters(List.of(requestFilter, responseFilter))
                .baseUri(BASE_URL)
                .headers(
                        "Authorization",
                        accessToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .when()
                .delete("/auth/user");
    }

    public void getUserInfo(String accessToken) {
        given()
                .filters(List.of(requestFilter, responseFilter))
                .baseUri(BASE_URL)
                .headers(
                        "Authorization",
                        accessToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .when()
                .get("/auth/user");
    }

    public Response updateUserInfo(String accessToken, ExistingUser existingUser) {
        return RestAssured.with()
                .filters(List.of(requestFilter, responseFilter))
                .baseUri(BASE_URL)
                .headers(
                        "Authorization",
                        accessToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(existingUser)
                .when()
                .patch("/auth/user");
    }

}
