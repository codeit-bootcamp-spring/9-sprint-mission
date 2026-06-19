ALTER TABLE notifications
    ADD COLUMN event_key VARCHAR(150);

ALTER TABLE notifications
    ADD CONSTRAINT uk_notifications_receiver_event UNIQUE (receiver_id, event_key);
