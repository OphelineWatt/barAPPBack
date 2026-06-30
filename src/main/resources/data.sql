-- Database initialization script, executed automatically on every application
-- startup (after Hibernate creates/updates the schema) against H2 (dev) and
-- PostgreSQL (docker-compose). Statements are idempotent so re-running them
-- on an already-seeded database is a no-op.

INSERT INTO sizes (code, label)
SELECT 'S', 'Small'
WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE code = 'S');

INSERT INTO sizes (code, label)
SELECT 'M', 'Medium'
WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE code = 'M');

INSERT INTO sizes (code, label)
SELECT 'L', 'Large'
WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE code = 'L');

-- Demo barmaker account for the deployed/demo environment.
-- email: barmaker@barapp.fr / password: barmaker123 (BCrypt hash below)
INSERT INTO users (email, name, role, password_hash, created_at)
SELECT 'barmaker@barapp.fr', 'Demo Barmaker', 'BARMAKER',
       '$2a$10$CFC3ieQ1iOzkpXOHstKw9eV4/wSV/f0RBvqpZJEVr9MraVZhxft4W', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'barmaker@barapp.fr');
