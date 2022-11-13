CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(256)                            NOT NULL,
    email VARCHAR(256)                            NOT NULL,
    CONSTRAINT pk_user       PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(256)                            NOT NULL,
    CONSTRAINT pk_category      PRIMARY KEY (id),
    CONSTRAINT uq_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS locations
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    latitude  FLOAT                                   NOT NULL,
    longitude FLOAT                                   NOT NULL,
    CONSTRAINT pk_location PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation         VARCHAR(2000)                           NOT NULL,
    category_id        BIGINT                                  NOT NULL,
    confirmed_requests INT,
    created_on         TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    description        VARCHAR(7000)                           NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    initiator_id       BIGINT                                  NOT NULL,
    location_id        BIGINT                                  NOT NULL,
    paid               BOOLEAN                                 NOT NULL,
    participant_limit  INT                                     NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN                                 NOT NULL,
    state              VARCHAR(32)                             NOT NULL,
    title              VARCHAR(120)                            NOT NULL,
    CONSTRAINT pk_event                PRIMARY KEY (id),
    CONSTRAINT uq_event_title          UNIQUE (title),
    CONSTRAINT fk_event_to_category_id FOREIGN KEY (category_id)  REFERENCES categories (id),
    CONSTRAINT fk_event_to_user_id     FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT fk_event_to_location_id FOREIGN KEY (location_id)  REFERENCES locations (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_on   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    event_id     BIGINT                                  NOT NULL,
    requester_id BIGINT                                  NOT NULL,
    status       VARCHAR(32)                             NOT NULL,
    CONSTRAINT pk_request                 PRIMARY KEY (id),
    CONSTRAINT fk_request_to_event_id     FOREIGN KEY (event_id)     REFERENCES events (id),
    CONSTRAINT fk_request_to_requester_id FOREIGN KEY (requester_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned BOOLEAN,
    title  VARCHAR(120)                            NOT NULL,
    CONSTRAINT pk_compilation       PRIMARY KEY (id),
    CONSTRAINT uq_compilation_title UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS events_compilations
(
    event_id       BIGINT,
    compilation_id BIGINT,
    CONSTRAINT pk_events_compilations    PRIMARY KEY (event_id, compilation_id),
    CONSTRAINT fk_between_event_id       FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_between_compilation_id FOREIGN KEY (compilation_id) REFERENCES compilations (id)
);