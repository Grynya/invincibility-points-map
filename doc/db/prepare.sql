CREATE USER 'invPointMap'@'%' IDENTIFIED BY 'invPointMap';

GRANT ALL PRIVILEGES ON *.* TO 'invPointMap'@'%' WITH GRANT OPTION;

FLUSH PRIVILEGES;

create database invPointMap;

use invPointMap;

INSERT INTO resource (description, name)
VALUES ("Медично-санітарний заклад, що виготовляє і відпускає ліки за рецептами, продає готові лікарські засоби, які дозволено відпускати без рецепта.",
        "аптека"),
       ("Захисна споруда, об'єкт цивільної оборони, служить для захисту людей від авіабомб і артилерійських снарядів, уламків зруйнованих будівель і згубної дії отруйних газів.",
        "бомбосховище"),
       ("Спеціалізована медично-санітарна служба, що надає першу медичну допомогу на місці", "швидка медична допомога"),
       ("Магазин, що пропонує у продаж бакалійні та домогосподарські товари.", "продовольчий магазин"),
       ("Місце з енергонезалежним інтернетом", "інтернет"),
       ("Пункт обігріву в період холоду", "тепло"),
       ("Енергонезалежний мобільний зв'язок", "мобільний зв'язок"),
       ("Пункт охолодження в період спеки", "охолодження"),
       ("Пункт безкоштовного харчування", "харчування");

INSERT INTO map_point (coordinates, description, hours_of_work, lat, name, phone, user_owner)
VALUES (ST_GeomFromText('POINT(29.678028 52.358965)'), 'Пункт незламності', '08.00-20.00', 52.358965, 'Кафе',
        '5555-0000', 1),
       (ST_GeomFromText('POINT(30.421431380596886 48.590126340484375)'), 'Пункт незламності', '08.00-20.00', 51.507222,
        'Кафе', '5555-0000', 1),
       (ST_GeomFromText('POINT(-3.703790 40.416775)'), 'Пункт незламності', '08.00-20.00', 40.416775, 'Кафе',
        '5555-0000', 1),
       (ST_GeomFromText('POINT(12.496366 41.902782)'), 'Пункт незламності', '08.00-20.00', 41.902782, 'Кафе',
        '5555-0000', 1),
       (ST_GeomFromText('POINT(13.404954 52.520008)'), 'Пункт незламності', '08.00-20.00', 52.520008, 'Кафе',
        '5555-0000', 1),
       (ST_GeomFromText('POINT(2.352222 48.856613)'), 'Пункт незламності', '08.00-20.00', 48.856613, 'Кафе',
        '5555-0000', 1),
       (ST_GeomFromText('POINT(37.617298 55.755825)'), 'Пункт незламності', '08.00-20.00', 55.755825, 'Кафе',
        '5555-0000', 1),
       (ST_GeomFromText('POINT(139.691711 35.689487)'), 'Пункт незламності', '08.00-20.00', 35.689487, 'Кафе',
        '5555-0000', 1),
       (ST_GeomFromText('POINT(-122.419416 37.774929)'), 'Пункт незламності', '08.00-20.00', 37.774929, 'Кафе',
        '5555-0000', 1),
       (ST_GeomFromText('POINT(114.109497 22.396428)'), 'Пункт незламності', '08.00-20.00', 22.396428, 'Кафе',
        '5555-0000', 1);

