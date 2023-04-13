CREATE USER 'invPointMap'@'%' IDENTIFIED BY 'invPointMap';

GRANT ALL PRIVILEGES ON *.* TO 'invPointMap'@'%' WITH GRANT OPTION;

FLUSH PRIVILEGES;

create database invPointMap;

use invPointMap;

INSERT INTO resource (description, name)
VALUES
    ("Медично-санітарний заклад, що виготовляє і відпускає ліки за рецептами, продає готові лікарські засоби, які дозволено відпускати без рецепта.", "аптека"),
    ("Захисна споруда, об'єкт цивільної оборони, служить для захисту людей від авіабомб і артилерійських снарядів, уламків зруйнованих будівель і згубної дії отруйних газів.", "бомбосховище"),
    ("Спеціалізована медично-санітарна служба, що надає першу медичну допомогу на місці", "швидка медична допомога"),
    ("Магазин, що пропонує у продаж бакалійні та домогосподарські товари.", "продовольчий магазин"),
    ("Місце з енергонезалежним інтернетом", "інтернет"),
    ("Пункт обігріву в період холоду", "тепло"),
    ("Енергонезалежний мобільний зв'язок", "мобільний зв'язок"),
    ("Пункт охолодження в період спеки", "охолодження"),
    ("Пункт безкоштовного харчування", "харчування");

INSERT INTO map_point (coordinates, description, hours_of_work, lat, name, phone, user_owner)
VALUES
    (ST_GeomFromText('POINT(29.678028 52.358965)'), 'Пункт незламності', '08.00-20.00', 52.358965, 'Кафе', '5555-0000', 1),
    (ST_GeomFromText('POINT(30.421431380596886 48.590126340484375)'), 'Пункт незламності', '08.00-20.00', 51.507222, 'Кафе', '5555-0000', 1),
    (ST_GeomFromText('POINT(-3.703790 40.416775)'), 'Пункт незламності', '08.00-20.00', 40.416775, 'Кафе', '5555-0000', 1),
    (ST_GeomFromText('POINT(12.496366 41.902782)'), 'Пункт незламності', '08.00-20.00', 41.902782, 'Кафе', '5555-0000', 1),
    (ST_GeomFromText('POINT(13.404954 52.520008)'), 'Пункт незламності', '08.00-20.00', 52.520008, 'Кафе', '5555-0000', 1),
    (ST_GeomFromText('POINT(2.352222 48.856613)'), 'Пункт незламності', '08.00-20.00', 48.856613, 'Кафе', '5555-0000', 1),
    (ST_GeomFromText('POINT(37.617298 55.755825)'), 'Пункт незламності', '08.00-20.00', 55.755825, 'Кафе', '5555-0000', 1),
    (ST_GeomFromText('POINT(139.691711 35.689487)'), 'Пункт незламності', '08.00-20.00', 35.689487, 'Кафе', '5555-0000', 1),
    (ST_GeomFromText('POINT(-122.419416 37.774929)'), 'Пункт незламності', '08.00-20.00', 37.774929, 'Кафе', '5555-0000', 1),
    (ST_GeomFromText('POINT(114.109497 22.396428)'), 'Пункт незламності', '08.00-20.00', 22.396428, 'Кафе', '5555-0000', 1);

use invPointMap;
#
# INSERT INTO map_point (coordinates, description, hours_of_work, name, phone, user_owner)
# VALUES(ST_GeomFromText('POINT(48.468416 31.169128)'), 'Пункт незламності', '08.00-20.00', 'Кафе', '5555-0000', 1);
# lat lng

INSERT INTO map_point (coordinates, description, hours_of_work, name, phone, user_owner)
VALUES(ST_GeomFromText('POINT(49.96585677169776 33.61018554839228)'), 'Пункт незламності', '08.00-20.00', 'Кафе', '5555-0000', 1);


SELECT * FROM map_point WHERE ST_Intersects(coordinates, ST_MakeEnvelope(Point(26.27617956769251, 44.389999999999105), Point(36.24264053591213, 52.36999999999915)))