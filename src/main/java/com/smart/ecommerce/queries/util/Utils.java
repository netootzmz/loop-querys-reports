package com.smart.ecommerce.queries.util;

import com.google.common.hash.Hashing;
import com.smart.ecommerce.logging.Console;
import com.smart.ecommerce.queries.model.dto.ClientAffiliationDTO;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

  public static boolean isMailValid(String email) {

    if (!email.contains("@")) {
      return false;
    }

    Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
          + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    Matcher mather = pattern.matcher(email);

    return mather.find();
  }

  public static String encryptSha256(String value) {
    return Hashing.sha256().hashString(value, StandardCharsets.UTF_8).toString();
  }

  public static String generateCodeVerfication() {
    String codeVerification = "";

    SecureRandom random = new SecureRandom();
    codeVerification = new BigInteger(130, random).toString(32);

    return codeVerification.substring(0, 8);

  }

  public static void info(String msg, String idOperation) {
    Console.writeln(Console.Level.INFO, idOperation, msg);
  }

  public static String getMessageClientConfKafka(ClientAffiliationDTO item) {

    StringBuilder message = new StringBuilder();

    if(item.getTypeClient() == 1 ){
      message.append((null != item.getClientId() && !item.getClientId().equals("") ? item.getClientId() : "0"));
      message.append("|");
      message.append("|");

    }else if(item.getTypeClient() == 2){
      message.append("|");
      message.append((null != item.getBusinessId() && !item.getBusinessId().equals("") ? item.getBusinessId() : "0"));
      message.append("|");

    }



    message.append("|");
    message.append(item.getAffiliation());

    return message.toString();
  }
}
