import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CookieTest {

    @Test
    public void testCookie(){

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        Map<String, String > cookies = response.getCookies();
        assertTrue(cookies.containsKey("HomeWork"), "Unexpected key of cookie");
        assertTrue(cookies.containsValue("hw_value"), "Unexpected value of cookie");
    }
}
