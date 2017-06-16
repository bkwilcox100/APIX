USE middle_layer;

CREATE TABLE heb_pubsub_settings (
	last_update_check TIMESTAMP NOT NULL
);
INSERT INTO heb_pubsub_settings (last_update_check) values ('2000-01-01 00:00:01');
