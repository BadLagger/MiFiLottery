-- Вставка пользователя Luba
INSERT INTO Users (id, name, password, telegram, balance, role_id)
VALUES (100, 'Luba', 'test', '765043566', 1000, 2);

-- Вставка типа лотереи, если еще не был добавлен
INSERT INTO LotteryType (id, description, ticket_price, min_ticket, prize_pool_percentage, algorithm_type, algorithm_rules)
VALUES (
    2,
    '5 из 36',
    100.00,
    1,
    0.6,
    'RANDOM_UNIQUE_NUMBERS',
    '{"algorithmRules":{"RANDOM_UNIQUE_NUMBERS":{"numberCount":5,"minNumber":1,"maxNumber":36,"sorted":true,"allowDuplicates":true}}}'
);

-- Вставка розыгрыша, завершившегося 601 минуту назад
INSERT INTO Draw (id, lottery_type_id, name, start_time, duration, status)
VALUES (
    42,
    2,
    'completed-draw',
    TIMESTAMPADD(MINUTE, -601, CURRENT_TIMESTAMP),
    600,
    'COMPLETED'
);

-- Билет пользователя на завершённый розыгрыш
INSERT INTO Ticket (id, user_id, draw_id, data, status)
VALUES (
    500,
    100,
    42,
    '[1, 5, 10, 20, 30]',
    'INGAME'
);
