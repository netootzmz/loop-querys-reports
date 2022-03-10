package com.smart.ecommerce.queries.model.dto;

import lombok.Data;

@Data
public class DetailDto {
	private String accessTypeIdentifier;
	private String additionalAmount;
	private Integer attendedByAcquirerIndicator;
	private String authenticationMethod;
	private String branch;
	private String cardName;
	private Integer cardPresenceIndicator;
	private String cardType;
	private String cardBrand;
	private Integer cardholderPresenceIndicator;
	private String deferredPaymentsDeferral;
	private String deferredPaymentsNumber;
	private String deferredpaymentsPlan;
	private Integer ecommerceIndicator;
	private String fiid;
	private String layoutVersion;
	private String logicalNetwork;
	private String merchantIdentifier;
	private String operationKey;
	private String posEntryMode;
	private String registryType;
	private String rejectReason;
	private Integer routingIndicator;
	private String securityLevelAcquirer;
	private String serviceCodeFlag;
	private Integer speiIndicator;
	private String status;
	private Integer statusIndicator;
	private Integer terminalActivationByCardholder;
	private String terminalIdentifier;
	private String transactionTime;
	private String refSgNumber;
	private String refSpNumber;
	private String approvalCode;
	private Double amountTip;
	private String currencyCode;
	private String respMessage;
}
