CREATE SCHEMA IF NOT EXISTS naps;

CREATE TABLE IF NOT EXISTS naps.pollutants
(
   id SERIAL PRIMARY KEY,
   name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS naps.methods
(
   id SERIAL PRIMARY KEY,
   dataset VARCHAR(15) NOT NULL,
   report_type VARCHAR(15) NOT NULL,
   method VARCHAR(20) NULL,
   units VARCHAR(10) NOT NULL,
   UNIQUE NULLS NOT DISTINCT (dataset, report_type, method, units)
);

CREATE TABLE IF NOT EXISTS naps.sites
(
   id            SERIAL PRIMARY KEY,
   NAPS_id       int NOT NULL UNIQUE,
   station_name  VARCHAR(255) NULL,
   city_name     VARCHAR(255) NOT NULL,
   prov_terr     VARCHAR(50) NOT NULL,
   latitude      NUMERIC(16,12) NOT NULL,
   longitude     NUMERIC(16,12) NOT NULL,
   site_type     VARCHAR(2) NULL,
   urbanization  VARCHAR(2) NULL,
   neighbourhood VARCHAR(2) NULL,
   land_use      VARCHAR(1) NULL,
   scale         VARCHAR(2) NULL,
   elevation     int NULL
);
CREATE INDEX IF NOT EXISTS idx_sites_prov_terr ON naps.sites (prov_terr ASC);
CREATE INDEX IF NOT EXISTS idx_sites_latitude ON naps.sites (latitude ASC);
CREATE INDEX IF NOT EXISTS idx_sites_longitude ON naps.sites (longitude ASC);
CREATE INDEX IF NOT EXISTS idx_sites_site_type ON naps.sites (site_type ASC);
CREATE INDEX IF NOT EXISTS idx_sites_urbanization ON naps.sites (urbanization ASC);
CREATE INDEX IF NOT EXISTS idx_sites_neighbourhood ON naps.sites (neighbourhood ASC);
CREATE INDEX IF NOT EXISTS idx_sites_land_use ON naps.sites (land_use ASC);
CREATE INDEX IF NOT EXISTS idx_sites_scale ON naps.sites (scale ASC);
CREATE INDEX IF NOT EXISTS idx_sites_elevation ON naps.sites (elevation ASC);

CREATE TABLE IF NOT EXISTS naps.continuous_data
(
   site_id int NOT NULL,
   pollutant_id int NOT NULL,
   method_id int NOT NULL,
   date_time timestamp NOT NULL,
   year smallint NOT NULL,
   month smallint NOT NULL,
   day smallint NOT NULL,
   hour smallint NOT NULL,
   day_of_week smallint NOT NULL,
   data NUMERIC(8,3) NOT NULL,
   PRIMARY KEY (site_id, pollutant_id, method_id, date_time),
   CONSTRAINT fk_continuous_data_site_id
			    FOREIGN KEY (site_id)
			    REFERENCES naps.sites (id)
			    ON DELETE CASCADE,
   CONSTRAINT fk_continuous_data_pollutant_id
			    FOREIGN KEY (pollutant_id)
			    REFERENCES naps.pollutants (id)
			    ON DELETE CASCADE,
   CONSTRAINT fk_continuous_data_method_id
			    FOREIGN KEY (method_id)
			    REFERENCES naps.methods (id)
			    ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_continuous_data_site_id ON naps.continuous_data (site_id ASC);
CREATE INDEX IF NOT EXISTS idx_continuous_data_method_id ON naps.continuous_data (method_id ASC);
CREATE INDEX IF NOT EXISTS idx_continuous_data_pollutant_id ON naps.continuous_data (pollutant_id ASC);
CREATE INDEX IF NOT EXISTS idx_continuous_data_date_time ON naps.continuous_data (date_time ASC);
CREATE INDEX IF NOT EXISTS idx_continuous_data_year ON naps.continuous_data (year ASC);
CREATE INDEX IF NOT EXISTS idx_continuous_data_month ON naps.continuous_data (month ASC);
CREATE INDEX IF NOT EXISTS idx_continuous_data_hour ON naps.continuous_data (hour ASC);
CREATE INDEX IF NOT EXISTS idx_continuous_data_day_of_week ON naps.continuous_data (day_of_week ASC);

CREATE TABLE IF NOT EXISTS naps.samples
(
   id SERIAL PRIMARY KEY,
   naps_sample_id VARCHAR(15) NULL,
   canister_id VARCHAR(15) NULL,
   fine boolean NULL,
   cartridge VARCHAR(10) NULL,
   media VARCHAR(10) NULL,
   type VARCHAR(15) NULL,
   sample_mass NUMERIC(12,6) NULL,
   spec_mass NUMERIC(12,6) NULL,
   dichot_mass NUMERIC(12,6) NULL,
   sample_vol NUMERIC(12,6) NULL,
   sample_duration double precision NULL,
   tsp NUMERIC(12,6) NULL
);
CREATE INDEX IF NOT EXISTS idx_samples_sample_id ON naps.samples (naps_sample_id ASC);
CREATE INDEX IF NOT EXISTS idx_samples_canister_id ON naps.samples (canister_id ASC);
CREATE INDEX IF NOT EXISTS idx_samples_fine ON naps.samples (fine ASC);
CREATE INDEX IF NOT EXISTS idx_samples_cartridge ON naps.samples (cartridge ASC);
CREATE INDEX IF NOT EXISTS idx_samples_media ON naps.samples (media ASC);
CREATE INDEX IF NOT EXISTS idx_samples_type ON naps.samples (type ASC);

CREATE TABLE IF NOT EXISTS naps.integrated_data
(
   site_id int NOT NULL,
   pollutant_id int NOT NULL,
   method_id int NOT NULL,
   sample_id int NOT NULL,
   date_time timestamp NOT NULL,
   year smallint NOT NULL,
   month smallint NOT NULL,
   day smallint NOT NULL,
   day_of_week smallint NOT NULL,
   data NUMERIC(8,3) NOT NULL,
   PRIMARY KEY (site_id, pollutant_id, method_id, sample_id, date_time),
   CONSTRAINT fk_integrated_data_site_id
			    FOREIGN KEY (site_id)
			    REFERENCES naps.sites (id)
			    ON DELETE CASCADE,
   CONSTRAINT fk_integrated_data_pollutant_id
			    FOREIGN KEY (pollutant_id)
			    REFERENCES naps.pollutants (id)
			    ON DELETE CASCADE,
   CONSTRAINT fk_integrated_data_method_id
			    FOREIGN KEY (method_id)
			    REFERENCES naps.methods (id)
			    ON DELETE CASCADE,
   CONSTRAINT fk_integrated_data_sample_id
			    FOREIGN KEY (sample_id)
			    REFERENCES naps.samples (id)
			    ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_integrated_data_site_id ON naps.integrated_data (site_id ASC);
CREATE INDEX IF NOT EXISTS idx_integrated_data_method_id ON naps.integrated_data (method_id ASC);
CREATE INDEX IF NOT EXISTS idx_integrated_data_pollutant_id ON naps.integrated_data (pollutant_id ASC);
CREATE INDEX IF NOT EXISTS idx_integrated_data_sample_id ON naps.integrated_data (sample_id ASC);
CREATE INDEX IF NOT EXISTS idx_integrated_data_date_time ON naps.integrated_data (date_time ASC);
CREATE INDEX IF NOT EXISTS idx_integrated_data_year ON naps.integrated_data (year ASC);
CREATE INDEX IF NOT EXISTS idx_integrated_data_month ON naps.integrated_data (month ASC);
CREATE INDEX IF NOT EXISTS idx_integrated_data_day_of_week ON naps.integrated_data (day_of_week ASC);

