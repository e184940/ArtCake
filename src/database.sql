CREATE TABLE artcake.cakes
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    price       DECIMAL(10, 2),
    image_url   VARCHAR(500),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE artcake.allergens
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE artcake.cake_allergens
(
    cake_id     INT NOT NULL,
    allergen_id INT NOT NULL,
    PRIMARY KEY (cake_id, allergen_id),
    FOREIGN KEY (cake_id) REFERENCES artcake.cakes (id) ON DELETE CASCADE,
    FOREIGN KEY (allergen_id) REFERENCES artcake.allergens (id) ON DELETE CASCADE
);
