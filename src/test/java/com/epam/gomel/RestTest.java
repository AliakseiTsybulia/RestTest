package com.epam.gomel;

import static java.time.Duration.ofMinutes;
import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.*;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.ReadContext;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;


import java.util.List;
import java.util.Map;


import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

/**
 * Created by AliakseiTsybulia on 03.01.2018.
 */

@Tag("RestTest")
class RestTest {
    private final String POSTS_URI = "http://jsonplaceholder.typicode.com/posts";
    private final String COMMENTS_URI = "http://jsonplaceholder.typicode.com/comments";
    private final String USERS_URI = "http://jsonplaceholder.typicode.com/users";

    @Disabled
    @Test
    @DisplayName("Disabled test")
    void disabledTest() {
        Response res = get("nonexistent URI");
        assertEquals(200, res.getStatusCode());
    }

    @Test
    @DisplayName("Status code test")
    void statusCodeTest() {
        Response res = get(POSTS_URI + "/1");
        assertEquals(200, res.getStatusCode());
    }

    @Test
    @DisplayName("Content type test")
    void contentTypeTest() {
        Response res = get(POSTS_URI + "/1");
        assertEquals("application/json; charset=utf-8", res.getContentType());
    }

    @Test
    @DisplayName("Timeout test")
    void timeoutTest() {
        assertTimeout(ofMinutes(1), () -> {
            get(POSTS_URI + "/1");
        });
    }

