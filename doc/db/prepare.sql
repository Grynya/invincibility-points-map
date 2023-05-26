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

UPDATE user
SET password = '$2a$10$fC2SAgWTk/iLGHH3sG7VReuOhbzoIv/LqGJQ.p098uLlvARSVRpNi'
where id > 0; -- set passwords for all '123'

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


INSERT INTO user (code, email, name, password, surname, user_status)
VALUES (NULL, 'email1ToDelete@example.com', 'Asa', '123', 'Jacobson', 'ACTIVE'),
       (NULL, 'email2ToDelete@example.com', 'Elias', '123', 'Ortega', 'ACTIVE');

INSERT INTO map_point (coordinates, description, hours_of_work, name, phone, user_owner)
VALUES (ST_GeomFromText('POINT(49.965861 33.610194)'), 'Пункт toDelete', '08.00-20.00', 'Кафе',
        '5555-0000', 1),
       (ST_GeomFromText('POINT(49.973993 33.602435)'), 'Пункт toDelete2', '08.00-20.00', 'Кафе',
        '5555-0000', 2);

INSERT INTO rated_point (rating, user_id, point_id)
values ('LIKED', 33, 3),
       ('LIKED', 33, 4),
       ('LIKED', 34, 3),
       ('LIKED', 34, 4);

-- insert 20 dislikes
SET @start_id = (SELECT MAX(id) - 19
                 FROM user);
SET @end_id = (SELECT MAX(id)
               FROM user);

INSERT INTO rated_point (rating, user_id, point_id)
SELECT 'DISLIKED', id, 1
FROM user
WHERE id BETWEEN ((SELECT MAX(id) - 19 FROM user)) AND ((SELECT MAX(id) FROM user));

-- inserting points
INSERT INTO map_point (coordinates, description, hours_of_work, is_deleted, name, phone, user_owner)
VALUES
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'Ліки по знижці', '09.00-19.00', false, 'Аптека копійка', '1234567890', 1),
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'Є їжа та продовольчі продукти', '05.00-16.00', false, 'Магазин', '4534434534', 2),
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'Пропонуємо продуктові набори', '07.00-18.00', false, 'Сільпо', '45664642', 3),
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'Пропонуємо безкоштовні ліки', '07.00-13.00', false, 'Медсервіс', '2325143533', 4),
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'В наявності безкоштовні обіди та інтернет', '09.00-19.00', false, 'Міська їдальня', '1234423374', 5),
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'Є їжа та продовольчі продукти', '10.00-13.00', false, 'Магазин', '323343414', 6),
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'Працюємо від генераторів', '13.00-19.00', false, 'Кафе Їжачок', '334134432', 7),
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'Гарний настрій гарантовано', '12.00-16.00', false, 'Ворк-плейс', '090323123', 8),
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'В наявності б/у одяг', '12.00-19.00', false, 'Магазин Сток', '222-333', 9),
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'Є їжа та продовольчі продукти', '11.00-18.00', false, 'Їдальня', '4353531', 10),
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'Надаємо безкоштовний інтернет', '11.00-19.00', false, 'Коворкінг Free community', '099432354', 12),
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'Надаємо інтернет по знижці', '09.00-16.00', false, 'Кафе для роботи', '5453454353', 13),
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'Тепло та затишок гарантовані', '11.00-21.00', false, 'Соціальна їдальня', '43546564', 14),
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'Приємні ціни та швидке обслуговування', '10.00-21.00', false, 'Їдальня New life', '654534432', 15),
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'Маємо 5 генераторів', '06.00-22.00', false, 'Ворк-плейс', '436562434', 16),
    (POINT(49.965861 + (RAND() * 0.06 - 0.03), 33.610194 + (RAND() * 0.06 - 0.03)), 'Безкоштовний інтернет та електроенергія', '10.00-21.00', false, 'Ворк-плейс', '345545634', 17);
