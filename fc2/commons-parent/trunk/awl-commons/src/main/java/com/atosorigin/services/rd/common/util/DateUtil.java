/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.sql.Timestamp;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ 
/*     */ public class DateUtil
/*     */ {
/*  39 */   public static String FORMAT_DB = "yyyyMMddHHmmss";
/*     */ 
/*  41 */   private static SimpleDateFormat dateFormatDB = new SimpleDateFormat(FORMAT_DB);
/*     */ 
/*     */   public static boolean estFeriee(Calendar cal)
/*     */   {
/*  50 */     boolean reponse = false;
/*     */ 
/*  52 */     SimpleDateFormat formatCal = new SimpleDateFormat("MMdd");
/*  53 */     String dateCal = formatCal.format(cal.getTime());
/*  54 */     String[] tabDateFeriee = { "0101", 
/*  55 */       "0501", 
/*  56 */       "0508", 
/*  57 */       "0714", 
/*  58 */       "0815", 
/*  59 */       "1101", 
/*  60 */       "1111", 
/*  61 */       "1225" };
/*     */ 
/*  63 */     for (int i = 0; (i < tabDateFeriee.length) && (!reponse); ++i)
/*     */     {
/*  65 */       reponse = tabDateFeriee[i].equals(dateCal);
/*     */     }
/*     */ 
/*  68 */     return reponse;
/*     */   }
/*     */ 
/*     */   public static boolean estFermee(Calendar cal)
/*     */   {
/*     */     boolean reponse;
/*     */   //  boolean reponse;
/*  80 */     if ((cal.get(7) == 7) || 
/*  82 */       (cal.get(7) == 1) || 
/*  84 */       (estFeriee(cal)))
/*     */     {
/*  86 */       reponse = true;
/*     */     }
/*     */     else
/*     */     {
/*  90 */       reponse = false;
/*     */     }
/*     */ 
/*  93 */     return reponse;
/*     */   }
/*     */ 
/*     */   public static GregorianCalendar getGregorianCalendarDate(String date)
/*     */     throws Exception
/*     */   {
/* 105 */     int year = Integer.valueOf(date.substring(0, 4)).intValue();
/* 106 */     int month = Integer.valueOf(date.substring(4, 6)).intValue() - 1;
/* 107 */     int day = Integer.valueOf(date.substring(6, 8)).intValue();
/* 108 */     int hour = Integer.valueOf(date.substring(8, 10)).intValue();
/* 109 */     int min = Integer.valueOf(date.substring(10, 12)).intValue();
/* 110 */     int sec = Integer.valueOf(date.substring(12, 14)).intValue();
/*     */ 
/* 112 */     return new GregorianCalendar(year, month, day, hour, min, sec);
/*     */   }
/*     */ 
/*     */   public static String getStringDate(GregorianCalendar date)
/*     */   {
/* 123 */     String year = String.valueOf(date.get(1));
/*     */ 
/* 125 */     String month = String.valueOf(date.get(2) + 1);
/* 126 */     month = StringUtil.alignLeft(month, 2, '0');
/*     */ 
/* 128 */     String day = String.valueOf(date.get(5));
/* 129 */     day = StringUtil.alignLeft(day, 2, '0');
/*     */ 
/* 131 */     String hour = String.valueOf(date.get(11));
/* 132 */     hour = StringUtil.alignLeft(hour, 2, '0');
/*     */ 
/* 134 */     String min = String.valueOf(date.get(12));
/* 135 */     min = StringUtil.alignLeft(min, 2, '0');
/*     */ 
/* 137 */     String sec = String.valueOf(date.get(13));
/* 138 */     sec = StringUtil.alignLeft(sec, 2, '0');
/*     */ 
/* 140 */     return year + month + day + hour + min + sec;
/*     */   }
/*     */ 
/*     */   public static String getDate(Date date, String format)
/*     */   {
/* 150 */     SimpleDateFormat dateFormat = new SimpleDateFormat(format);
/* 151 */     return dateFormat.format(date);
/*     */   }
/*     */ 
/*     */   public static String getDate(String format)
/*     */   {
/* 156 */     SimpleDateFormat formatter = new SimpleDateFormat(format);
/* 157 */     return formatter.format(GregorianCalendar.getInstance().getTime());
/*     */   }
/*     */ 
/*     */   public static Date getDate(String dateString, String format)
/*     */     throws Exception
/*     */   {
/* 169 */     SimpleDateFormat dateFormat = new SimpleDateFormat(format);
/* 170 */     return dateFormat.parse(dateString);
/*     */   }
/*     */ 
/*     */   public static Date getDateDB(String dateString)
/*     */     throws Exception
/*     */   {
/* 180 */     return dateFormatDB.parse(dateString);
/*     */   }
/*     */ 
/*     */   public static Date getDateFromTimestamp(Timestamp timestamp)
/*     */   {
/* 190 */     return new Date(timestamp.getTime());
/*     */   }
/*     */ 
/*     */   public static String getDisplayStringDate(GregorianCalendar date)
/*     */   {
/* 203 */     String year = String.valueOf(date.get(1));
/*     */ 
/* 205 */     String month = String.valueOf(date.get(2) + 1);
/* 206 */     month = StringUtil.alignLeft(month, 2, '0');
/*     */ 
/* 208 */     String day = String.valueOf(date.get(5));
/* 209 */     day = StringUtil.alignLeft(day, 2, '0');
/*     */ 
/* 211 */     return day + "/" + month + "/" + year;
/*     */   }
/*     */ 
/*     */   public static String getStringFromTimestamp(Timestamp timestamp, String format)
/*     */   {
/*     */     try
/*     */     {
/* 224 */       SimpleDateFormat formatter = new SimpleDateFormat(format);
/* 225 */       return formatter.format(getDateFromTimestamp(timestamp)); } catch (Exception e) {
/*     */     }
/* 227 */     return "";
/*     */   }
/*     */ 
/*     */   public static Timestamp getTimestamp()
/*     */   {
/* 238 */     return getTimestampFromDate(
/* 239 */       GregorianCalendar.getInstance().getTime());
/*     */   }
/*     */ 
/*     */   public static Timestamp getTimestampFromString(String date, String format)
/*     */   {
/*     */     try
/*     */     {
/* 249 */       SimpleDateFormat formatter = new SimpleDateFormat(format);
/* 250 */       return getTimestampFromDate(formatter.parse(date)); } catch (Exception e) {
/*     */     }
/* 252 */     return null;
/*     */   }
/*     */ 
/*     */   public static Timestamp getTimestampFromDate(Date date)
/*     */   {
/* 262 */     Timestamp timestamp = new Timestamp(date.getTime());
/* 263 */     return timestamp;
/*     */   }
/*     */ 
/*     */   public static String getYear()
/*     */   {
/* 275 */     Calendar cal = Calendar.getInstance();
/*     */     String currentYear;
/*     */   //  String currentYear;
/* 277 */     if (cal.get(1) < 10)
/* 278 */       currentYear = "0" + String.valueOf(cal.get(1));
/* 279 */     else currentYear = String.valueOf(cal.get(1));
/*     */ 
/* 281 */     return currentYear;
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.DateUtil
 * JD-Core Version:    0.5.4
 */