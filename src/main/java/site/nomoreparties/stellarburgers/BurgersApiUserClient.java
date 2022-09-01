package site.nomoreparties.stellarburgers;

import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

public class BurgersApiUserClient {
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/";

    private static final Filter requestFilter = new RequestLoggingFilter();
    private static final Filter responseFilter = new ResponseLoggingFilter();

    RequestSpecification requestSpecification =
            RestAssured.given()
                    .filters(List.of(requestFilter, responseFilter))
                    .baseUri(BASE_URL)
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON);

    public Response createUser(User newUser) {
        return RestAssured.with()
                .spec(requestSpecification)
                .body(newUser)
                .when()
                .post("/auth/register");
    }

    public Response loginUser(User existingUser) {
        return RestAssured.with()
                .spec(requestSpecification)
                .body(existingUser)
                .when()
                .post("/auth/login");
    }

    public Response deleteUser(String accessToken) {
        return RestAssured.with()
                .spec(requestSpecification)
                .headers("Authorization", accessToken)
                .when()
                .delete("/auth/user");
    }

    public Response updateUserInfo(String accessToken, User existingUser) {
        return RestAssured.with()
                .spec(requestSpecification)
                .headers("Authorization", accessToken)
                .body(existingUser)
                .when()
                .patch("/auth/user");
    }

}
