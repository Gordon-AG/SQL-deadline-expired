package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

// Класс страницы верификации
public class VerificationPage {
    private final SelenideElement codeInput = $("[data-test-id=code] input");
    private final SelenideElement verifyButton = $("[data-test-id=action-verify]");

    // Ожидаем появления поля ввода кода — к этому моменту SUT уже записал код в БД
    public VerificationPage() {
        codeInput.shouldBe(visible);
    }

    // Заполнение поля
    private void setFieldValue(SelenideElement field, String value) {
        field.shouldBe(visible).setValue(value);
    }

    // Ввод кода верификации и переход
    public DashboardPage validVerify(DataHelper.VerificationCode code) {
        setFieldValue(codeInput, code.getCode());
        verifyButton.click();
        return new DashboardPage();
    }
}