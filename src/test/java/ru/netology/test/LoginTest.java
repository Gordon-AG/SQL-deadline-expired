package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.db.DbUtils;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.refresh;
import static org.junit.jupiter.api.Assertions.assertEquals;

// Интеграционные тесты для проверки формы входа и взаимодействия с БД
public class LoginTest {

    private static final String INVALID_LOGIN_ERROR = "Неверно указан логин или пароль";

    // Глобальная настройка Selenide перед запуском тестов
    @BeforeAll
    static void setupAll() {
        Configuration.headless = false;        // true — без графического интерфейса
        Configuration.timeout = 10000;        // таймаут ожидания элементов (10 сек)
    }

    // Полная очистка базы данных после завершения всех тестов
    @AfterAll
    static void tearDownAll() {
        DbUtils.cleanDatabase();
    }

    // Подготовка окружения перед каждым тестом (очистка временных таблиц и открытие SUT)
    @BeforeEach
    void setup() {
        DbUtils.cleanTables();
        open("http://localhost:9999");
    }

    // Тест на успешный логин с получением одноразового кода из базы данных
    @Test
    void shouldLoginSuccessfullyWithDbCode() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getValidAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var code = DataHelper.getValidCodeFor(authInfo);   // код из БД
        var dashboard = verificationPage.validVerify(code);
        dashboard.shouldBeVisible();
    }

    // Тест на проверку того, что SUT не блокирует пользователя после трех неверных попыток
    @Test
    void shouldNotBlockAfterThreeInvalidAttempts() {
        var loginPage = new LoginPage();
        var invalid = DataHelper.getInvalidAuthInfo();

        // Три неудачные попытки входа подряд с проверкой уведомления и обновлением страницы
        for (int i = 0; i < 3; i++) {
            loginPage.invalidLogin(invalid);
            loginPage.assertErrorMessage(INVALID_LOGIN_ERROR);
            refresh();
        }

        // Проверка через БД, что статус пользователя остался 'active'
        String actualStatus = DbUtils.getUserStatus(invalid.getLogin());
        assertEquals("active", actualStatus);
    }

    // Тест на появление уведомления об ошибке при неверных данных входа
    @Test
    void shouldShowErrorForInvalidPassword() {
        var loginPage = new LoginPage();
        var invalid = DataHelper.getInvalidAuthInfo();
        loginPage.invalidLogin(invalid);
        loginPage.assertErrorMessage(INVALID_LOGIN_ERROR);
    }
}