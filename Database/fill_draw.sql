INSERT INTO Draw(lottery_type_id, name, startTime, duration, status) VALUES
(1, 'Lottery1', NOW() + INTERVAL '2 HOURS', 5, 'planned'),
(1, 'Lottery2', NOW() + INTERVAL '10 HOURS', 7, 'planned'),
(1, 'Lottery3', NOW() + INTERVAL '1 HOURS', 2, 'active'),
(1, 'Lottery4', NOW() - INTERVAL '3 HOURS', 3, 'completed');