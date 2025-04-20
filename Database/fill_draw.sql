INSERT INTO Draw(lottery_id, startTime, status) VALUES
(1, NOW() + INTERVAL '2 HOURS', 'planned'),
(1, NOW() + INTERVAL '10 HOURS', 'planned'),
(1, NOW() + INTERVAL '1 HOURS', 'active'),
(1, NOW() - INTERVAL '3 HOURS', 'completed');