-- Лотерея
CREATE TABLE IF NOT EXISTS Lottery (
	id SERIAL PRIMARY KEY,
	lottery_type VARCHAR(10) NOT NULL CHECK (lottery_type IN ('auto', '5in36')),
	ticket_price DECIMAL(8, 2) NOT NULL CHECK(ticket_price > 0),
	min_ticket INT CHECK(min_ticket >= 0) DEFAULT 0,
	price_pool_percantage INT NOT NULL CHECK(price_pool_percantage >= 0 AND price_pool_percantage <= 100)
);

-- Тираж
CREATE TABLE IF NOT EXISTS Draw (
    id SERIAL PRIMARY KEY,
	lottery_id INT,
	startTime DATE,
	status VARCHAR(15) NOT NULL CHECK(status IN ('planned', 'active', 'completed', 'cancelled')),
	FOREIGN KEY(lottery_id) REFERENCES Lottery(id)
);

-- Результат тиража
CREATE TABLE IF NOT EXISTS DrawResult (
	id SERIAL PRIMARY KEY,
	draw_id INT,
	winningCombination INT[] NOT NULL,
	resultTime DATE,
	FOREIGN KEY(draw_id) REFERENCES Draw(id)
);

-- Создаём тип пользователя
DO $$
BEGIN
	CREATE TYPE customer_type as ENUM('admin', 'user');
EXCEPTION
	WHEN duplicate_object THEN
		RAISE NOTICE 'customer_type already exists';
END$$
LANGUAGE plpgsql;

-- Пользователи 
CREATE TABLE IF NOT EXISTS Customers (
	id SERIAL PRIMARY KEY,
	role customer_type NOT NULL,
	username VARCHAR(50) NOT NULL UNIQUE, -- здесь храним login. telegram или email
	info JSONB  -- это поле выполняет роль смешанного типа, для пользователя тут может храниться баланс, а для админов хэш пароля например 
);




