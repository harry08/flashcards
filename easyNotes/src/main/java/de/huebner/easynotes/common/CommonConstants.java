package de.huebner.easynotes.common;

/**
 * Common constants
 */
public class CommonConstants {

    public static final String FIELD_FRONTTEXT = "fronttext";
    public static final String FIELD_BACKTEXT = "backtext";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_TEXT = "text";
    
    /**
     * Exportmode Fronttext - Backtext - Description
     */
    public static final int EXPORTTYPE_STANDARD = 1;

    /**
     * Exportmode Fronttext - Backtext
     */
  	public static final int EXPORTTYPE_FRONTBACK = 2;

  	/**
     * Exportmode Backtext - Fronttext
     */
  	public static final int EXPORTTYPE_BACKFRONT = 3;

  	/**
     * Exportmode Fronttext - Backtext and Description
     */
  	public static final int EXPORTTYPE_FRONT_BACKDESC = 4;

    /**
     * constant for double-quote
     */
    public static final String DOUBLE_QUOTE = "\"";

    /**
     * constant for a double-quote that is masked.
     */
    public static final String MASEKD_DOUBLE_QUOTE = "\"\"";

    /**
     * constant for semicolon
     */
    public static final String SEMICOLON = ";";

    /**
     * constant for linefeed
     */
    public static final String END_OF_LINE = "\n";

    /**
     * constant for carriage return
     */
    public static final String CARRIAGE_RETURN = "\r";
}
