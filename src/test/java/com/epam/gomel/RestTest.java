package com.epam.gomel;

import static java.time.Duration.ofMinutes;
import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

/**
 * Created by AliakseiTsybulia on 03.01.2018.
 */

@Tag("RestTest")
class RestTest {

    @Disabled
    @Test
    @DisplayName("disabledTest")
    void disabledTest() {
        Response res = get("nonexistent URI");
        assertEquals(200, res.getStatusCode());
    }

    @Test
    @DisplayName("statusCodeTest")
    void statusCodeTest() {
        Response res = get("https://jsonplaceholder.typicode.com/posts/1");
        assertEquals(200, res.getStatusCode());
    }

    @Test
    @DisplayName("contentTypeTest")
    void contentTypeTest() {
        Response res = get("https://jsonplaceholder.typicode.com/posts/1");
        assertEquals("application/json; charset=utf-8", res.getContentType());
    }

    @Test
    @DisplayName("timeoutTest")
    void timeoutTest() {
        assertTimeout(ofMinutes(1), () -> {
            get("https://jsonplaceholder.typicode.com/posts/1");
        });
    }

    @Test
    @DisplayName("bodyAssertionTest")
    void bodyAssertionTest() {
        Response res = get("https://jsonplaceholder.typicode.com/posts/1");
        String json = res.asString();
        JsonPath jp = new JsonPath(json);
        assertEquals("sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                jp.get("title"));
    }

    @Test
    @DisplayName("bodyAssertionHamcrestTest")
    void bodyAssertionHamcrestTest() {
        Response res = get("https://jsonplaceholder.typicode.com/posts/1");
        String json = res.asString();
        JsonPath jp = new JsonPath(json);
        assertThat(jp.get("userId"), equalTo(1));
        assertThat(jp.get("id"), equalTo(1));
    }

    @Test
    @DisplayName("bodyArrayHasItemsTest")
    void bodyArrayHasItemsTest() {
        Response res = get("https://jsonplaceholder.typicode.com/posts");
        String json = res.asString();
        JsonPath jp = new JsonPath(json);
        assertThat(jp.get("id"), hasItems(1, 2, 100));
    }

    @Test
    @DisplayName("ParameterTest")
    void ParameterTest() {
        Response res = given().
                            param("id", 1, 2, 100).
                            when().
                            get("https://jsonplaceholder.typicode.com/posts");
        String json = res.asString();
        JsonPath jp = new JsonPath(json);
        assertThat(jp.get("id"), hasItems(1, 2, 100));
    }

    @Test
    @DisplayName("Parameter2Test")
    void Parameter2Test() {
        Response res = given().
            param("id", 94).
            param("title", "qui qui voluptates illo iste minima").
        when().
            get("https://jsonplaceholder.typicode.com/posts");
        String json = res.asString();
        JsonPath jp = new JsonPath(json);
        assertThat(jp.get("userId"), hasItems(10));
    }
}
