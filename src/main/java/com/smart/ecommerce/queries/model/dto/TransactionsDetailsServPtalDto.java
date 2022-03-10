package com.smart.ecommerce.queries.model.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionsDetailsServPtalDto {
    private Boolean detail;
    private TransactionsDetailsClientServPtalDto clientServPtalDto; /**Cliente**/
    private TransactionsDetailsPromissoryNoteServPtalDto promissoryNoteServPtalDto; /**Pagaré**/
    private TransactionsDetailsInformationServPtalDto informationServPtalDto; /**Transacción**/
    private TransactionsDetailsCardServPtalDto cardServPtalDto; /**Tarjeta**/
    private TransactionsDetailsAmountAndCommisionsServPtalDto amountAndCommisionsServPtalDto; /**Costos y Comisiones**/
}
