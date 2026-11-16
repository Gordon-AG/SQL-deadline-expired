package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

// Page Object класс для страницы авторизации
public class LoginPage {
    // Элементы формы входа
    private final SelenideElement loginInput = $("[data-test-id=login] input");
    private final SelenideElement passwordInput = $("[data-test-id=password] input");
    private final SelenideElement loginButton = $("[data-test-id=action-login]");
    private final SelenideElement errorNotification = $("[data-test-id=error-notification] .notification__content");

    /**
     * Очистка и корректное заполнение React-инпута через Ctrl+A и Delete.
     */
    private void setFieldValue(SelenideElement field, String value) {
        field.shouldBe(visible).click();
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        field.sendKeys(Keys.DELETE);
        field.sendKeys(value);
    }

    // Успешный ввод логина/пароля и переход к странице верификации
    public VerificationPage validLogin(DataHelper.AuthInfo info) {
        setFieldValue(loginInput, info.getLogin());
        setFieldValue(passwordInput, info.getPassword());
        loginButton.click();
        // Ждем появления поля кода для синхронизации с записью в БД
        $("[data-test-id=code] input").shouldBe(visible);
        return new VerificationPage();
    }

    // Ввод неверных данных с проверкой появления ошибки
    public LoginPage invalidLogin(DataHelper.AuthInfo info) {
        setFieldValue(loginInput, info.getLogin());
        setFieldValue(passwordInput, info.getPassword());
        loginButton.click();
        errorNotification.shouldBe(visible);
        return this;
    }

    // Проверка наличия текста блокировки в уведомлении
    public void assertBlocked() {
        errorNotification
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text("Заблокирован"));
    }
}