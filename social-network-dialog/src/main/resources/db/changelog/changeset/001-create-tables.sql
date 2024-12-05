--liquibase formatted sql

--changeset liquibase-docs:create-table

CREATE TABLE dialogs (
    id BIGSERIAL PRIMARY KEY,
    participant_one_id UUID NOT NULL,
    participant_two_id UUID NOT NULL
);

CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    time TIMESTAMP NOT NULL,
    author_id UUID NOT NULL,
    recipient_id UUID NOT NULL,
    message_text TEXT NOT NULL,
    status VARCHAR(255) NOT NULL,
    dialog_id BIGINT,
    CONSTRAINT fk_message_dialog FOREIGN KEY (dialog_id) REFERENCES dialogs(id) ON DELETE CASCADE,
    CONSTRAINT chk_status CHECK (status IN ('SENT', 'DELIVERED', 'READ'))
);
