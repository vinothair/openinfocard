/*     */ package com.atosorigin.services.rd.common.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class StringUtil
/*     */ {
/*     */   public static String alignLeft(String s, int size, char padding)
/*     */   {
/*  58 */     if (s.length() < size) {
/*  59 */       char[] pad = new char[size - s.length()];
/*     */ 
/*  61 */       for (int i = 0; i < size - s.length(); ++i) {
/*  62 */         pad[i] = padding;
/*     */       }
/*  64 */       return new String(pad) + s;
/*     */     }
/*  66 */     return s;
/*     */   }
/*     */ 
/*     */   public static String alignRight(String s, int size, char padding)
/*     */   {
/*  82 */     if (s.length() < size) {
/*  83 */       char[] pad = new char[size - s.length()];
/*     */ 
/*  85 */       for (int i = 0; i < size - s.length(); ++i) {
/*  86 */         pad[i] = padding;
/*     */       }
/*  88 */       return s + new String(pad);
/*     */     }
/*     */ 
/*  91 */     return s;
/*     */   }
/*     */ 
/*     */   public static final String chomp(String s)
/*     */   {
/* 104 */     if ((s == null) || (s.length() == 0)) {
/* 105 */       return s;
/*     */     }
/*     */ 
/* 108 */     if (s.charAt(s.length() - 1) == '\n') {
/* 109 */       return s.substring(0, s.length() - 1);
/*     */     }
/* 111 */     return s;
/*     */   }
/*     */ 
/*     */   public static final int countChar(String s, char theChar)
/*     */   {
/* 120 */     int c = 0;
/* 121 */     for (int i = 0; i < s.length(); ++i)
/* 122 */       if (s.charAt(i) == theChar)
/* 123 */         ++c;
/* 124 */     return c;
/*     */   }
/*     */ 
/*     */   public static boolean eq(String s1, String s2)
/*     */   {
/* 135 */     return s1.compareTo(s2) == 0;
/*     */   }
/*     */ 
/*     */   public static String join(String separator, String[] a)
/*     */   {
/* 145 */     StringBuffer buffer = new StringBuffer("");
/*     */ 
/* 147 */     for (int i = 0; i < a.length; ++i) {
/* 148 */       buffer.append(a[i]);
/* 149 */       if (i + 1 < a.length) {
/* 150 */         buffer.append(separator);
/*     */       }
/*     */     }
/*     */ 
/* 154 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public static final String quoteChar(String s0, char[] quoted)
/*     */   {
/* 165 */     String s = new String(s0);
/* 166 */     for (int i = 0; i < s.length(); ++i) {
/* 167 */       char c = s.charAt(i);
/* 168 */       for (int j = 0; j < quoted.length; ++j) {
/* 169 */         if (c == quoted[j]) {
/* 170 */           s = s.substring(0, i) + "\\" + s.substring(i);
/* 171 */           ++i;
/*     */         }
/*     */       }
/*     */     }
/* 175 */     return s;
/*     */   }
/*     */ 
/*     */   public static String removeHeadingSpaces(String s)
/*     */   {
/* 185 */     int i = 0;
/* 186 */     while ((i < s.length()) && (((s.charAt(i) == ' ') || (s.charAt(i) == '\t')))) {
/* 187 */       ++i;
/*     */     }
/*     */ 
/* 190 */     if (i == s.length()) {
/* 191 */       return "";
/*     */     }
/*     */ 
/* 194 */     return s.substring(i, s.length());
/*     */   }
/*     */ 
/*     */   public static String removeTrailingSpaces(String s)
/*     */   {
/* 205 */     int i = s.length();
/* 206 */     for (; (i > 0) && (((s.charAt(i - 1) == ' ') || (s.charAt(i - 1) == '\t'))); --i);
/* 208 */     if (i < 0) {
/* 209 */       return "";
/*     */     }
/* 211 */     return s.substring(0, i);
/*     */   }
/*     */ 
/*     */   public static String removePattern(String s, String pattern)
/*     */   {
/* 221 */     StringBuffer sb = new StringBuffer();
/* 222 */     int i = s.indexOf(pattern);
/* 223 */     System.out.println(s);
/*     */ 
/* 225 */     while (i >= 0) {
/* 226 */       System.out.println("i=" + i);
/* 227 */       sb.append(s.substring(0, i));
/* 228 */       System.out.println("sb=" + sb.toString());
/* 229 */       s = s.substring(i + pattern.length());
/* 230 */       System.out.println("s =" + s);
/* 231 */       i = s.indexOf(pattern);
/*     */     }
/* 233 */     sb.append(s);
/* 234 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static String replaceInString(String s, String a, String b)
/*     */   {
/* 245 */     int previousIndex = -1;
/*     */ 
/* 247 */     while (s.indexOf(a) > previousIndex)
/*     */     {
/* 249 */       int i = s.indexOf(a);
/*     */ 
/* 253 */       previousIndex = i + b.length();
/*     */ 
/* 255 */       s = s.substring(0, i) + b + s.substring(i + a.length());
/*     */     }
/* 257 */     return s;
/*     */   }
/*     */ 
/*     */   public static String[] split(String separator, String in)
/*     */   {
/* 269 */     Vector result = new Vector();
/* 270 */     int i = 0;
/*     */ 
/* 272 */     while (i < in.length()) {
/* 273 */       int oldi = i;
/* 274 */       i = in.indexOf(separator, i);
/*     */ 
/* 276 */       if (i == -1) {
/* 277 */         result.addElement(in.substring(oldi, in.length()));
/* 278 */         return vector2stringArray(result);
/*     */       }
/*     */ 
/* 281 */       result.addElement(in.substring(oldi, i));
/* 282 */       i += separator.length();
/*     */     }
/*     */ 
/* 286 */     result.addElement("");
/* 287 */     return vector2stringArray(result);
/*     */   }
/*     */ 
/*     */   public static Vector stringArray2vector(Object[] a)
/*     */   {
/* 295 */     Vector v = new Vector();
/*     */ 
/* 297 */     for (int i = 0; i < a.length; ++i) {
/* 298 */       v.addElement(a[i]);
/*     */     }
/* 300 */     return v;
/*     */   }
/*     */ 
/*     */   public static Vector stringArray2vector(String[] a)
/*     */   {
/* 310 */     Vector v = new Vector();
/*     */ 
/* 312 */     for (int i = 0; i < a.length; ++i) {
/* 313 */       v.addElement(a[i]);
/*     */     }
/* 315 */     return v;
/*     */   }
/*     */ 
/*     */   public static String[] vector2stringArray(Vector v)
/*     */   {
/* 325 */     String[] a = new String[v.size()];
/*     */ 
/* 327 */     for (int i = 0; i < v.size(); ++i) {
/* 328 */       a[i] = ((String)v.elementAt(i));
/*     */     }
/* 330 */     return a;
/*     */   }
/*     */ 
/*     */   public static String removeSpacesBlock(String s)
/*     */   {
/* 340 */     char[] sArray = new char[s.length()];
/* 341 */     char prev = '\000';
/* 342 */     int c = 0;
/*     */     try
/*     */     {
/* 345 */       for (int i = 0; i < s.length(); ++i)
/*     */       {
/* 347 */         char current = s.charAt(i);
/*     */ 
/* 349 */         if ((current == ' ') || (current == '\t')) {
/* 350 */           if (prev != ' ') {
/* 351 */             prev = ' ';
/* 352 */             sArray[c] = ' ';
/* 353 */             ++c;
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 358 */           sArray[c] = current;
/* 359 */           prev = '\000';
/* 360 */           ++c;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 365 */       System.out.println("-- error :" + e);
/*     */     }
/* 367 */     return new String(sArray, 0, c);
/*     */   }
/*     */ 
/*     */   public static String getValueQueryString(String queryString, String key)
/*     */     throws Exception
/*     */   {
/* 379 */     String value = null;
/* 380 */     String[] parameters = queryString.split("&");
/*     */ 
/* 382 */     for (int i = 0; i < parameters.length; ++i)
/*     */     {
/* 385 */       int posEqual = parameters[i].indexOf("=");
/* 386 */       if (posEqual <= -1)
/*     */         continue;
/* 388 */       String keyI = parameters[i].substring(0, posEqual);
/* 389 */       keyI = removeHeadingSpaces(removeTrailingSpaces(keyI));
/*     */ 
/* 391 */       if (keyI.compareToIgnoreCase(key) != 0)
/*     */         continue;
/* 393 */       value = parameters[i].substring(posEqual + 1, parameters[i].length());
/* 394 */       value = removeHeadingSpaces(removeTrailingSpaces(value));
/*     */     }
/*     */ 
/* 399 */     return value;
/*     */   }
/*     */ 
/*     */   public static byte[] hexaToBytes(String s)
/*     */   {
/* 410 */     byte[] b = new byte[s.length() / 2];
/* 411 */     byte[] tmp = new BigInteger(s, 16).toByteArray();
/*     */ 
/* 413 */     System.arraycopy(tmp, 
/* 414 */       Math.max(0, tmp.length - b.length), 
/* 415 */       b, 
/* 416 */       Math.max(0, b.length - tmp.length), 
/* 417 */       b.length);
/* 418 */     return b;
/*     */   }
/*     */ }

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.StringUtil
 * JD-Core Version:    0.5.4
 */