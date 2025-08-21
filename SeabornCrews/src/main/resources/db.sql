CREATE TABLE IF NOT EXISTS crews (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(64) UNIQUE NOT NULL,
  owner_uuid VARCHAR(36) NOT NULL,
  reputation INT NOT NULL DEFAULT 0,
  description VARCHAR(256),
  tag VARCHAR(8),
  island_world VARCHAR(128) DEFAULT NULL,
  battle_wins INT NOT NULL DEFAULT 0,
  is_public BOOLEAN DEFAULT FALSE,
  max_members INT DEFAULT 15,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (owner_uuid) REFERENCES players(uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS crew_members (
  crew_id INT NOT NULL,
  member_uuid VARCHAR(36) NOT NULL,
  role ENUM('member', 'quartermaster', 'captain'),
  PRIMARY KEY (crew_id, member_uuid),
  FOREIGN KEY (crew_id) REFERENCES crews(id) ON DELETE CASCADE,
  FOREIGN KEY (member_uuid) REFERENCES players(uuid) ON DELETE CASCADE
);