    @Test
    @DisplayName("Body assertion test")
    void bodyAssertionTest() {
        Response res = get(POSTS_URI + "/1");
        assertEquals("sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                JsonPath.read(res.asString(), "$.title"));
    }

    @Test
    @DisplayName("Body assertion test 2")
    void bodyAssertion2Test() {
        Response res = get(POSTS_URI + "?id=3");
        Integer id = JsonPath.read(res.asString(), "$.[0].id");
        assertTrue(id.equals(3));
    }

    @Test
    @DisplayName("Body assertion test 3")
    void bodyAssertion3Test() {
        Response res = get(POSTS_URI);
        int id = JsonPath.parse(res.asString()).read( "$.[2].id");
        assertEquals(3, id);
    }

    @Test
    @DisplayName("Body assertion test 4")
    void bodyAssertion4Test() {
        Response res = get(POSTS_URI);
        List<Integer> id = JsonPath.read(res.asString(), "$.[*].id");
        assertTrue(id.contains(3));
    }

    @Test
    @DisplayName("Body assertion test 5 with filtering inline predicates")
    void bodyAssertion5Test() {
        Response res = get(POSTS_URI);
        List<Map<String, Object>>  objects =  JsonPath.parse(res.asString())
                .read("$.[?(!(@.userId == 1 && @.id <= 11))]");
        assertTrue(objects.get(3).get("id").equals(14));
    }

    @Test
    @DisplayName("Body assertion test 6 with filtering predicates")
    void bodyAssertion6Test() {
        Response res = get(POSTS_URI);
        Filter filter = filter(
                where("userId").ne(1).and("id").gt(10)
        );
        List<Map<String, Object>>  objects =  JsonPath.parse(res.asString())
                .read("$.[?]", filter);
        assertTrue(objects.get(3).get("id").equals(14));
    }

    @Test
    @DisplayName("Body assertion test 7 with custom filtering predicates")
    void bodyAssertion7Test() {
        Response res = get(POSTS_URI);
        ReadContext context = JsonPath.parse(res.asString());
        Predicate customFilterPredicate = context1 -> context1.item(Map.class).containsKey("id");
        List<Map<String, Object>>  objects =  context.read("$.[?]", List.class, customFilterPredicate);
        assertTrue(objects.get(3).get("id").equals(4));
    }

    @Test
    @DisplayName("Body assertion test 8 with mixed filtering predicates")
    void bodyAssertion8Test() {
        Response res = get(POSTS_URI);
        ReadContext context = JsonPath.parse(res.asString());
        Predicate customFilterPredicate = context1 -> context1.item(Map.class).containsKey("id");
        Filter filter = filter(
                where("id").eq(14)
        );
        List<Integer> ids =  context.read("$.[?].[?].id", List.class, customFilterPredicate, filter);
        assertTrue(ids.get(0).equals(14));
    }

    @Test
    @DisplayName("Body assertion test 9 with mixed filtering predicates")
    void bodyAssertion9Test() {
        Response res = get(COMMENTS_URI);
        ReadContext context = JsonPath.parse(res.asString());
        Predicate customFilterPredicate = context1 -> context1.item(Map.class).containsKey("email");
        Filter filter = filter(
                where("name").eq("modi ut eos dolores illum nam dolor")
        );
        List<String> ids =  context.read("$.[?].[?].email", List.class, customFilterPredicate, filter);
        assertTrue(ids.get(0).equals("Oswald.Vandervort@leanne.org"));
    }

    @Test
    @DisplayName("Body assertion test 10 with mixed filtering predicates")
    void bodyAssertion10Test() {
        Response res = get(COMMENTS_URI);
        ReadContext context = JsonPath.parse(res.asString());
        Predicate customFilterPredicate = context1 -> context1.item(Map.class).containsKey("email");
        Filter filter1 = filter(
                where("name").eq("modi ut eos dolores illum nam dolor")
        );
        Filter filter2 = filter(
                where("postId").eq(3)
        );
        Filter filter3 = filter(
                where("id").eq(12)
        );
        List<String> ids =  context.read("$.[?].[?].[?].[?].email",
                List.class, customFilterPredicate, filter1, filter2, filter3);
        assertTrue(ids.get(0).equals("Oswald.Vandervort@leanne.org"));
    }

    @Test
    @DisplayName("Body assertion test 11 with mixed filtering predicates")
    void bodyAssertion11Test() {
        Response res = get(USERS_URI);
        ReadContext context = JsonPath.parse(res.asString());
        Predicate customFilterPredicate = context1 -> context1.item(Map.class).containsKey("id");
        Filter filter1 = filter(
                where("name").eq("Leanne Graham")
        );
        Filter filter2 = filter(
                where("city").eq("Gwenborough")
        );
        Filter filter3 = filter(
                where("lat").eq("-37.3159")
        );
        List<String> ids =  context.read("$.[?].[?].address[?].geo[?].lng",
                List.class, customFilterPredicate, filter1, filter2, filter3);
        assertTrue(ids.get(0).equals("81.1496"));
    }

    @Test
    @DisplayName("Body assertion test 12 with filtering function")
    void bodyAssertion12Test() {
        Response res = get(USERS_URI);
        ReadContext context = JsonPath.parse(res.asString());
        List<Integer> ids = context.read("$.[?(@.name == \"Leanne Graham\")]." +
                "address[?(@.city == \"Gwenborough\")].geo[?(@.lat == \"-37.3159\")].length()");
        assertTrue(ids.get(0).equals(2));
    }

    @Test
    @DisplayName("JSON schema test")
    void jsonSchemaTest() {
        given().
            get("http://jsonplaceholder.typicode.com/users/1").
        then().
            assertThat().body(matchesJsonSchemaInClasspath("schema.json"));
    }

    @Test
    @DisplayName("Posts with users connection test")
    void connection1Test() {
        List<Integer> postUserIds = JsonPath.parse(get(POSTS_URI).asString()).read("$.[*].userId");
        List<Integer> userIds = JsonPath.parse(get(USERS_URI).asString()).read("$.[*].id");

        assertTrue(userIds.containsAll(postUserIds));
    }

    @Test
    @DisplayName("Posts with comments connection test")
    void connection2Test() {
        List<Integer> postIds = JsonPath.parse(get(POSTS_URI).asString()).read("$.[*].id");
        List<Integer> commentsPostIds = JsonPath.parse(get(USERS_URI).asString()).
                read("$.[*].postId");

        assertTrue(postIds.containsAll(commentsPostIds));
    }
}
