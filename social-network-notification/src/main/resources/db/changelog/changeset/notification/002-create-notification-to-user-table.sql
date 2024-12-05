CREATE TABLE IF NOT EXISTS ws_notification.notifications_to_users (
    id UUID PRIMARY KEY,
    notification_id UUID NOT NULL,
    to_user_id UUID NOT NULL,
    is_status_sent BOOLEAN NOT NULL DEFAULT FALSE
);