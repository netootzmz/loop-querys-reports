package com.smart.ecommerce.queries.Enum;

public enum ResponseType {
  ACEPT("00", "Información procesada correctamente"),
  LISTEMPTY("01", "Sin resgistros para mostrar"),
  EXIST("02", "Existe un %s parametrizado con los mismos datos"),
  NOT_EXIST("03", "No existe el %s en DB"),
  DATA_EMPTY("04", "Parámetros incompletos"),
  DATA_INCORRECT("05", "%s no válido"),
  DELETE_ERROR("06", "Error al eliminar registro"),
  NOT_AVAILABLE("07", "Ocurrio un error en el sistema"),
  ROLE_ASSOCIATE("08", "Rol asociado a un usuario"),
  MENU_ASSOCIATE("09","Imposible realizar la edición, existen parámetros asociados")
  ;

  private String code;
  private String message;

  ResponseType(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
