package com.smart.ecommerce.queries.util;

import com.smart.ecommerce.entity.core.CoreErrorCode;
import com.smart.ecommerce.queries.model.dto.CoreErrorCodeDto;

import java.util.List;

public class ErrorCode {

  public static final String ERROR_CODE_PROCESADO_CORRECTAMENTE     = "00";
  public static final String ERROR_CODE_SIN_REGISTROS               = "01";
  public static final String ERROR_CODE_PARAMETROS_PREEXISTENTES    = "02";
  public static final String ERROR_CODE_NO_EXISTE_EN_BD             = "03";
  public static final String ERROR_CODE_PARAMETROS_INCOMPLETOS      = "04";
  public static final String ERROR_CODE_NO_VALIDO                   = "05";
  public static final String ERROR_CO_DEERROR_AL_ELIMINAR           = "06";
  public static final String ERROR_CODE_ERROR_EN_EL_SISTEMA         = "07";
  public static final String ERROR_CODE_ROL_ASOSCIADO_A_UN_USUARIO  = "08";
  public static final String ERROR_CODE_IMPOSIBLE_REALIZAR_EDICION  = "09";
  public static final String ERROR_CODE_USUARIO_CORREO_NO_EXISTE    = "10";
  public static final String ERROR_CODE_INACTIVO                    = "11";
  public static final String ERROR_CODE_CODIGO_VERIFICACION_ERRONEO = "12";
  public static final String ERROR_CODE_CODIGO_VERIFICACION_VENCIDO = "13";
  public static final String ERROR_CODE_DOCUMENTO_NO_EXISTE_EN_BD   = "24";
  public static final String ERROR_CODE_COMMERCIAL_BUSINESS         = "26";
  public static final String ERROR_CODE_TYPE_SERVICE                = "27";
  public static final String ERROR_CODE_CORE_BILLING                = "28";
  public static final String ERROR_CODE_CORE_SALE_DISCOUNT_RATE     = "29";
  public static final String ERROR_CODE_EL_DATO_INGRESADO_NO_ES_UN_EMAIL  = "33";
  public static final String ERROR_CODE_CLIENT_AFFILIATION  = "37";
  public static final String ERROR_CODE_CLIENT_AFFILIATION_EXISTENTE  = "38";
  public static final String ERROR_CODE_MESSAGE_CORETYPEMESSAGECATALOG  = "39";
  public static final String ERROR_CODE_MESSAGE_ACQUIRECATALOG  = "40";
  public static final String ERROR_CODE_MESSAGE_COREEMISORCATALOG  = "41";
  public static final String ERROR_CODE_MESSAGE_AFFECTATIONCATALOG  = "42";
  public static final String ERROR_CODE_MESSAGE_STATUSALERT  = "43";
  public static final String ERROR_CODE_OVERRIDE_RATE  = "44";
  public static final String ERROR_CODE_OVERRIDE_RATE_EXIST  = "45";
  public static final String ERROR_CODE_AFFILIATION_BUSINESS = "46";
  public static final String ERROR_CODE_AFFILIATION = "47";
  public static final String ERROR_CODE_AUTH = "48";
  public static final String ERROR_CODE_GROUP_CONFIGURATION_CODI_MEMBERSHIP = "49";
  public static final String ERROR_CODE_CAT_BANK_EXIST = "58";

  public static CoreErrorCodeDto getError(List<CoreErrorCode> listError, String code) {
    CoreErrorCodeDto dto = new CoreErrorCodeDto();

    for (CoreErrorCode error : listError) {

      if (error.getCode().equals(code)) {

        dto.setCode(error.getCode());
        dto.setMessage(error.getMessage());
        dto.setStatus(error.getStatus());
        break;

      }

    }
    return dto;
  }


}
