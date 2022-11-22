package lib;

import io.restassured.http.Headers;
import io.restassured.response.Response;

import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTestCase {
    protected String getHeader(Response response, String name) {
        Headers headers = response.getHeaders(); // <-- передаем header

        assertTrue(headers.hasHeaderWithName(name), "Response doesn't have header with name " + name); // убеждаемся, что значение пришло и соответствует
        return headers.getValue(name); // возвращаем это значение
    }

    protected String getCookie(Response response, String name) {
        Map<String, String> cookies = response.getCookies(); // <-- передаем cookie

        assertTrue(cookies.containsKey(name), "Response doesn't have cookie with name " + name); // убеждаемся, что значение пришло и соответствует
        return cookies.get(name); // возвращаем это значение
    }

    protected int getIntFromJson(Response response, String name) {
        response.then().assertThat().body("$", hasKey(name)); // здесь убеждаемся, что в json'е есть нужное поле
        // знак $ указывает, что ищем поле в корне json. Если бы поле было не в корне, указали бы путь к полю
        return response.jsonPath().getInt(name); // если поле есть, получаем его имя
    }
}
