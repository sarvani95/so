use requestdb;

ALTER TABLE request_processing_data ADD COLUMN IF NOT EXISTS IS_DATA_INTERNAL TINYINT NOT NULL DEFAULT '0';