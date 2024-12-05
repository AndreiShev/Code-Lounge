CREATE TABLE IF NOT EXISTS ws_notification.notification_settings (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    friend_request BOOLEAN NOT NULL DEFAULT FALSE,
    friend_birthday BOOLEAN NOT NULL DEFAULT FALSE,
    post_comment BOOLEAN NOT NULL DEFAULT FALSE,
    comment_comment BOOLEAN NOT NULL DEFAULT FALSE,
    post BOOLEAN NOT NULL DEFAULT FALSE,
    message BOOLEAN NOT NULL DEFAULT FALSE,
    send_phone_message BOOLEAN NOT NULL DEFAULT FALSE,
    send_email_message BOOLEAN NOT NULL DEFAULT FALSE,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    change_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE ws_notification.notification_settings ADD CONSTRAINT unique_user_id UNIQUE (user_id);

CREATE INDEX idx_notification_settings_user_id ON ws_notification.notification_settings (user_id);