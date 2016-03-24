/*
 * Created on 21-Jan-2005
 */
package uk.ac.standrews.cs.util;

import java.io.PrintStream;
import java.text.DecimalFormat;

/**
 * Output class with suppport for singleton and redirection of I/O stream elsewhere
 * Abstracts over output streams
 * TODO - Graham this is a job for you - we need a witch hunt for System.out.println :)
 * 
 * @author stuart
 */
public class Output {

	public static String DECIMAL_FORMAT_STRING = "#.###";
	public static String DEFAULT_SEPARATOR = "\t";
	private DecimalFormat df;
	private String separator;
	private PrintStream printStream;
	private static Output instance = null;
	
	public Output(){
		df=new DecimalFormat(DECIMAL_FORMAT_STRING);
		separator=DEFAULT_SEPARATOR;
		printStream = System.out;
	}
	
	public static Output getSingleton() {
	    if( instance == null ) {
	        instance = new Output();
	    }
	    return instance;
	}
	
	public void println( String s ) {
		printStream.println(s);	    
	}
	
	public void print( String s ) {
		printStream.println(s);	    
	}	
	
	public void println(double d) {
		printStream.println(d);	    
	}
	
	public void printlnSeparated(String s1, String s2){
		printStream.print(s1);
		printStream.print(separator);
		printStream.print(s2);
		printStream.println();
	}
	
	public void printlnSeparated(String s1, double d1){
		printStream.print(s1);
		printStream.print(separator);
		printStream.print(df.format(d1));
		printStream.println();
	}
	
	public void printlnSeparated(int s, double d) {
		printStream.print(s);
		printStream.print(separator);
		printStream.print(df.format(d));
		printStream.println();
	}

	
	public void printlnSeparated(String s1, int i1){
		printStream.print(s1);
		printStream.print(separator);
		printStream.print(i1);
		printStream.println();
	}

	
	public void printlnSeparated(String s1, String s2, String s3, String s4, String s5, String s6) {
		printStream.print(s1);
		printStream.print(separator);
		printStream.print(s2);
		printStream.print(separator);
		printStream.print(s3);
		printStream.print(separator);
		printStream.print(s4);
		printStream.print(separator);
		printStream.print(s5);
		printStream.print(separator);
		printStream.print(s6);
		printStream.println();
	}
	
	public void printlnSeparated(String s1, String s2, String s3, String s4, String s5, String s6, String s7) {
		printStream.print(s1);
		printStream.print(separator);
		printStream.print(s2);
		printStream.print(separator);
		printStream.print(s3);
		printStream.print(separator);
		printStream.print(s4);
		printStream.print(separator);
		printStream.print(s5);
		printStream.print(separator);
		printStream.print(s6);
		printStream.print(separator);
		printStream.print(s7);
		printStream.println();
	}
	
	public void printlnSeparated(int i1, double d1, double d2, double d3, double d4, double d5) {
		printStream.print(i1);
		printStream.print(separator);
		printStream.print(df.format(d1));
		printStream.print(separator);
		printStream.print(df.format(d2));
		printStream.print(separator);
		printStream.print(df.format(d3));
		printStream.print(separator);
		printStream.print(df.format(d4));
		printStream.print(separator);
		printStream.print(df.format(d5));
		printStream.println();
	}	
	
	public void printlnSeparated(int i1, double d1, double d2, double d3, double d4, double d5 , double d6) {
		printStream.print(i1);
		printStream.print(separator);
		printStream.print(df.format(d1));
		printStream.print(separator);
		printStream.print(df.format(d2));
		printStream.print(separator);
		printStream.print(df.format(d3));
		printStream.print(separator);
		printStream.print(df.format(d4));
		printStream.print(separator);
		printStream.print(df.format(d5));
		printStream.print(separator);
		printStream.print(df.format(d6));
		printStream.println();
	}	
	
    /**
     * @return Returns the out.
     */
    public PrintStream getPrintStream() {
        return printStream;
    }
    /**
     * @param out The out to set.
     */
    public void setPrintStream(PrintStream out) {
        this.printStream = out;
    }
}
