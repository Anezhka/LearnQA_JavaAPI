package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Get data cases")
@Feature("Get data")
public class UserGetTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("This test try to get data by not auth user")
    @DisplayName("Test negative get data by not auth user")
    @Test
    @Owner(value = "Анна Синицына")
    @Severity(value = SeverityLevel.BLOCKER)
    public void testGetUserDataNotAuth() {
        // тест для неавторизованного запроса. В ответ получим только параметр username и проверяем, что нет других параметров
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Description("This test successfully get user data")
    @DisplayName("Test positive get data")
    @Test
    @Owner(value = "Анна Синицына")
    @Severity(value = SeverityLevel.CRITICAL)
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    //HOMEWORK
    @Description("This test try to get user data by another auth user")
    @DisplayName("Test negative get data by another auth user")
    @Test
    @Owner(value = "Анна Синицына")
    @Severity(value = SeverityLevel.CRITICAL)
    public void testGetUserDetailsAuthAsAnotherUser() {
        //GENERATE USER
        String email = DataGenerator.getRandomEmail();
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests //создаю нового пользователя
                .makePostRequestForCreateUser("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
        String userId = responseCreateAuth.jsonPath().get("id"); //записываю id созданного пользователя в переменную userId

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests //авторизуюсь под учебным пользователем
                .makePostRequestForLogin("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //GET
        Response responseUserData = apiCoreRequests //под учебным пользователем проверяю данные созданного пользователя
                .makeGetRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user/" + userId, header, cookie);

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
        //System.out.println(responseUserData.asString());
    }
}
