-- Начальные данные
INSERT INTO lottery_type
(description, ticket_price, min_ticket, prize_pool_percentage, algorithm_type, algorithm_rules)
VALUES (
           '5 из 36',
           100.00,
           1,
           0.6,
           'RANDOM_UNIQUE_NUMBERS',
           '{"algorithmRules":{"RANDOM_UNIQUE_NUMBERS":{"numberCount":5,"minNumber":1,"maxNumber":36,"sorted":true,"allowDuplicates":true}}}'::jsonb
       );

INSERT INTO roles (name, description) VALUES
                                          ('admin', 'Администратор'),
                                          ('user', 'Пользователь');

INSERT INTO users (name, password, telegram, balance, role_id) VALUES
                                                                   ('User1', 'User1', 'simple_user1', 0, 2),
                                                                   ('User2', 'User2','simple_user2', 100, 2),
                                                                   ('Admin', 'Admin','iamadmin', 0, 1);

INSERT INTO draw(lottery_type_id, name, start_time, duration, status) VALUES
                                                                          (1, 'planned', CURRENT_TIMESTAMP + INTERVAL '30 minutes', 600, 'PLANNED'),
                                                                          (1, 'completed', CURRENT_TIMESTAMP - INTERVAL '601 minutes', 600, 'COMPLETED'),
                                                                          (1, 'active', CURRENT_TIMESTAMP - INTERVAL '1 hour', 620, 'ACTIVE');