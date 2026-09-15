CREATE TABLE IF NOT EXISTS ddd_data (
    id VARCHAR(64) PRIMARY KEY,
    current_value INTEGER NOT NULL,
    version BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS ddd_entity (
    operation_id VARCHAR(64) PRIMARY KEY,
    id VARCHAR(64) NOT NULL,
    business_value INTEGER NOT NULL,
    rule_code VARCHAR(64) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL
);
