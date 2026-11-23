package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;
import ru.netology.db.DbUtils;

import java.util.Locale;

public class DataHelper {
    // Генератор случайных тестовых данных
    private static final Faker faker = new Faker(new Locale("en"));

    // Утилитный класс — запрещаем создание экземпляров
    private DataHelper() {
    }

    // Валидные данные первого пользователя
    public static AuthInfo getValidAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    // Валидные данные второго пользователя
    public static AuthInfo getSecondValidAuthInfo() {
        return new AuthInfo("petya", "123qwerty");
    }

    // Неверные данные для проверки ошибок (реальный логин + случайный пароль)
    public static AuthInfo getInvalidAuthInfo() {
        return new AuthInfo("vasya", faker.internet().password());
    }

    // Запрос актуального кода верификации из базы данных
    public static VerificationCode getValidCodeFor(AuthInfo authInfo) {
        String code = DbUtils.getLastAuthCode(authInfo.getLogin());
        return new VerificationCode(code);
    }

    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    @Value
    public static class VerificationCode {
        String code;
    }
}