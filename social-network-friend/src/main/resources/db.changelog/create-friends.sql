CREATE TABLE friends (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id UUID NOT NULL,
    friend_id UUID NOT NULL,
    status_code VARCHAR(255) NOT NULL,
    UNIQUE (account_id, friend_id),
    INDEX friend_account_id_index (account_id),
    INDEX friend_friend_id_index (friend_id)
);
INSERT INTO friends (account_id, friend_id, status_code)
VALUES

    ('3fa85f64-5717-4562-b3fc-2c963f66afa6', '87765f64-5717-4562-b3fc-2c963f66afa8', 'PENDING');
