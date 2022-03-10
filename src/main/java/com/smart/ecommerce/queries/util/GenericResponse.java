package com.smart.ecommerce.queries.util;

import com.smart.ecommerce.queries.Enum.ResponseType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class GenericResponse {

    private String codeStatus;
    private String message;
    private Map<String,Object> information;

    public GenericResponse() {
    }

    public GenericResponse(ResponseType type, Map information){
        this.codeStatus = type.getCode();
        this.message = type.getMessage();
        this.information = information;
    }

    public GenericResponse(String codeStatus, String message){
      this.codeStatus = codeStatus;
      this.message = message;
    }

    public GenericResponse(ResponseType type){
      this.codeStatus = type.getCode();
      this.message = type.getMessage();
    }

    public GenericResponse(ResponseType type , String message){
        this.codeStatus = type.getCode();
        this.message = message;
    }

    public GenericResponse(String codeStatus, String message, String key, Object value ){
        this.codeStatus = codeStatus;
        this.message = message;

        if(null == this.information)
            information = new HashMap<>();
        information.put(key,value);
    }

    public GenericResponse(String codeStatus, String message, Map<String, Object> information) {
        this.codeStatus = codeStatus;
        this.message = message;
        this.information = information;
    }
}
