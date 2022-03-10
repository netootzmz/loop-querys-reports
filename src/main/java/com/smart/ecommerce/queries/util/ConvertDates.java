/**
 * 
 */
package com.smart.ecommerce.queries.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Eduardo Valeriano
 *
 */
@Slf4j
public class ConvertDates {

    private ConvertDates() {}
	
	public static String getDateDDMMYYYY(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	public static String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static Date addMinutesDate(Date date, Integer minutes){
		Date dateAdd = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date); // Configuramos la fecha que se recibe
		calendar.add(Calendar.MINUTE, minutes);  // numero de horas a añadir, o restar en caso de horas<0
		dateAdd = calendar.getTime();

		return dateAdd;
	}
	
	  /**
	   * Subtract days.
	   *
	   * @param date the date
	   * @param days the days
	   * @return the date
	   */
	  public static Date subtractDays(Date date, int days) {
	          GregorianCalendar cal = new GregorianCalendar();
	          cal.setTime(date);
	          cal.add(Calendar.DATE, -days);
	                          
	          return cal.getTime();
	  }
	  
	  public static Date convertStrDate(String ptt, String dateStr) {

	    SimpleDateFormat sdf = new SimpleDateFormat(ptt);
	    Date dateConverted = null;
	    try {
	      dateConverted = sdf.parse(dateStr);
	    } catch (Exception e) {
	      log.info("Error converted date --> {}", e.getMessage());
	    }
	    return dateConverted;
	  }
	  
	  public static String convertDateToStr(String ptt, Date date) {
	    SimpleDateFormat sdf = new SimpleDateFormat(ptt);
	    String strConverted = null;
	    try {
	      strConverted = sdf.format(date);
	    } catch (Exception e) {
	      log.info("Error converted date --> {}", e.getMessage());
	    }
	    return strConverted;
	  }
	  
	  public static String getHoursToday() {
	    Integer hoursInt = LocalDateTime.now().getHour();
	    Integer minuteInt = LocalDateTime.now().getMinute();
	    String hours = getMinuteOrHours(hoursInt, "00");
	    String minutes = getMinuteOrHours(minuteInt, "00");
	    return hours + ":" + minutes;
	  }
	  
	  public static String getMinuteOrHours(Integer min, String cifras) {    
	    Integer len = min.toString().length();
	    String newCifras = "";
	    if (cifras.length() > len) {
	      newCifras = cifras.substring(len - 1, 1);
	    }
	    return newCifras + min.toString();
	  }



}
