package site.nomoreparties.stellarburgers;

import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.List;

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

}
