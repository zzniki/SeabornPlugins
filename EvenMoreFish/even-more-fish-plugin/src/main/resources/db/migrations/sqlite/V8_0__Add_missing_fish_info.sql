-- Enable foreign key support in SQLite
PRAGMA foreign_keys = ON;

-- Add start_time and end_time to competitions table (keeping as TIMESTAMP)
ALTER TABLE `${table.prefix}competitions` ADD COLUMN start_time TIMESTAMP;
ALTER TABLE `${table.prefix}competitions` ADD COLUMN end_time TIMESTAMP;

-- Recreate fish table with new columns while keeping TIMESTAMP
CREATE TABLE `${table.prefix}fish_new` (
   fish_name VARCHAR(256) NOT NULL,
   fish_rarity VARCHAR(256) NOT NULL,
   first_fisher VARCHAR(36) NOT NULL,
   total_caught INTEGER NOT NULL,
   largest_fish REAL NOT NULL,
   largest_fisher VARCHAR(36) NOT NULL,
   shortest_length REAL NOT NULL DEFAULT 0.0,
   shortest_fisher VARCHAR(36) NOT NULL DEFAULT '',
   first_catch_time TIMESTAMP NOT NULL,
   discoverer VARCHAR(128),
   PRIMARY KEY (fish_name, fish_rarity)
);

-- Copy data from old table to new table
INSERT INTO `${table.prefix}fish_new`
(fish_name, fish_rarity, first_fisher, total_caught, largest_fish, largest_fisher, first_catch_time)
SELECT
fish_name, fish_rarity, first_fisher, total_caught, largest_fish, largest_fisher, first_catch_time
FROM `${table.prefix}fish`;

-- Drop old table and rename new one
DROP TABLE `${table.prefix}fish`;
ALTER TABLE `${table.prefix}fish_new` RENAME TO `${table.prefix}fish`;

-- Add new columns to users table
ALTER TABLE `${table.prefix}users` ADD COLUMN shortest_fish VARCHAR(256) NOT NULL DEFAULT '';
ALTER TABLE `${table.prefix}users` ADD COLUMN shortest_length REAL NOT NULL DEFAULT 0.0;

-- Create new user_fish_stats table with TIMESTAMP
CREATE TABLE IF NOT EXISTS `${table.prefix}user_fish_stats` (
    fish_name VARCHAR(256) NOT NULL,
    fish_rarity VARCHAR(256) NOT NULL,
    user_id INTEGER NOT NULL,
    first_catch_time TIMESTAMP NOT NULL,
    shortest_length REAL,
    longest_length REAL,
    quantity INTEGER,
    PRIMARY KEY (fish_name, fish_rarity, user_id),
    FOREIGN KEY (user_id) REFERENCES `${table.prefix}users`(id)
);

-- Recreate fish_log table with TIMESTAMP
CREATE TABLE `${table.prefix}fish_log_new` (
   id INTEGER PRIMARY KEY AUTOINCREMENT,
   user_id INTEGER NOT NULL,
   fish_rarity VARCHAR(256) NOT NULL,
   fish_name VARCHAR(256) NOT NULL,
   fish_length REAL NOT NULL DEFAULT 0.0,
   catch_time TIMESTAMP NOT NULL,
   competition_id VARCHAR(256),
   FOREIGN KEY (user_id) REFERENCES `${table.prefix}users`(id)
);

-- Copy data from old table to new table
INSERT INTO `${table.prefix}fish_log_new`
(id, user_id, fish_rarity, fish_name, catch_time)
SELECT
id, user_id, rarity, fish, first_catch_time
FROM `${table.prefix}fish_log`;

-- Drop old table and rename new one
DROP TABLE `${table.prefix}fish_log`;
ALTER TABLE `${table.prefix}fish_log_new` RENAME TO `${table.prefix}fish_log`;

-- No changes needed for transactions table (keeps TIMESTAMP)
-- No changes needed for users_sales table (no timestamp columns)