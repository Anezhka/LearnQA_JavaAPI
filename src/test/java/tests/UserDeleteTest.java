package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Deletion cases")
@Feature("Deletion")
public class UserDeleteTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Owner(value = "Анна Синицына")
    @Description("This test try to delete admin test user with ID=2")
    @DisplayName("Test negative deletion admin test user")
    @Severity(value = SeverityLevel.MINOR)
    public void testDeleteUserWithId2() {
    //LOGIN
    Map<String, String> authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");

    Response responseGetAuth = apiCoreRequests //авторизуюсь под учебным пользователем
            .makePostRequestForLogin("https://playground.learnqa.ru/api/user/login", authData);

    String header = this.getHeader(responseGetAuth, "x-csrf-token");
    String cookie = this.getCookie(responseGetAuth, "auth_sid");
    String userId = responseGetAuth.jsonPath().get("id");

    //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequestByAuthUser("https://playground.learnqa.ru/api/user/" + userId, header, cookie);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 404);

        ResponseBody body = responseDeleteUser.getBody();
        String bodyAsString = body.asString();
        assertTrue(bodyAsString.contains("This is 404 error!"), "Something went wrong. Should be impossible to delete user with id = 2");
    }

    @Description("This test successfully delete just created user")
    @DisplayName("Test positive deletion just created user")
    @Test
    @Owner(value = "Анна Синицына")
    @Severity(value = SeverityLevel.MINOR)
    public void testDeleteJustCreatedUser() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestForCreateUser("https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.jsonPath().get("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequestForLogin("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequestByAuthUser("https://playground.learnqa.ru/api/user/" + userId, header, cookie);

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user/" + userId, header, cookie);

        Assertions.assertResponseCodeEquals(responseUserData, 404);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");

        Assertions.assertJsonHasNotField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Description("This test try to delete user by another auth user")
    @DisplayName("Test negative deletion by another auth user")
    @Test
    @Owner(value = "Анна Синицына")
    @Severity(value = SeverityLevel.CRITICAL)
    public void testNegativeDeleteByAnotherUserAuth() {
        //GENERATE USER FOR DELETE
        Map<String, String> userDetail = DataGenerator.getRegistrationData();

        Response responseCreateAuthUser = apiCoreRequests
                .makePostRequestForCreateUser("https://playground.learnqa.ru/api/user/", userDetail);

        String userId = responseCreateAuthUser.jsonPath().get("id");

        //GENERATE USER FOR AUTH
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestForCreateUser("https://playground.learnqa.ru/api/user/", userData);

        String IdUser = responseCreateAuth.jsonPath().get("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequestForLogin("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //DELETE
        //Здесь баг. Не имеет значения, какой ID указан, вместо кода ошибки приходит код 200 и удаляется тот пользователь, который авторизован
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequestByAuthUser("https://playground.learnqa.ru/api/user/" + userId, header, cookie);

        //System.out.println(responseDeleteUser.getStatusCode());

        //GET - ПРОВЕРКА
        //Этот метод убеждается, что текущий пользователь был удален.
        Response responseUserData = apiCoreRequests
                .makeGetRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user/" + IdUser, header, cookie);

        Assertions.assertResponseCodeEquals(responseUserData, 404);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }
}

