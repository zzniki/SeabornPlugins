-- Add start_time and end_time to competitions table (keeping as TIMESTAMP)
ALTER TABLE `${table.prefix}competitions`
ADD COLUMN start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN end_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Update fish table with new columns and primary key (keeping TIMESTAMP)
ALTER TABLE `${table.prefix}fish`
ADD COLUMN shortest_length REAL NOT NULL DEFAULT 0.0,
ADD COLUMN shortest_fisher VARCHAR(36) NOT NULL DEFAULT '',
ADD COLUMN discoverer VARCHAR(128),
MODIFY COLUMN fish_name VARCHAR(256) NOT NULL,
MODIFY COLUMN fish_rarity VARCHAR(256) NOT NULL;

-- Modify primary key (requires dropping and recreating in MySQL)
ALTER TABLE `${table.prefix}fish` DROP PRIMARY KEY;
ALTER TABLE `${table.prefix}fish` ADD PRIMARY KEY (fish_name, fish_rarity);

-- Update users table with new columns
ALTER TABLE `${table.prefix}users`
ADD COLUMN shortest_fish VARCHAR(256) NOT NULL DEFAULT '',
ADD COLUMN shortest_length REAL NOT NULL DEFAULT 0.0,
ADD UNIQUE (uuid);

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
    CONSTRAINT FK_UserFishStats_User
    FOREIGN KEY (user_id) REFERENCES `${table.prefix}users`(id)
) ENGINE=InnoDB;

-- Update fish_log table (keeping TIMESTAMP)
ALTER TABLE `${table.prefix}fish_log`
RENAME COLUMN fish TO fish_name,
MODIFY COLUMN fish_name VARCHAR(256) NOT NULL,
MODIFY COLUMN fish_rarity VARCHAR(256) NOT NULL,
ADD COLUMN fish_length REAL NOT NULL DEFAULT 0.0,
RENAME COLUMN first_catch_time TO catch_time,
ADD COLUMN competition_id VARCHAR(256),
DROP COLUMN quantity,
DROP COLUMN largest_length;

-- No changes needed for transactions table (keeps TIMESTAMP)
-- No changes needed for users_sales table (no timestamp columns)