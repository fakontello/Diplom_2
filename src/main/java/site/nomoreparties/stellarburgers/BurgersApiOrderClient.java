package site.nomoreparties.stellarburgers;

import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

public class BurgersApiOrderClient {

    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/";

    private static final Filter requestFilter = new RequestLoggingFilter();
    private static final Filter responseFilter = new ResponseLoggingFilter();

    RequestSpecification requestSpecification =
            RestAssured.given()
                    .filters(List.of(requestFilter, responseFilter))
                    .baseUri(BASE_URL)
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON);

    public Response getIngredients() {
        return RestAssured.with()
                .spec(requestSpecification)
                .when()
                .get("/ingredients");
    }

    public Response newOrder(Order newOrder) {
        return RestAssured.with()
                .spec(requestSpecification)
                .body(newOrder)
                .when()
                .post("/orders");
    }

    public Response newAuthOrder(Order newOrder, String accessToken) {
        return RestAssured.with()
                .spec(requestSpecification)
                .headers("Authorization", accessToken)
                .body(newOrder)
                .when()
                .post("/orders");
    }

    public Response getUserOrders(String accessToken) {
        return RestAssured.with()
                .spec(requestSpecification)
                .headers("Authorization", accessToken)
                .when()
                .get("/orders");
    }
}
