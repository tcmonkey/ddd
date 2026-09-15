CREATE TABLE IF NOT EXISTS ddd_data (
    id VARCHAR(64) PRIMARY KEY,
    current_value INTEGER NOT NULL,
    version BIGINT NOT NULL,
    entities_json CLOB NOT NULL
);
