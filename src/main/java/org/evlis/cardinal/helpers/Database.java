package org.evlis.cardinal.helpers;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database {
    private final Jdbi jdbi;
    // Single-threaded executor to process DB tasks sequentially
    private final ExecutorService dbExecutor; // <--- New Executor

    public Database(String dbUrl) {
        // Initialize JDBI instance connected to SQLite
        this.jdbi = Jdbi.create(dbUrl);
        // Initialize the Single-Thread Executor
        // This ensures all tasks submitted to it are run one-by-one, in order.
        this.dbExecutor = Executors.newSingleThreadExecutor();
        this.createPlayerTable();

        // Optional: test connection on startup
        try {
            jdbi.useHandle(handle -> handle.execute("SELECT 1"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPlayerTable() {
        // Use a handle to execute the table creation SQL
        jdbi.useHandle(handle -> {
            handle.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "uuid TEXT PRIMARY KEY," + // Use UUID as the unique identifier
                    "username TEXT NOT NULL," +
                    "global_vars TEXT NOT NULL" + // Use TEXT for storing serialized data (e.g., JSON)
                    ");");
        });
    }

    // --- ASYNCHRONOUS, ORDERED METHODS ---
    // 1. Ensures player exists, returns a CompletableFuture for chaining
    public CompletableFuture<Void> ensurePlayerExists(String uuid, String username) {
        return CompletableFuture.runAsync(() -> { // Submit to single-thread executor
            jdbi.useExtension(PlayerDao.class, dao -> dao.insertOrIgnore(uuid, username, "{}"));
        }, dbExecutor); // <--- Use the internal dbExecutor
    }

    // 2. Retrieves global variables, returns a CompletableFuture<Optional<String>>
    public CompletableFuture<Optional<String>> getGlobalVars(String uuid) {
        return CompletableFuture.supplyAsync(() -> { // Submit to single-thread executor
            return jdbi.withExtension(PlayerDao.class, dao -> dao.getGlobalVarsByUUID(uuid));
        }, dbExecutor); // <--- Use the internal dbExecutor
    }

    // 3. Updates global variables
    public CompletableFuture<Void> setGlobalVars(String uuid, String globalVarsJson) {
        return CompletableFuture.runAsync(() -> { // Submit to single-thread executor
            jdbi.useExtension(PlayerDao.class, dao -> dao.updateGlobalVars(uuid, globalVarsJson));
        }, dbExecutor);
    }

    // Recommended: Add a shutdown method for onDisable
    public void shutdown() {
        dbExecutor.shutdown();
    }

    // DAO interface (remains the same)
    public interface PlayerDao {
        @SqlUpdate("INSERT OR IGNORE INTO players (uuid, username, global_vars) VALUES (:uuid, :username, :global_vars)")
        void insertOrIgnore(@Bind("uuid") String uuid, @Bind("username") String username, @Bind("global_vars") String globalVars);

        @SqlQuery("SELECT global_vars FROM players WHERE uuid = :uuid")
        Optional<String> getGlobalVarsByUUID(@Bind("uuid") String uuid);

        @SqlUpdate("UPDATE players SET global_vars = :global_vars WHERE uuid = :uuid")
        void updateGlobalVars(@Bind("uuid") String uuid, @Bind("global_vars") String globalVars);
    }
}
