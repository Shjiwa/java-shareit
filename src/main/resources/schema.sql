DROP TABLE IF EXISTS PUBLIC.comments;
DROP TABLE IF EXISTS PUBLIC.bookings;
DROP TABLE IF EXISTS PUBLIC.items;
DROP TABLE IF EXISTS PUBLIC.requests;
DROP TABLE IF EXISTS PUBLIC.users;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(512) NOT NULL,
    requester_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_request_to_users FOREIGN KEY (requester_id) REFERENCES users(id) ON UPDATE RESTRICT ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(512) NOT NULL,
    is_available boolean NOT NULL,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_item_to_users FOREIGN KEY (owner_id) REFERENCES users(id) ON UPDATE RESTRICT ON DELETE CASCADE,
    CONSTRAINT fk_item_to_requests FOREIGN KEY (request_id) REFERENCES requests(id) ON UPDATE RESTRICT ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(16) NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_booking_to_items FOREIGN KEY (item_id) REFERENCES items(id) ON UPDATE RESTRICT ON DELETE CASCADE,
    CONSTRAINT fk_booking_to_users FOREIGN KEY (booker_id) REFERENCES users(id) ON UPDATE RESTRICT ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(1024) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT fk_comment_to_items FOREIGN KEY (item_id) REFERENCES items(id) ON UPDATE RESTRICT ON DELETE CASCADE,
    CONSTRAINT fk_comment_to_users FOREIGN KEY (author_id) REFERENCES users(id) ON UPDATE RESTRICT ON DELETE CASCADE
);