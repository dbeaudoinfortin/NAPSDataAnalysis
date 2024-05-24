CREATE SCHEMA IF NOT EXISTS naps;

CREATE TABLE IF NOT EXISTS naps.pollutants
(
   id SERIAL NOT NULL,
   name VARCHAR(255) NOT NULL,
   PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_pollutants_name ON naps.pollutants (name ASC);

-- Insert the basic pollutants
INSERT into naps.pollutants (name) values ('CO'), ('NO'), ('NO2'), ('NOX'), ('O3'), ('PM10'), ('PM25'), ('SO2') ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS naps.sites
(
   id            SERIAL NOT NULL,
   NAPS_id       int not null,
   station_name  VARCHAR(255) null,
   city_name     VARCHAR(255) NOT NULL,
   prov_terr     VARCHAR(50) NOT NULL,
   latitude      NUMERIC(16,12) NOT NULL,
   longitude     NUMERIC(16,12) NOT NULL,
   site_type     VARCHAR(2) null,
   urbanization  VARCHAR(2) null,
   neighbourhood VARCHAR(2) null,
   land_use      VARCHAR(1) null,
   scale         VARCHAR(2) null,
   elevation     int null,
   PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_site_NAPS_id ON naps.sites (NAPS_id ASC);

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
   pollutant_id int not null,
   date_time timestamp NOT NULL,
   year smallint not null,
   month smallint not null,
   day smallint not null,
   hour smallint not null,
   day_of_week smallint not null,
   data NUMERIC(6,2) NOT NULL,
   PRIMARY KEY (site_id, pollutant_id, date_time),
   CONSTRAINT fk_continuous_data_site_id
			    FOREIGN KEY (site_id)
			    REFERENCES naps.sites (id)
			    ON DELETE CASCADE,
   CONSTRAINT fk_continuous_data_pollutant_id
			    FOREIGN KEY (pollutant_id)
			    REFERENCES naps.pollutants (id)
			    ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_continuous_data_pollutant_id ON naps.continuous_data (pollutant_id ASC);
CREATE INDEX IF NOT EXISTS idx_continuous_data_date_time ON naps.continuous_data (date_time ASC);
CREATE INDEX IF NOT EXISTS idx_continuous_data_year ON naps.continuous_data (year ASC);
CREATE INDEX IF NOT EXISTS idx_continuous_data_month ON naps.continuous_data (month ASC);
CREATE INDEX IF NOT EXISTS idx_continuous_data_hour ON naps.continuous_data (hour ASC);
CREATE INDEX IF NOT EXISTS idx_continuous_data_day_of_week ON naps.continuous_data (day_of_week ASC);

CREATE TABLE IF NOT EXISTS naps.integrated_data
(
   site_id int NOT NULL,
   pollutant_id int not null,
   date_time timestamp NOT NULL,
   year smallint not null,
   month smallint not null,
   day smallint not null,
   day_of_week smallint not null,
   fine boolean not null,
   data NUMERIC(6,2) NOT NULL,
   PRIMARY KEY (site_id, pollutant_id, date_time, fine),
   CONSTRAINT fk_integrated_data_site_id
			    FOREIGN KEY (site_id)
			    REFERENCES naps.sites (id)
			    ON DELETE CASCADE,
   CONSTRAINT fk_integrated_data_pollutant_id
			    FOREIGN KEY (pollutant_id)
			    REFERENCES naps.pollutants (id)
			    ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_integrated_data_pollutant_id ON naps.integrated_data (pollutant_id ASC);
CREATE INDEX IF NOT EXISTS idx_integrated_data_date_time ON naps.integrated_data (date_time ASC);
CREATE INDEX IF NOT EXISTS idx_integrated_data_year ON naps.integrated_data (year ASC);
CREATE INDEX IF NOT EXISTS idx_integrated_data_month ON naps.integrated_data (month ASC);
CREATE INDEX IF NOT EXISTS idx_integrated_data_day_of_week ON naps.integrated_data (day_of_week ASC);
CREATE INDEX IF NOT EXISTS idx_integrated_data_fine ON naps.integrated_data (fine ASC);
