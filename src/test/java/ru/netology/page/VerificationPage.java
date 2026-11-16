package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

// Page Object класс для страницы верификации (ввода одноразового кода)
public class VerificationPage {
    // Элементы формы ввода кода
    private final SelenideElement codeInput = $("[data-test-id=code] input");
    private final SelenideElement verifyButton = $("[data-test-id=action-verify]");

    /**
     * Очистка и корректное заполнение React-инпута через Ctrl+A и Delete.
     */
    private void setFieldValue(SelenideElement field, String value) {
        field.shouldBe(visible).click();
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        field.sendKeys(Keys.DELETE);
        field.sendKeys(value);
    }

    // Ввод валидного кода подтверждения и переход на дашборд
    public DashboardPage validVerify(DataHelper.VerificationCode code) {
        setFieldValue(codeInput, code.getCode());
        verifyButton.click();
        return new DashboardPage();
    }
}