USE middle_layer;

DROP TABLE IF EXISTS heb_id_generator;

CREATE TABLE heb_id_generator (
	data_type VARCHAR(40) NOT NULL,
	next_id BIGINT,
	PRIMARY KEY (data_type)
);
