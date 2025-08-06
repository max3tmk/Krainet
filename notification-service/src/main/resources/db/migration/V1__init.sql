DROP TABLE IF EXISTS email_notifications CASCADE;


CREATE TABLE IF NOT EXISTS email_notifications (
                                                   id SERIAL PRIMARY KEY,
                                                   subject TEXT,
                                                   text TEXT,
                                                   recipient_email TEXT,
                                                   created_at TIMESTAMP,
                                                   status VARCHAR(16)
    );