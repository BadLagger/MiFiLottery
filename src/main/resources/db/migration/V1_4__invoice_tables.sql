-- Создаем enum тип для статусов инвойсов
CREATE TYPE invoice_status AS ENUM ('UNPAID', 'PENDING', 'PAID', 'REFUNDED');

CREATE TABLE invoice (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
                         ticket_data JSONB NOT NULL,
                         register_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         payment_link TEXT,
                         status invoice_status NOT NULL DEFAULT 'UNPAID',
                         cancelled INTEGER NOT NULL DEFAULT 0 CHECK (cancelled IN (0, 1))
);