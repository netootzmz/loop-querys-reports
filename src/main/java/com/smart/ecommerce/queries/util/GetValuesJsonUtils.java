package com.smart.ecommerce.queries.util;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

//import lombok.extern.slf4j.Slf4j

//@Slf4j
public class GetValuesJsonUtils {

  private GetValuesJsonUtils() { }

  /**
   * Gets the json string.
   *
   * @param obj obj
   * @param key key
   * @return json string
   */
  public static String getJsonString(JSONObject obj, String key) {
    try {
      if (obj.has(key)) {
        return obj.getString(key);
      }else {
        return null;
      }
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Gets the json int.
   *
   * @param obj obj
   * @param key key
   * @return json int
   */
  public static Integer getJsonInt(JSONObject obj, String key) {
    try {
      if (obj.has(key)) {
        return obj.getInt(key);
      }else {
        return 0;
      }
    } catch (Exception e) {
      return 0;
    }
  }

  /**
   * Gets the json double.
   *
   * @param obj obj
   * @param key key
   * @return json double
   */
  public static Double getJsonDouble(JSONObject obj, String key) {
    try {
      if (obj.has(key)) {
        return obj.getDouble(key);
      }else {
        return 0D;
      }
    } catch (Exception e) {
      return 0D;
    }
  }

  /**
   * Gets the json boolean.
   *
   * @param obj obj
   * @param key key
   * @return json boolean
   */
  public static Boolean getJsonBoolean(JSONObject obj, String key) {
    try {
      if (obj.has(key)) {
        return obj.getBoolean(key);
      }else {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Gets the json object.
   *
   * @param obj obj
   * @param key key
   * @return json object
   */
  public static JSONObject getJsonObject(JSONObject obj, String key) {
    try {
      if (obj.has(key)) {
        return obj.getJSONObject(key);
      }else {
        return null;
      }
    } catch (Exception e) {
      return null;
    }
  }

  public static String getJsonNodeString(JsonNode obj, String key) {
    try {
      if (obj.has(key)) {
        return obj.get(key).textValue();
      }else {
        return null;
      }
    } catch (Exception e) {
      return null;
    }
  }

  public static Double getJsonNodeDouble(JsonNode obj, String key) {
    try {
      if (obj.has(key)) {
        return obj.get(key).asDouble();
      }else {
        return 0D;
      }
    } catch (Exception e) {
      return 0D;
    }
  }

  public static Integer getJsonNodeInt(JsonNode obj, String key) {
    try {
      if (obj.has(key)) {
        return obj.get(key).asInt();
      }else {
        return 0;
      }
    } catch (Exception e) {
      return 0;
    }
  }
  
  public static Long getJsonNodeLong(JsonNode obj, String key) {
    try {
      if (obj.has(key)) {
        return obj.get(key).asLong();
      }else {
        return 0L;
      }
    } catch (Exception e) {
      return 0L;
    }
  }

  public static Boolean getJsonNodeBoolean(JsonNode obj, String key) {
    try {
      if (obj.has(key)) {
        return obj.get(key).asBoolean();
      }else {
        return Boolean.FALSE;
      }
    } catch (Exception e) {
      return Boolean.FALSE;
    }
  }

}
