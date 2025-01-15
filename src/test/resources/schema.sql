CREATE TABLE products (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6),
    description VARCHAR(255) NOT NULL,
    last_updated_at DATETIME(6),
    name VARCHAR(255) NOT NULL,
    price DECIMAL(38, 2) NOT NULL,
    stock INTEGER NOT NULL,
    PRIMARY KEY (id)
);
