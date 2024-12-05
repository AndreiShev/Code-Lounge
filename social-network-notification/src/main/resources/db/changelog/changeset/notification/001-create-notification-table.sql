CREATE TABLE IF NOT EXISTS ws_notification.notifications (
    id UUID PRIMARY KEY,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    author_id UUID NOT NULL,
    content VARCHAR(500) NOT NULL,
    notification_type VARCHAR(50) NOT NULL
);