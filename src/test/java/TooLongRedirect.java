import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class TooLongRedirect {

    @Test
    public void testTooLongRedirect() {

        int count = 0;
        int StatusCode = 0;
        String url = "https://playground.learnqa.ru/api/long_redirect";

        while (StatusCode != 200) {
                Response response = RestAssured
                        .given()
                        .redirects()
                        .follow(false)
                        .when()
                        .get(url)
                        .andReturn();

                String locationHeader = response.getHeader("location");
                url = locationHeader;
                StatusCode = response.getStatusCode();
                count++;
            }
        System.out.println(count);
    }
    }

