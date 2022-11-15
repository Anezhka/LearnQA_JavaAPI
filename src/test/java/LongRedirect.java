import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class LongRedirect {

    @Test
    public void testLongRedirect(){
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                //.get("https://playground.learnqa.ru/")
                //.get("https://learnqa.ru/")
                //.get("https://www.learnqa.ru/")
                .andReturn();

        String locationHeader = response.getHeader("location");
        System.out.println(locationHeader);

    }
}
