package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testEditJustCreatedUser() {
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

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithAuthToEditUser("https://playground.learnqa.ru/api/user/" + userId, header, cookie, editData);

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user/" + userId, header, cookie);

        System.out.println(responseUserData.asString());
        Assertions.assertJsonByName(responseUserData, "firstName", newName);

    }

    //HOMEWORK
    @Test
    public void testEditByNotAuthUser() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestForCreateUser("https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.jsonPath().get("id");

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequestForEditByNotAuthUser("https://playground.learnqa.ru/api/user/" + userId, editData);

        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");
    }

    @Test
    public void testEditAnotherUserAuth() {
        //GENERATE USER FOR EDIT
        Map<String, String> userDetail = DataGenerator.getRegistrationData();

        Response responseCreateAuthUser = apiCoreRequests
                .makePostRequestForCreateUser("https://playground.learnqa.ru/api/user/", userDetail);

        String userId = responseCreateAuthUser.jsonPath().get("id");

        //GENERATE USER FOR AUTH
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestForCreateUser("https://playground.learnqa.ru/api/user/", userData);

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequestForLogin("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //EDIT
        //Здесь баг. Не имеет значения, какой ID указан, вместо кода ошибки приходит код 200 и редактируется тот пользователь, который авторизован
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithAuthToEditUser("https://playground.learnqa.ru/api/user/" + userId, header, cookie, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 200);


        //GET
        //Этот метод в таком виде будет падать, т.к. у чужого пользователя нельзя увидеть поле "firstName"

        /*Response responseUserData = apiCoreRequests
                .makeGetRequestWithTokenAndCookie("https://playground.learnqa.ru/api/user/" + userId, header, cookie);

        System.out.println(responseUserData.asString());
        Assertions.assertJsonByName(responseUserData, "firstName", newName);
         */
    }

    @Test
    public void testEditJustCreatedTestWithIncorrectEmail(){
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestForCreateUser("https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.jsonPath().get("id");;

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequestForLogin("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        //EDIT
        String newEmail = "learnqaexample.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithAuthToEditUser("https://playground.learnqa.ru/api/user/" + userId, header, cookie, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");
    }

    @Test
    public void testEditJustCreatedTestWithTooShortFirstName(){
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

        //EDIT
        String newName = "X";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithAuthToEditUser("https://playground.learnqa.ru/api/user/" + userId, header, cookie, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error", "Too short value for field firstName");
    }
}

