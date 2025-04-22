INSERT INTO Draw(lottery_type_id, name, start_time, duration, status) VALUES
(1, 'Lottery1', NOW() + INTERVAL '2 HOURS', 5, 'PLANNED'),
(1, 'Lottery2', NOW() + INTERVAL '10 HOURS', 7, 'PLANNED'),
(1, 'Lottery3', NOW() + INTERVAL '1 HOURS', 2, 'ACTIVE'),
(1, 'Lottery4', NOW() - INTERVAL '3 HOURS', 3, 'COMPLETED');