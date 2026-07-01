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

-- Demo barmaker account — email: barmaker@barapp.fr / password: barmaker123
INSERT INTO users (email, name, role, password_hash, created_at)
SELECT 'barmaker@barapp.fr', 'Demo Barmaker', 'BARMAKER',
       '$2a$10$CFC3ieQ1iOzkpXOHstKw9eV4/wSV/f0RBvqpZJEVr9MraVZhxft4W', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'barmaker@barapp.fr');

-- Demo client account — email: client@barapp.fr / password: client123
INSERT INTO users (email, name, role, password_hash, created_at)
SELECT 'client@barapp.fr', 'Demo Client', 'CLIENT',
       '$2a$10$wKBk2.oXl5XZsKHlAERGsO4tn/8/yqZfWuJDpP0atWUXcm4wOIJYy', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'client@barapp.fr');

-- Categories
INSERT INTO categories (name, description)
SELECT 'Classiques', 'Les grands classiques de la mixologie'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Classiques');

INSERT INTO categories (name, description)
SELECT 'Spirits', 'Cocktails forts pour les amateurs'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Spirits');

INSERT INTO categories (name, description)
SELECT 'Tropicaux', 'Saveurs exotiques et fraîcheur garantie'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Tropicaux');

INSERT INTO categories (name, description)
SELECT 'Mocktails', 'Sans alcool, 100% saveur'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Mocktails');

-- Cocktails (reference categories by sub-select to stay portable across H2 and PostgreSQL)
INSERT INTO cocktails (name, description, category_id, image_url, active, created_at)
SELECT 'Mojito',
       'Fraîcheur tropicale, menthe fraîche et rhum blanc.',
       (SELECT id FROM categories WHERE name = 'Classiques'),
       'https://www.thecocktaildb.com/images/media/drink/metwgh1606770327.jpg',
       true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM cocktails WHERE name = 'Mojito');

INSERT INTO cocktails (name, description, category_id, image_url, active, created_at)
SELECT 'Negroni',
       'L''équilibre parfait entre l''amer, le doux et le fort.',
       (SELECT id FROM categories WHERE name = 'Classiques'),
       'https://www.thecocktaildb.com/images/media/drink/qgdu971561574065.jpg',
       true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM cocktails WHERE name = 'Negroni');

INSERT INTO cocktails (name, description, category_id, image_url, active, created_at)
SELECT 'Whisky Sour',
       'Acidité et caractère pour les esprits libres.',
       (SELECT id FROM categories WHERE name = 'Spirits'),
       'https://www.thecocktaildb.com/images/media/drink/hbkfsh1589574990.jpg',
       true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM cocktails WHERE name = 'Whisky Sour');

INSERT INTO cocktails (name, description, category_id, image_url, active, created_at)
SELECT 'Piña Colada',
       'L''île dans un verre. Note de coco, ananas, soleil.',
       (SELECT id FROM categories WHERE name = 'Tropicaux'),
       'https://www.thecocktaildb.com/images/media/drink/cpf4j51504371346.jpg',
       true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM cocktails WHERE name = 'Piña Colada');

INSERT INTO cocktails (name, description, category_id, image_url, active, created_at)
SELECT 'Virgin Storm',
       'Tempête de fruits rouges. Zéro alcool, cent pour cent saveur.',
       (SELECT id FROM categories WHERE name = 'Mocktails'),
       'https://www.thecocktaildb.com/images/media/drink/xwqvur1468876473.jpg',
       true, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM cocktails WHERE name = 'Virgin Storm');

-- Prices per size for each cocktail (S/M/L)
INSERT INTO cocktail_price (cocktail_id, size_id, price)
SELECT c.id, s.id,
  CASE s.code WHEN 'S' THEN 9.0 WHEN 'M' THEN 12.0 ELSE 15.0 END
FROM cocktails c, sizes s
WHERE c.name = 'Mojito'
  AND NOT EXISTS (SELECT 1 FROM cocktail_price cp WHERE cp.cocktail_id = c.id AND cp.size_id = s.id);

INSERT INTO cocktail_price (cocktail_id, size_id, price)
SELECT c.id, s.id,
  CASE s.code WHEN 'S' THEN 10.0 WHEN 'M' THEN 13.0 ELSE 16.0 END
FROM cocktails c, sizes s
WHERE c.name = 'Negroni'
  AND NOT EXISTS (SELECT 1 FROM cocktail_price cp WHERE cp.cocktail_id = c.id AND cp.size_id = s.id);

INSERT INTO cocktail_price (cocktail_id, size_id, price)
SELECT c.id, s.id,
  CASE s.code WHEN 'S' THEN 10.0 WHEN 'M' THEN 13.0 ELSE 16.0 END
FROM cocktails c, sizes s
WHERE c.name = 'Whisky Sour'
  AND NOT EXISTS (SELECT 1 FROM cocktail_price cp WHERE cp.cocktail_id = c.id AND cp.size_id = s.id);

INSERT INTO cocktail_price (cocktail_id, size_id, price)
SELECT c.id, s.id,
  CASE s.code WHEN 'S' THEN 9.0 WHEN 'M' THEN 12.0 ELSE 15.0 END
FROM cocktails c, sizes s
WHERE c.name = 'Piña Colada'
  AND NOT EXISTS (SELECT 1 FROM cocktail_price cp WHERE cp.cocktail_id = c.id AND cp.size_id = s.id);

INSERT INTO cocktail_price (cocktail_id, size_id, price)
SELECT c.id, s.id,
  CASE s.code WHEN 'S' THEN 7.0 WHEN 'M' THEN 9.0 ELSE 11.0 END
FROM cocktails c, sizes s
WHERE c.name = 'Virgin Storm'
  AND NOT EXISTS (SELECT 1 FROM cocktail_price cp WHERE cp.cocktail_id = c.id AND cp.size_id = s.id);
