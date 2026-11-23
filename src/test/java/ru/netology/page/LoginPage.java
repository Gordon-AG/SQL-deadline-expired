package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

// Класс для страницы авторизации
public class LoginPage {
    // Элементы формы входа
    private final SelenideElement loginInput = $("[data-test-id=login] input");
    private final SelenideElement passwordInput = $("[data-test-id=password] input");
    private final SelenideElement loginButton = $("[data-test-id=action-login]");
    private final SelenideElement errorNotification = $("[data-test-id=error-notification] .notification__content");

    // Заполнение поля
    private void setFieldValue(SelenideElement field, String value) {
        field.shouldBe(visible).setValue(value);
    }

    // Общая логика заполнения формы и отправки данных входа
    private void submitLogin(DataHelper.AuthInfo info) {
        setFieldValue(loginInput, info.getLogin());
        setFieldValue(passwordInput, info.getPassword());
        loginButton.click();
    }

    // Успешный ввод логина/пароля и переход к странице верификации
    public VerificationPage validLogin(DataHelper.AuthInfo info) {
        submitLogin(info);
        return new VerificationPage();
    }

    // Ввод неверных данных входа
    public LoginPage invalidLogin(DataHelper.AuthInfo info) {
        submitLogin(info);
        return this;
    }

    // Проверка видимости и текста сообщения об ошибке
    public void assertErrorMessage(String expectedText) {
        errorNotification
                .shouldBe(visible)
                .shouldHave(text(expectedText));
    }

    // Проверка наличия текста блокировки в уведомлении
    public void assertBlocked() {
        errorNotification
                .shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(text("Заблокирован"));
    }
}