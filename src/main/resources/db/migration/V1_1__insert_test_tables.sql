-- Начальные данные
INSERT INTO Lotterytype
(description, ticket_price, min_ticket, prize_pool_percentage, algorithm_type, algorithm_rules)
VALUES (
           '5 из 36',
           100.00,
           1,
           0.6,
           'RANDOM_UNIQUE_NUMBERS',
           '{"algorithmType":"RANDOM_UNIQUE_NUMBERS","numberCount":5,"minNumber":1,"maxNumber":36,"sorted":true,"allowDuplicates":true}'::jsonb
       );

INSERT INTO Roles (name, description) VALUES
                                          ('admin', 'Администратор'),
                                          ('user', 'Пользователь');

INSERT INTO Users (name, telegram, balance, role_id) VALUES
                                                         ('User1', 'simple_user1', 0, 2),
                                                         ('User2', 'simple_user2', 100, 2),
                                                         ('Admin', 'iamadmin', 0, 1);

INSERT INTO Draw(lottery_type_id, name, start_time, duration, status) VALUES
      (1, 'planned', TIMESTAMPADD(SQL_TSI_MINUTE, 30, CURRENT_TIMESTAMP), 600, 'PLANNED'),
      (1, 'completed', TIMESTAMPADD(SQL_TSI_MINUTE, -601, CURRENT_TIMESTAMP), 600, 'COMPLETED'),
      (1, 'active', TIMESTAMPADD(SQL_TSI_HOUR, -1, CURRENT_TIMESTAMP), 620, 'ACTIVE');
