package com.smart.ecommerce.oauth;

public class JwtConfig {
	
	public static final String PRIVATE_KEY_RSA = "-----BEGIN RSA PRIVATE KEY-----\n"
			+ "MIIEpAIBAAKCAQEAqvvzIQgec2SPCBcTH/FLa8RAXuwzttTcvfY3i20Sf2+Otz41\n"
			+ "dFr//sVK1Dbd6oeGMW9xaEdAZB7yFD9i1vk/5zUqMuPb9k4Cmi7vlRDhdXmEt3yt\n"
			+ "lxHnsXXoOPpc9cQgtITQm8HEBlW9jgUuV2h8so1tRZIYa1I214UttCBq2TQPDS7N\n"
			+ "EWb4qBJ4oTaZpnizASzkSFRqId2bFwr57OqRPgCVpn2c/ZU/7B0FPhOzObACtuc7\n"
			+ "BM2qLYEhzpnIobSfQHhpfAnLfOOYvoD+vy0E1j2MIoyTG1/LYQhrQuNcwIBPhAs7\n"
			+ "nO6AA5SGuicjlcbDHhZiHQdpyEMQENaVOaB7GwIDAQABAoIBAANG4Nn5NdwwSS12\n"
			+ "g79Q+IkQMcuf6z3Nxo09csMRTBF1Kd8JKi0YsV9ImK6IfqhIAKJh6GJ/OxSwIyOQ\n"
			+ "M2y7SiY8NprTExwdUp9x1tWchTWjJo/9q7YBeddE0zaEVdO/9oB/KyBRDliwu/lE\n"
			+ "0CaMshcZ7sYujpk/8h0fSEjzdhjDhpZGfj+pMZoVpqbGaaUFHmiUrLYnOOAaLaXH\n"
			+ "0WWnF/KJ5MJJ5pHl1JbgDoyluEww/dLk5KhYhc92N2kASReJwkxmqX0D3+fDMigi\n"
			+ "5WwCuxq5l/SCTjhwC0dzOFEl0xsMUnN2KJVIgBMeCI1rmtCTw6tjhwulaHjpKXmQ\n"
			+ "cW9b0sECgYEA6CIYSAINBCobwomannJB3hKIMhV5FyWwXn4UAjYP+VVDEAhcp0JF\n"
			+ "GgW6eTtQ6/3jgmEop/GcC7vhCDVt42Jf+mLN4mxTA84mnZesZ/1vqgYNKUok90nD\n"
			+ "N4HT757f6lDCfkeMeYl4O7DSzW0eaeA8bJtiGFHAoOGDKdqq77UM9P8CgYEAvJBe\n"
			+ "+TT33P7uaI2b5U6OLxUmEDIOEeZbWfTLHBo4BnSYKXQBzdLf8mSfZy3qiOHaxAbs\n"
			+ "UmowXTldefYtGXYwZaYH5oQFrNohr3dURXNw5e42XLWzsHUFzqz2kZQWJZ8lDZyT\n"
			+ "IZQOct9fJDb9Q3SbaJ6WcRii41VLpG7Zga+HreUCgYEAtm2VB4IVGKC/EDNz//Fr\n"
			+ "Mkr+Hd4iO76yzI/oyZQTGa+X9JGdvxSlmb3Gvl+PSOdOVLhmaxXFvLL6NqlGotw8\n"
			+ "8VmU08ytsN/iGReJtm/FwajfzwMl6fFeKdEt7bK95jdxoiw4iYsvojlkcWy/6hT/\n"
			+ "Z5r1jIczCzwvCEWA8MEoXaECgYEAm1IUWRQvYwM+oDKdCkolxlSWUNraShZEgxmn\n"
			+ "wFM8eNzLhcG/xql/vGrz/oqM2QmpKLVHLi+3/OJGxDJqPcHj5abYdSGkhxf6jU0u\n"
			+ "xjJr+Ym5j65AAAa0rOQ+CfZAM+ceH1MswojxSeACv5LoFQtVA901sJ0Jd1aIAz8n\n"
			+ "vLI4gZ0CgYBtI/i+p5C2Hv0dToengDo1+lkfD9wMXYvl3DTU//O8o+QWKGRmXgVA\n"
			+ "4FipfiojAuVFhbQ/Sqvfeh3AxDWcsfW2BY47HtMyRMBLdSfabH09stOB+uqnblFP\n"
			+ "zVAuyGXh1DY/x6M5iYgkV3gDh/N8yCW7J0kGbrcUBzc0Hqqw45gjgA==\n" 
			+ "-----END RSA PRIVATE KEY-----";

	public static final String PUBLIC_KEY_RSA = "-----BEGIN PUBLIC KEY-----\n"
			+ "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqvvzIQgec2SPCBcTH/FL\n"
			+ "a8RAXuwzttTcvfY3i20Sf2+Otz41dFr//sVK1Dbd6oeGMW9xaEdAZB7yFD9i1vk/\n"
			+ "5zUqMuPb9k4Cmi7vlRDhdXmEt3ytlxHnsXXoOPpc9cQgtITQm8HEBlW9jgUuV2h8\n"
			+ "so1tRZIYa1I214UttCBq2TQPDS7NEWb4qBJ4oTaZpnizASzkSFRqId2bFwr57OqR\n"
			+ "PgCVpn2c/ZU/7B0FPhOzObACtuc7BM2qLYEhzpnIobSfQHhpfAnLfOOYvoD+vy0E\n"
			+ "1j2MIoyTG1/LYQhrQuNcwIBPhAs7nO6AA5SGuicjlcbDHhZiHQdpyEMQENaVOaB7\n" 
			+ "GwIDAQAB\n"
			+ "-----END PUBLIC KEY-----";

}
