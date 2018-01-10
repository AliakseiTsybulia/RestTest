package com.epam.gomel;

import static java.time.Duration.ofMinutes;
import static io.restassured.RestAssured.*;
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
        Response res = get("https://jsonplaceholder.typicode.com/posts/1");
        assertEquals(200, res.getStatusCode());
    }

    @Test
    @DisplayName("Content type test")
    void contentTypeTest() {
        Response res = get("https://jsonplaceholder.typicode.com/posts/1");
        assertEquals("application/json; charset=utf-8", res.getContentType());
    }

    @Test
    @DisplayName("Timeout test")
    void timeoutTest() {
        assertTimeout(ofMinutes(1), () -> {
            get("https://jsonplaceholder.typicode.com/posts/1");
        });
    }

    @Test
    @DisplayName("Body assertion test")
    void bodyAssertionTest() {
        Response res = get("https://jsonplaceholder.typicode.com/posts/1");
        String json = res.asString();
        assertEquals("sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                JsonPath.read(json, "$.title"));
    }

    @Test
    @DisplayName("Body assertion test 2")
    void bodyAssertion2Test() {
        Response res = get("https://jsonplaceholder.typicode.com/posts");
        String json = res.asString();
        List<String> id = JsonPath.read(json, "$.[?(@.id == 3)].id");
        assertTrue(id.contains(3));
    }

    @Test
    @DisplayName("Body assertion test 3")
    void bodyAssertion3Test() {
        Response res = get("https://jsonplaceholder.typicode.com/posts");
        String json = res.asString();
        int id = JsonPath.parse(json).read( "$.[2].id");
        assertEquals(3, id);
    }

    @Test
    @DisplayName("Body assertion test 4")
    void bodyAssertion4Test() {
        Response res = get("https://jsonplaceholder.typicode.com/posts");
        String json = res.asString();
        List<Integer> id = JsonPath.read(json, "$.[*].id");
        assertTrue(id.contains(3));
    }

    @Test
    @DisplayName("Parameter test")
    void ParameterTest() {
        Response res = given().
                param("id", 1, 2, 100).
                when().
                get("https://jsonplaceholder.typicode.com/posts");
        String json = res.asString();
        List<Integer> id = JsonPath.read(json, "$.[*].id");
        assertTrue(id.contains(2));
    }

    @Test
    @DisplayName("Parameter test 2")
    void Parameter2Test() {
        Response res = given().
                param("id", 94).
                param("title", "qui qui voluptates illo iste minima").
                when().
                get("https://jsonplaceholder.typicode.com/posts");
        String json = res.asString();
        List<Integer> userId = JsonPath.read(json, "$.[*].userId");
        assertTrue(userId.contains(10));

    }

    @Test
    @DisplayName("Filter inline predicates test")
    void filterInlinePredicatesTest() {
        Response res = get("https://jsonplaceholder.typicode.com/posts");
        String json = res.asString();
        List<Map<String, Object>>  objects =  JsonPath.parse(json)
                .read("$.[?(!(@.userId == 1 && @.id <= 11))]");
        assertTrue(objects.get(3).get("id").equals(14));
    }

    @Test
    @DisplayName("Filter predicates test")
    void filterPredicatesTest() {
        Response res = get("https://jsonplaceholder.typicode.com/posts");
        String json = res.asString();
        Filter filter = filter(
                where("userId").ne(1).and("id").gt(10)
        );
        List<Map<String, Object>>  objects =  JsonPath.parse(json)
                .read("$.[?]", filter);
        assertTrue(objects.get(3).get("id").equals(14));
    }

    @Test
    @DisplayName("Custom predicates test")
    void customPredicatesTest() {
        Response res = get("https://jsonplaceholder.typicode.com/posts");
        String json = res.asString();
        ReadContext context = JsonPath.parse(json);
        Predicate customFilterPredicate = context1 -> context1.item(Map.class).containsKey("id");
        List<Map<String, Object>>  objects =  context.read("$.[?]", List.class, customFilterPredicate);
        assertTrue(objects.get(3).get("id").equals(4));
    }

    @Test
    @DisplayName("Mixed filter predicates test")
    void mixedFilterPredicates1Test() {
        Response res = get("https://jsonplaceholder.typicode.com/posts");
        String json = res.asString();
        ReadContext context = JsonPath.parse(json);
        Predicate customFilterPredicate = context1 -> context1.item(Map.class).containsKey("id");
        Filter filter = filter(
                where("id").eq(14)
        );
        List<Integer> ids =  context.read("$.[?].[?].id", List.class, customFilterPredicate, filter);
        assertTrue(ids.get(0).equals(14));
    }

    @Test
    @DisplayName("Mixed filter predicates test 2 with /comments resource")
    void mixedFilterPredicates2Test() {
        Response res = get("https://jsonplaceholder.typicode.com/comments");
        String json = res.asString();
        ReadContext context = JsonPath.parse(json);
        Predicate customFilterPredicate = context1 -> context1.item(Map.class).containsKey("email");
        Filter filter = filter(
                where("name").eq("modi ut eos dolores illum nam dolor")
        );
        List<String> ids =  context.read("$.[?].[?].email", List.class, customFilterPredicate, filter);
        assertTrue(ids.get(0).equals("Oswald.Vandervort@leanne.org"));
    }

    @Test
    @DisplayName("Mixed filter predicates test 3 with /comments resource")
    void mixedFilterPredicates3Test() {
        Response res = get("https://jsonplaceholder.typicode.com/comments");
        String json = res.asString();
        ReadContext context = JsonPath.parse(json);
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
    @DisplayName("Mixed filter predicates test 4 with /users resource")
    void mixedFilterPredicates4Test() {
        Response res = get("https://jsonplaceholder.typicode.com/users");
        String json = res.asString();
        ReadContext context = JsonPath.parse(json);
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
    @DisplayName("Function using test with /users resource")
    void functionUsingTest() {
        Response res = get("https://jsonplaceholder.typicode.com/users");
        String json = res.asString();
        ReadContext context = JsonPath.parse(json);
        List<Integer> ids = context.read("$.[?(@.name == \"Leanne Graham\")]." +
                "address[?(@.city == \"Gwenborough\")].geo[?(@.lat == \"-37.3159\")].length()");
        assertTrue(ids.get(0).equals(2));
    }
}
