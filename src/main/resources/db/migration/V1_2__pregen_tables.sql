-- Таблица предсозданных билетов
CREATE TABLE pre_generated_tickets
(
    id BIGSERIAL PRIMARY KEY,
    draw_id BIGINT NOT NULL,
    numbers JSONB NOT NULL,
    numbers_hash VARCHAR(64) NOT NULL,
    issued BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_draw FOREIGN KEY (draw_id) REFERENCES draw (id) ON DELETE CASCADE
);

-- Индекс для уникальной комбинации в рамках тиража
CREATE UNIQUE INDEX idx_pre_generated_ticket_draw_hash ON pre_generated_tickets (draw_id, numbers_hash);