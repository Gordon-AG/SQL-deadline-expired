package ru.netology.db;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;

public class DbUtils {
    // Параметры подключения к базе данных
    private static final String DB_URL = "jdbc:mysql://localhost:3306/app";
    private static final String DB_USER = "app";
    private static final String DB_PASS = "pass";

    private DbUtils() {
    }

    // --- Чтение данных ---

    /**
     * Получение последнего кода верификации из БД с retry-ожиданием.
     */
    @SneakyThrows
    public static String getLastAuthCode(String login) {
        var sql = "SELECT code FROM auth_codes " +
                "WHERE user_id = (SELECT id FROM users WHERE login = ?) " +
                "ORDER BY created DESC LIMIT 1;";
        var runner = new QueryRunner();

        // Опрос базы данных до 5 секунд (10 попыток по 500мс)
        for (int i = 0; i < 10; i++) {
            try (var conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String code = runner.query(conn, sql, new ScalarHandler<>(), login);
                if (code != null) {
                    return code;
                }
            }
            Thread.sleep(500);
        }
        throw new IllegalStateException(
                "Код аутентификации не найден в БД для пользователя " + login +
                        " за 5 секунд. Проверьте, что SUT запущен и подключён к БД."
        );
    }

    /**
     * Получение текущего статуса пользователя.
     */
    @SneakyThrows
    public static String getUserStatus(String login) {
        var sql = "SELECT status FROM users WHERE login = ?;";
        var runner = new QueryRunner();
        try (var conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            return runner.query(conn, sql, new ScalarHandler<>(), login);
        }
    }

    // --- Управление статусом пользователя ---

    /**
     * Ручная установка статуса 'blocked' для пользователя.
     */
    @SneakyThrows
    public static void blockUser(String login) {
        var runner = new QueryRunner();
        try (var conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            runner.update(conn, "UPDATE users SET status = 'blocked' WHERE login = ?;", login);
        }
    }

    // --- Очистка данных ---

    /**
     * Очистка временных таблиц и сброс статусов перед каждым тестом.
     */
    @SneakyThrows
    public static void cleanTables() {
        var runner = new QueryRunner();
        try (var conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            runner.update(conn, "DELETE FROM card_transactions;");
            runner.update(conn, "DELETE FROM auth_codes;");
            runner.update(conn, "UPDATE users SET status = 'active' WHERE status = 'blocked';");
        }
    }

    /**
     * Полная очистка всей базы данных (с отключением внешних ключей).
     */
    @SneakyThrows
    public static void cleanDatabase() {
        var runner = new QueryRunner();
        try (var conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            runner.update(conn, "SET FOREIGN_KEY_CHECKS = 0;");
            runner.update(conn, "DELETE FROM card_transactions;");
            runner.update(conn, "DELETE FROM auth_codes;");
            runner.update(conn, "DELETE FROM cards;");
            runner.update(conn, "DELETE FROM users;");
            runner.update(conn, "SET FOREIGN_KEY_CHECKS = 1;");
        }
    }
}