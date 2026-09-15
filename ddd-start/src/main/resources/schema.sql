CREATE TABLE IF NOT EXISTS ddd_data (
    id VARCHAR(64) PRIMARY KEY,
    current_value INTEGER NOT NULL,
    version BIGINT NOT NULL,
    entities_json CLOB NOT NULL
);

CREATE TABLE IF NOT EXISTS ddd_rule (
    rule_code VARCHAR(64) PRIMARY KEY,
    factor INTEGER NOT NULL,
    reason VARCHAR(255) NOT NULL
);
