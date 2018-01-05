package com.epam.gomel;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import org.junit.gen5.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

/**
 * Created by AliakseiTsybulia on 03.01.2018.
 */

@Tag("RestTest")
class RestTest {

    @Test
    @DisplayName("statusCodeTest")
    void statusCodeTest() {
        given().
            get("https://jsonplaceholder.typicode.com/posts/1").
        then().
            statusCode(200).
            log().all();
    }

    @Test
    @DisplayName("bodyEqualTest")
    void bodyEqualTest() {
        given().
                get("https://jsonplaceholder.typicode.com/posts/1").
                then().
                body("title",equalTo(
                        "sunt aut facere repellat provident occaecati excepturi optio reprehenderit"));
    }

    @Test
    @DisplayName("bodyHasItemsTest")
    void bodyHasItemsTest() {
        given().
                get("https://jsonplaceholder.typicode.com/posts").
                then().
                body("id",hasItems(1, 2, 3));
    }
}
