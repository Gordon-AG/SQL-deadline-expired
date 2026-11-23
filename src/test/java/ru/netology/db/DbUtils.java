package ru.netology.db;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;

// Утилитный класс для работы с базой данных MySQL
public class DbUtils {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/app";
    private static final String DB_USER = "app";
    private static final String DB_PASS = "pass";

    private DbUtils() {
    }

    // --- Чтение данных ---

    // Получение последнего кода верификации из таблицы auth_codes
    @SneakyThrows
    public static String getLastAuthCode(String login) {
        var sql = "SELECT code FROM auth_codes " +
                "WHERE user_id = (SELECT id FROM users WHERE login = ?) " +
                "ORDER BY created DESC LIMIT 1;";
        var runner = new QueryRunner();
        try (var conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            return runner.query(conn, sql, new ScalarHandler<>(), login);
        }
    }

    // Получение текущего статуса пользователя
    @SneakyThrows
    public static String getUserStatus(String login) {
        var sql = "SELECT status FROM users WHERE login = ?;";
        var runner = new QueryRunner();
        try (var conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            return runner.query(conn, sql, new ScalarHandler<>(), login);
        }
    }

    // --- Управление статусом пользователя ---

    // Ручная установка статуса 'blocked' для пользователя
    @SneakyThrows
    public static void blockUser(String login) {
        var runner = new QueryRunner();
        try (var conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            runner.update(conn, "UPDATE users SET status = 'blocked' WHERE login = ?;", login);
        }
    }

    // --- Очистка данных ---

    // Очистка временных таблиц и сброс статусов перед каждым тестом
    @SneakyThrows
    public static void cleanTables() {
        var runner = new QueryRunner();
        try (var conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            runner.update(conn, "DELETE FROM card_transactions;");
            runner.update(conn, "DELETE FROM auth_codes;");
            runner.update(conn, "UPDATE users SET status = 'active' WHERE status = 'blocked';");
        }
    }

    // Полная очистка всех таблиц базы данных после завершения тестов
    @SneakyThrows
    public static void cleanDatabase() {
        var runner = new QueryRunner();
        try (var conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            runner.update(conn, "DELETE FROM card_transactions;");
            runner.update(conn, "DELETE FROM auth_codes;");
            runner.update(conn, "DELETE FROM cards;");
            runner.update(conn, "DELETE FROM users;");
        }
    }
}