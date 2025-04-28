-- Начальные данные
INSERT INTO LotteryType (description, ticket_price, min_ticket, prize_pool_percentage) VALUES
    ('auto', 100.0, 5, 0.6);

INSERT INTO Roles (name, description) VALUES
                                          ('admin', 'Администратор'),
                                          ('user', 'Пользователь');

INSERT INTO Users (name, telegram, balance, role_id) VALUES
                                                         ('User1', 'simple_user1', 0, 2),
                                                         ('User2', 'simple_user2', 100, 2),
                                                         ('Admin', 'iamadmin', 0, 1);

INSERT INTO Draw(lottery_type_id, name, start_time, duration, status) VALUES
      (1, 'LotteryStartsAfter2Minutes', TIMESTAMPADD(SQL_TSI_MINUTE, 2, CURRENT_TIMESTAMP), 5, 'PLANNED'), -- добавляем 2 минуты
      (1, 'LotteryStartsAfter10Minutes', TIMESTAMPADD(SQL_TSI_MINUTE, 10, CURRENT_TIMESTAMP), 7, 'PLANNED'), -- добавляем 10 минут
      (1, 'ShouldBeCompletedIn2MinutesAfterStart', TIMESTAMPADD(SQL_TSI_HOUR, -1, CURRENT_TIMESTAMP), 62, 'ACTIVE'),
      (1, 'ShouldBeCompletedAfterStart', TIMESTAMPADD(SQL_TSI_DAY, -1, CURRENT_TIMESTAMP), 2, 'ACTIVE'),
      (1, 'CompletedDraw', TIMESTAMPADD(SQL_TSI_HOUR, -3, CURRENT_TIMESTAMP), 3, 'COMPLETED'), -- вычитаем 3 часа
      (1, 'ShouldBeCancelledAfterStart', TIMESTAMPADD(SQL_TSI_DAY, -3, CURRENT_TIMESTAMP), 3, 'PLANNED');
