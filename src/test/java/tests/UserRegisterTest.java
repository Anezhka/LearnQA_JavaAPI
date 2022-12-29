package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Registration cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("This test try to register user with existing email")
    @DisplayName("Test negative register with existing email")
    @Test
    @Owner(value = "Анна Синицына")
    @Severity(value = SeverityLevel.BLOCKER)
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");

    }

    @Description("This test successfully register user")
    @DisplayName("Test positive register")
    @Test
    @Owner(value = "Анна Синицына")
    @Severity(value = SeverityLevel.BLOCKER)
    public void testCreateUserSuccessfully() {
        //String email = DataGenerator.getRandomEmail();
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestForCreateUser("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    //HOMEWORK
    @Test
    @Owner(value = "Анна Синицына")
    @Description("This test checks creating user with incorrect email - w/o @")
    @DisplayName("Test negative creation user with incorrect email")
    @Severity(value = SeverityLevel.CRITICAL)
    public void testNegativeCreateUserWithIncorrectEmail() {
        Map<String, String> userData = DataGenerator.getIncorrectEmailRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestForCreateUser("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }


    @Description("This test checks creating user w/o one of the parameter")
    @DisplayName("Test negative creation user")
    @ParameterizedTest
    @CsvSource(
            {", 123, learnqa, learnqa, learnqa, The value of 'email' field is too short",
                    "email,, learnqa, learnqa, learnqa, The following required params are missed: password",
                    "email, 123,, learnqa, learnqa, The following required params are missed: username",
                    "email, 123, learnqa,, learnqa, The following required params are missed: firstName",
                    "email, 123, learnqa, learnqa,, The following required params are missed: lastName"
            }
    )
    @Owner(value = "Анна Синицына")
    @Severity(value = SeverityLevel.BLOCKER)
    public void testNegativeCreateUserWithoutOneParameter(String email, String password, String username, String firstName, String lastName, String expectedResponse) {
        Map<String, String> authData;
        authData = DataGenerator.getRegistrationDataNegative(email, password, username, firstName, lastName);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestForCreateUser("https://playground.learnqa.ru/api/user/", authData);

        Assertions.assertResponseTextEquals(responseCreateAuth, expectedResponse);
    }


    @Test
    @Owner(value = "Анна Синицына")
    @Description("This test checks creating user with too short username")
    @DisplayName("Test negative creation user")
    @Severity(value = SeverityLevel.NORMAL)
    public void testNegativeCreateUserWithTooShortUsername() {
        Map<String, String> userData = DataGenerator.getIncorrectRegistrationDataWithTooShortUsername();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestForCreateUser("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
    }

    @Test
    @Owner(value = "Анна Синицына")
    @Description("This test checks creating user with too long username")
    @DisplayName("Test negative creation user")
    @Severity(value = SeverityLevel.CRITICAL)
    public void testNegativeCreateUserWithTooLongUsername() {
        Map<String, String> userData = DataGenerator.getIncorrectRegistrationDataWithTooLongUsername();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestForCreateUser("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
    }

}
