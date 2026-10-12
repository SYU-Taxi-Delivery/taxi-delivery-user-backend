-- Preserve V1 for databases that already applied it. USERS is the v4 conceptual name.
RENAME TABLE app_user TO users;
-- v4 excludes FINISHED, including expiry computed at read time. Author row locking
-- in PostService makes conflict checks + creation/time changes atomic instead.
CREATE INDEX ix_post_author_reference ON post(author_id, reference_time, status);
ALTER TABLE post DROP INDEX uq_post_author_reference;
ALTER TABLE post DROP COLUMN valid_reference_time;
ALTER TABLE post ADD COLUMN deleted_by BIGINT NULL;
ALTER TABLE post ADD CONSTRAINT fk_post_deleted_by FOREIGN KEY (deleted_by) REFERENCES users(id);
ALTER TABLE post ADD CONSTRAINT ck_post_places CHECK (
    (type = 'TAXI' AND CHAR_LENGTH(TRIM(departure)) > 0 AND CHAR_LENGTH(TRIM(destination)) > 0)
    OR (type = 'DELIVERY' AND CHAR_LENGTH(TRIM(restaurant_name)) > 0 AND CHAR_LENGTH(TRIM(pickup_location)) > 0));
