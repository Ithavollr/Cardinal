package org.evlis.cardinal.helpers;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:users.db";
    private final Jdbi jdbi;

    public Database() {
        // Initialize JDBI instance connected to SQLite
        this.jdbi = Jdbi.create(DB_URL);

        // Optional: test connection on startup
        try {
            jdbi.useHandle(handle -> handle.execute("SELECT 1"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void storeUserHash(String username, int hash) {
        jdbi.useExtension(UserDao.class, dao -> dao.insertOrReplace(username, hash));
    }

    public Integer getUserHash(String username) {
        return jdbi.withExtension(UserDao.class, dao -> dao.getHashByUsername(username));
    }

    // DAO interface using JDBI annotations
    public interface UserDao {
        @SqlUpdate("INSERT OR REPLACE INTO users (username, hash) VALUES (:username, :hash)")
        void insertOrReplace(String username, int hash);

        @SqlQuery("SELECT hash FROM users WHERE username = :username")
        Integer getHashByUsername(String username);
    }
}
