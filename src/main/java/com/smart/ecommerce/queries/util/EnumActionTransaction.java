package com.smart.ecommerce.queries.util;

public enum EnumActionTransaction {
	CC("CC", "concilied"), PC("PC","processed"), DP("DP","dispersed"), CD("CD","created_at");

	private String code;
	private String value;

	private EnumActionTransaction(String code, String value) {
		this.code = code;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getCode() {
		return code;
	}
}
