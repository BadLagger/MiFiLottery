package com.example.lottery.dto;

public enum InvoiceStatus {
    UNPAID,    // инвойс создан, но не оплачен - по умолчанию
    PENDING,   // ожидает оплаты, в этом статусе нельзя отменить (блокировка на оплату)
    PAID,      // инвойс оплачен
    REFUNDED   // средства возвращены
}