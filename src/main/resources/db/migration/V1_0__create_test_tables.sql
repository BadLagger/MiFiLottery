-- Создаем таблицу Roles
CREATE TABLE IF NOT EXISTS roles
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

-- Создаем таблицу Users
CREATE TABLE IF NOT EXISTS users
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    telegram VARCHAR(255) NOT NULL,
    balance BIGINT DEFAULT 0,
    role_id BIGINT NOT NULL,
    FOREIGN KEY(role_id) REFERENCES roles(id)
);

-- Создаем enum тип для алгоритмов лотереи
CREATE TYPE algorithm_type AS ENUM ('RANDOM_UNIQUE_NUMBERS', 'FIXED_POOL', 'USER_SELECTED');

-- Создаем таблицу LotteryType
CREATE TABLE IF NOT EXISTS lottery_type (
                                            id BIGSERIAL PRIMARY KEY,
                                            description TEXT NOT NULL,
                                            ticket_price DECIMAL(8, 2) NOT NULL CHECK(ticket_price > 0),
                                            min_ticket INTEGER CHECK(min_ticket >= 0),
                                            prize_pool_percentage DOUBLE PRECISION NOT NULL CHECK(prize_pool_percentage BETWEEN 0 AND 1),
                                            algorithm_type algorithm_type NOT NULL,
                                            algorithm_rules JSONB NOT NULL
);

-- Создаем enum тип для статусов тиража
CREATE TYPE draw_status AS ENUM ('PLANNED', 'ACTIVE', 'COMPLETED', 'CANCELLED');

-- Таблица Draw
CREATE TABLE IF NOT EXISTS draw (
                                    id BIGSERIAL PRIMARY KEY,
                                    name TEXT NOT NULL,
                                    lottery_type_id BIGINT NOT NULL,
                                    start_time TIMESTAMP NOT NULL,
                                    duration INTEGER NOT NULL,
                                    status draw_status NOT NULL,
                                    FOREIGN KEY(lottery_type_id) REFERENCES lottery_type(id)
);

-- Таблица для результатов тиража
CREATE TABLE IF NOT EXISTS draw_result (
                                           id BIGSERIAL PRIMARY KEY,
                                           draw_id BIGINT NOT NULL,
                                           winning_combination JSONB NOT NULL,
                                           winning_tickets JSONB,
                                           result_time TIMESTAMP NOT NULL,
                                           prize_pool DECIMAL(20, 2) NOT NULL,
                                           FOREIGN KEY(draw_id) REFERENCES draw(id)
);

-- Создаем enum тип для статусов билетов
CREATE TYPE ticket_status AS ENUM ('INGAME', 'WIN', 'LOSE', 'CANCELLED');

-- Таблица Ticket
CREATE TABLE IF NOT EXISTS ticket (
                                      id BIGSERIAL PRIMARY KEY,
                                      user_id BIGINT NOT NULL,
                                      draw_id BIGINT NOT NULL,
                                      data JSONB NOT NULL,
                                      status ticket_status NOT NULL,
                                      FOREIGN KEY(user_id) REFERENCES users(id),
                                      FOREIGN KEY(draw_id) REFERENCES draw(id)
);