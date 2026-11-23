package ru.netology.page;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

// Page Object класс для главной страницы (дашборда)
public class DashboardPage {

    // Проверка успешного отображения дашборда после авторизации
    public void shouldBeVisible() {
        $("[data-test-id=dashboard]")
                .shouldBe(visible)
                .shouldHave(exactText("Личный кабинет"));
    }
}