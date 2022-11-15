import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

public class Tokens {

    @Test
    public void testLongTimeJob() throws InterruptedException {

        JsonPath responseGetWithoutToken = RestAssured
                .given()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String responseToken = responseGetWithoutToken.get("token");
        int responseSeconds = responseGetWithoutToken.get("seconds");
        System.out.println("1. Job has been created.");


        JsonPath responseGetWithToken = RestAssured
                .given()
                .param("token", responseToken)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String statusText = responseGetWithToken.get("status");
        String expectedStatus = "Job is NOT ready";
        if (statusText.equals(expectedStatus)) {
            System.out.println("2. \"status\" field is: " + "\"Job is NOT ready\"");
        } else
            System.out.println("Job is ready");


        int timeForWait = responseSeconds * 1000;
        Thread.sleep(timeForWait);


        JsonPath responseGetReady = RestAssured
                .given()
                .param("token", responseToken)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String statusReady = responseGetReady.get("status");
        String expectedStatusReady = "Job is ready";
        String result = responseGetReady.get("result");
        if (statusReady.equals(expectedStatusReady) && result != null) {
            System.out.println("3. \"status\" field is: " + "\"Job is ready\"" + " and \"result\" field is exist.");
        } else
            System.out.println("Job is not ready");
    }
}

