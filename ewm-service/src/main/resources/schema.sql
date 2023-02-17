DROP TABLE IF EXISTS events_compilation_group;
DROP TABLE IF EXISTS event_compilation;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS locations;
DROP TABLE IF EXISTS events;

DROP TABLE IF EXISTS service_users;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS user_subscriptions;
DROP TABLE IF EXISTS event_subscriptions;
DROP TABLE IF EXISTS subscription_notifications;

CREATE TABLE categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR UNIQUE                                      NOT NULL
);


CREATE TABLE events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    category_id        BIGINT,
    annotation         VARCHAR(2000),
    description        VARCHAR(7000),
    event_date         TIMESTAMP WITHOUT TIME ZONE                         NOT NULL,
    paid               boolean,
    participant_limit  integer,
    request_moderation boolean,
    title              VARCHAR(120),
    initiator_id       BIGINT,
    event_status       VARCHAR(120),
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    created_on         TIMESTAMP WITHOUT TIME ZONE                         NOT NULL,
    views              BIGINT,
    location_id        BIGINT

);

CREATE TABLE locations
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    lat float,
    lon float
);

CREATE TABLE service_users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name  VARCHAR(300),
    email VARCHAR(512) UNIQUE
);


CREATE TABLE requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    requester_id BIGINT,
    event_id     BIGINT,
    created      TIMESTAMP WITHOUT TIME ZONE                         NOT NULL,
    status       VARCHAR
);

CREATE TABLE event_compilation
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    title  VARCHAR(512),
    pinned boolean
);

CREATE TABLE events_compilation_group
(
    event_id             BIGINT NOT NULL,
    event_compilation_id BIGINT NOT NULL
);

CREATE TABLE user_subscriptions
(
    id                      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    user_id                 BIGINT                                              NOT NULL,
    subscription_on_user_id BIGINT                                              NOT NULL,
    notify_by_email         boolean,
    notify_by_portal        boolean,
    subscribe_new_events    boolean,
    subscribe_change_events boolean,
    subscribe_delete_events boolean
);

CREATE TABLE event_subscriptions
(
    id                   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    event_id             BIGINT                                              NOT NULL,
    subscriber_id        BIGINT                                              NOT NULL,
    created_on           TIMESTAMP WITHOUT TIME ZONE                         NOT NULL,
    notify_new_events    boolean,
    notify_change_events boolean,
    notify_delete_events boolean,
    event_initiator_id   BIGINT                                              NOT NULL
);

CREATE TABLE subscription_notifications
(
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    subscription_id     BIGINT                                              NOT NULL,
    user_id             BIGINT                                              NOT NULL,
    notification_date   TIMESTAMP WITHOUT TIME ZONE                         NOT NULL,
    notification_type   VARCHAR                                             NOT NULL,
    notification_method VARCHAR                                             NOT NULL,
    payload             VARCHAR(512),
    notified            boolean
);

ALTER TABLE IF EXISTS events
    DROP CONSTRAINT IF EXISTS fr_events_to_categories;
ALTER TABLE IF EXISTS events
    ADD CONSTRAINT fr_events_to_categories FOREIGN KEY (category_id) REFERENCES categories (id)
        ON DELETE CASCADE ON UPDATE CASCADE;



ALTER TABLE IF EXISTS events
    DROP CONSTRAINT IF EXISTS fk_events_to_locations;

ALTER TABLE IF EXISTS events
    ADD CONSTRAINT fk_events_to_locations FOREIGN KEY (location_id) REFERENCES locations (id) ON DELETE CASCADE ON UPDATE CASCADE;


ALTER TABLE IF EXISTS events_compilation_group
    DROP CONSTRAINT IF EXISTS fk_event_compilation_group_to_event_compilation;

ALTER TABLE IF EXISTS events_compilation_group
    ADD CONSTRAINT fk_event_compilation_group_to_event_compilation FOREIGN KEY (event_compilation_id) REFERENCES event_compilation (id)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE IF EXISTS events_compilation_group
    DROP CONSTRAINT IF EXISTS fk_event_compilation_group_to_events;

ALTER TABLE IF EXISTS events_compilation_group
    ADD CONSTRAINT fk_event_compilation_group_to_events FOREIGN KEY (event_id) REFERENCES events (id)
        ON DELETE CASCADE ON UPDATE CASCADE;



ALTER TABLE IF EXISTS requests
    DROP CONSTRAINT IF EXISTS fk_request_to_events;

ALTER TABLE IF EXISTS requests
    ADD CONSTRAINT fk_request_to_events FOREIGN KEY (event_id) REFERENCES events (id)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE IF EXISTS requests
    DROP CONSTRAINT IF EXISTS fk_request_to_service_users;

ALTER TABLE IF EXISTS requests
    ADD CONSTRAINT fk_request_to_service_users FOREIGN KEY (requester_id) REFERENCES service_users (id)
        ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE user_subscriptions
    DROP CONSTRAINT IF EXISTS fk_user_subscriptions_to_service_users;
ALTER TABLE user_subscriptions
    ADD CONSTRAINT fk_user_subscriptions_to_service_users FOREIGN KEY (subscription_on_user_id) REFERENCES service_users (id);

ALTER TABLE event_subscriptions
    DROP CONSTRAINT IF EXISTS fk_event_subscriptions_to_events;

ALTER TABLE event_subscriptions
    ADD CONSTRAINT fk_event_subscriptions_to_events FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE subscription_notifications
    DROP CONSTRAINT IF EXISTS fk_subscription_notifications_to_event_subscriptions;

ALTER TABLE subscription_notifications
    ADD CONSTRAINT fk_subscription_notifications_to_event_subscriptions FOREIGN KEY (subscription_id) REFERENCES event_subscriptions (id) ON DELETE CASCADE ON UPDATE CASCADE;



