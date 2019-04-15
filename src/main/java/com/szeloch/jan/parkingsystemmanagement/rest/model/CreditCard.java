package com.szeloch.jan.parkingsystemmanagement.rest.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Currency;

@Data
@Entity
@NoArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue
    private Long id;

    private Currency currency;

    private Long creditCardNumber;

    private Integer ccvCode;

    public CreditCard(Currency currency, Long creditCardNumber, Integer ccvCode) {
        this.currency = currency;
        this.creditCardNumber = creditCardNumber;
        this.ccvCode = ccvCode;
    }
}
