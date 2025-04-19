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