-- Insert 30 users
-- Insert 30 users
INSERT INTO user (code, email, name, password, surname, user_status)
VALUES (NULL, 'email1@example.com', 'Asa', '123', 'Jacobson', 'ACTIVE'),
       (NULL, 'email2@example.com', 'Elias', '123', 'Ortega', 'ACTIVE'),
       (NULL, 'email3@example.com', 'Flora', '123', 'Dean', 'ACTIVE'),
       (NULL, 'email4@example.com', 'Ela', '123', 'Osborn', 'ACTIVE'),
       (NULL, 'email5@example.com', 'Husna', '123', 'Wade', 'ACTIVE'),
       (NULL, 'email6@example.com', 'Lee', '123', 'Simon', 'ACTIVE'),
       (NULL, 'email7@example.com', 'Kiara', '123', 'Harrell', 'ACTIVE'),
       (NULL, 'email8@example.com', 'Priya', '123', 'Byrd', 'ACTIVE'),
       (NULL, 'email9@example.com', 'Nicholas', '123', 'Gaines', 'ACTIVE'),
       (NULL, 'email10@example.com', 'Tyler', '123', 'Powers', 'ACTIVE'),
       (NULL, 'email11@example.com', 'Virgil', '123', 'Lowery', 'ACTIVE'),
       (NULL, 'email12@example.com', 'Izaak', '123', 'Jacobs', 'ACTIVE'),
       (NULL, 'email13@example.com', 'Maximillian', '123', 'Oconnor', 'ACTIVE'),
       (NULL, 'email14@example.com', 'Scott', '123', 'Knapp', 'ACTIVE'),
       (NULL, 'email15@example.com', 'Lewys', '123', 'Jensen', 'ACTIVE'),
       (NULL, 'email16@example.com', 'Jake', '123', 'Haas', 'ACTIVE'),
       (NULL, 'email17@example.com', 'Jeffrey', '123', 'Kramer', 'ACTIVE'),
       (NULL, 'email18@example.com', 'Laura', '123', 'Park', 'ACTIVE'),
       (NULL, 'email19@example.com', 'Mikolaj', '123', 'Erickson', 'ACTIVE'),
       (NULL, 'email20@example.com', 'Ines', '123', 'Curtis', 'ACTIVE'),
       (NULL, 'email21@example.com', 'Will', '123', 'Beasley', 'ACTIVE'),
       (NULL, 'email22@example.com', 'Ellie-May', '123', 'Sampson', 'ACTIVE'),
       (NULL, 'email23@example.com', 'Amelie', '123', 'O\'Brien', 'ACTIVE'),
       (NULL, 'email24@example.com', 'Sahil', '123', 'Martinez', 'ACTIVE'),
       (NULL, 'email25@example.com', 'Sally', '123', 'Guerra', 'ACTIVE'),
       (NULL, 'email26@example.com', 'Lottie', '123', 'Riley', 'ACTIVE'),
       (NULL, 'email27@example.com', 'Marley', '123', 'Johnson', 'ACTIVE'),
       (NULL, 'email28@example.com', 'Kelvin', '123', 'Conner', 'ACTIVE'),
       (NULL, 'email29@example.com', 'Ayah', '123', 'Fuller', 'ACTIVE'),
       (NULL, 'email30@example.com', 'Sylvia', 123, 'Sawyer', 'ACTIVE');

-- encrypt password
UPDATE user
SET password = '$2a$10$5z2kS6w60ZyJJbHjxK48xOaK8SzUv7AMjFFJWY1O0GzXeTCv8dEqC'
WHERE password = '123';

INSERT INTO role (name)
VALUES ('ROLE_ADMIN'),
       ('ROLE_USER');

-- Get the IDs of the inserted users
SET @start_id = (SELECT MAX(id) - 29
                 FROM user);
SET @end_id = (SELECT MAX(id)
               FROM user);

-- Insert user roles for the inserted users
INSERT INTO user_role (user_id, role_id)
SELECT id, (SELECT id FROM role WHERE name = 'ROLE_USER')
FROM user
WHERE id BETWEEN @start_id AND @end_id;



INSERT INTO map_point (coordinates, description, hours_of_work, name, phone, user_owner)
VALUES (ST_GeomFromText('POINT(49.96585677169776 33.61018554839228)'), 'Пункт незламності', '08.00-20.00', 'Кафе',
        '5555-0000', 1);

-- insert 20 dislikes
SET @start_id = (SELECT MAX(id) - 19
                 FROM user);
SET @end_id = (SELECT MAX(id)
               FROM user);

INSERT INTO rated_point (rating, user_id, point_id)
SELECT 'DISLIKED', id, 1
FROM user
WHERE id BETWEEN ((SELECT MAX(id) - 19 FROM user)) AND ((SELECT MAX(id) FROM user));
