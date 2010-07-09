package com.atosorigin.services.rd.common.util;

public abstract interface SessionListener
{
  public abstract void listen(SessionEvent paramSessionEvent);

  public abstract void removingFromListenerList();
}

/* Location:           D:\SharedZone\decompile\awlcommon-1.jar
 * Qualified Name:     com.atosorigin.services.rd.common.util.SessionListener
 * JD-Core Version:    0.5.4
 */