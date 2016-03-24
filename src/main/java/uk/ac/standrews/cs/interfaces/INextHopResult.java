/*
 * Created on 23-Jun-2005
  */
package uk.ac.standrews.cs.interfaces;

import uk.ac.stand.dcs.asa.applicationFramework.impl.P2PNodeException;

/**
 * @author stuart
 */
public interface INextHopResult {
    public static final int ERROR = -1;
    public static final int FINAL = 0;
	public static final int NEXT_HOP = 1;
	public static final int INTERCEPT = 2;
    public static final int ROOT = 3;
	
	public int getCode();
	public String getAppObjectName();
	public void setAppObjectName(String appObjectName, boolean root);
	public boolean isError();
	public P2PNodeException getError();
	public void setError(String errorMsg);
}
