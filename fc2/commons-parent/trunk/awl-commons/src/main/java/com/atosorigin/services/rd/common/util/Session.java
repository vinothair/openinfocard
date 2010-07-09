package com.atosorigin.services.rd.common.util;

import java.util.Enumeration;
import java.util.GregorianCalendar;

public abstract interface Session
{
  public abstract String getId();

  public abstract GregorianCalendar getCreationTime();

  public abstract GregorianCalendar getLastAccessedTime();

  public abstract void setLastAccessedTime(GregorianCalendar paramGregorianCalendar);

  public abstract Object getAttribute(String paramString);

  public abstract Enumeration getAttributeNames();

  public abstract void setAttribute(String paramString, Object paramObject);

  public abstract void removeAttribute(String paramString);

  public abstract void setFlushingDelay(int paramInt);

  public abstract int getFlushingDelay();
}

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.Session
 * JD-Core Version:    0.5.4
 */