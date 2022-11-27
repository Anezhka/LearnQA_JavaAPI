package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    // этот метод будет статический, т.к. класс Assertions не является прямым наследником
    // для наших тестов и, чтобы использовать функции этого класса в тестах,
    // потребуется или сначала каждый раз создавать объект Assertions и вызывать функции от этого объекта,
    // или СДЕЛАТЬ ФУНКЦИИ СТАТИЧЕСКИМИ <-- это удобней.

    public static void assertJsonByName(Response response, String name, int expectedValue) {
        // на вход функция получает объект с ответом сервера, чтобы вытянуть из него текст,
        // а также имя, по которому нужно искать значение в json и ожидаемое значение,
        // а на выходе будет происходить assert, где эта функция сравнивает ожидаемый рез-т и рез-т из json'а
        // и если оно правильное, то assert проходит, если нет, то выдается сообщение об ошибке.
        response.then().assertThat().body("$", hasKey(name));

        int value = response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "JSON value is not equal to expected value");
    }

    public static void assertJsonByName(Response response, String name, String  expectedValue) {
        response.then().assertThat().body("$", hasKey(name));

        String value = response.jsonPath().getString(name);
        assertEquals(expectedValue, value, "JSON value is not equal to expected value");
    }

    public static void assertResponseTextEquals(Response response, String expectedAnswer) {
        assertEquals(
               expectedAnswer,
               response.asString(),
               "Response text is not as expected"
        );
    }

    public static void assertResponseCodeEquals(Response response, int expectedStatusCode) {
        assertEquals(
                expectedStatusCode,
                response.statusCode(),
                "Response status code is not as expected"
        );
    }
    public static void assertJsonHasField(Response response, String expectedFieldName) {
        response.then().assertThat().body("$", hasKey(expectedFieldName));
    }
    public static void assertJsonHasFields (Response response, String[] expectedFieldNames) {
        // метод проверяет все поля через цикл for
        for (String expectedFieldName : expectedFieldNames) {
            Assertions.assertJsonHasField(response, expectedFieldName);
        }
    }
    public static void assertJsonHasNotField(Response response, String unexpectedFieldName) {
        response.then().assertThat().body("$", not(hasKey(unexpectedFieldName)));
    }
}
