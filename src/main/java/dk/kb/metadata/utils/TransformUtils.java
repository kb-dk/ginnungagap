package dk.kb.metadata.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transformation utilities for MODS transformations.
 * Extracted from deprecate COP project.
 * 
 *******************************************************
 * 
 * Utility klasse der skal hjælpe med at parse data
 * fra cumulus, og skabe mods objekter. Hvis nogen regler
 * ikke er overholdt logges dette. Logmeddelelen skal 
 * kunne forstås af lægmand. 
 * 
 * Følgende regler skal understøttes og skal behandles:
 * <ul>
 *  <li>sprog:værdi</li>
 *  <li>&lt;&lt;the>> Golden Gun</li>
 *  <li>&lt;&lt;Golden=Shiny>> Gun (Kun eenpr. felt understøttes. </li>
 *  <li>Sprog skal overholde RFC4646</li>
 * </ul>
 * @author jac
 */
public class TransformUtils {
    /** The logger.*/
    private static final Logger log = LoggerFactory.getLogger(TransformUtils.class);
    /** The language separator for descriptive metadata.*/
    private static final String LANG_SEPARATOR = "|";

    /** Whether it is a non-transliteration.*/
    protected static final int IS_NON_TRANSLITERATION = 0;
    /** Whether it is a transliteration.*/
    protected static final int IS_TRANSLITERATION_REX = 1;
    /** Unknown.*/
    protected static final int IS_RSS = 2;
    
    /**
     * Dummy constructor, for at tilfredsstille checkstyle :-(.
     */
    protected TransformUtils(){}

    /**
     * 
     * @param val Cumulus raw xml value felt.
     * @return Værdien, med cumulus regler overholdt.
     */
    public static String getCumulusVal(String val){       
        if(val.indexOf(LANG_SEPARATOR) > -1){
            return applyRules(val.substring(val.indexOf(LANG_SEPARATOR) + 1, val.length()), 
                    IS_NON_TRANSLITERATION).trim();
        } else {
            return applyRules(val, IS_NON_TRANSLITERATION).trim();
        }    
    }

    /**
     * @param val The field value.
     * @return The field value, without the language prefix.
     */
    public static String getCumulusSimpleVal(String val){
        if(val.indexOf(LANG_SEPARATOR) > -1){
            return applyRules(val.substring(val.indexOf(LANG_SEPARATOR) + 1, val.length()), 
                    IS_RSS).trim();
        } else {
            return applyRules(val, IS_RSS).trim();
        }     
    }

    /**
     * 
     * @param val cumulus raw xml value felt.
     * @return true hvis &lt;&lt;the>> eksisterer ellers false.
     */
    public static boolean isCumulusValNonSort(String val){
        return val.matches("^.*<<[^=]*>>.*$");
    }


    /**
     * 
     * @param val Cumulus raw xml value felt.
     * @return Værdien, med cumulus regler overholdt.
     */
    public static String getCumulusValNonSort(String val){       
        if(val.matches("^.*<<[^=]*>>.*$")){
            return val.substring(val.indexOf("<<")+2, val.indexOf(">>"));
        } else {
            return val;
        }    
    }

    /**
     * 
     * @param val Cumulus raw xml value felt.
     * @return true hvis &lt;&lt;a=b>> eksisterer ellers false.
     */
    public static Boolean isCumulusValTranslit(String val){
        return val.matches("^.*<<.*=.*$");
    }

    /**
     * forudsætter at der er blevet testet med isCumulusValSubject.
     * @param val Cumulus raw xml value felt.
     * @return Det der står efter = i "&lt;&lt;a=b>>"
     */
    public static String getCumulusValTranslit(String val){
        if(val.indexOf(LANG_SEPARATOR)>-1){
            return applyRules(val.substring(val.indexOf(LANG_SEPARATOR)+1
                    , val.length()), IS_TRANSLITERATION_REX).trim();
        } else {
            return applyRules(val, IS_TRANSLITERATION_REX).trim();
        }
    }

    /**
     * 
     * @param val Streng der skal parses.
     * @param transliteration int der beskriver hvilken regel der skal 
     *  parses efter.
     * @return Parset streng
     */
    public static String applyRules(String val, int transliteration){
        if(val.matches("^.*<<[^=]*>>.*$") && transliteration != IS_RSS){
            val = val.replaceAll("<<[^=]*>>", "");
        }
        if (val.matches("^.*<<.*=.*$")) {
            switch (transliteration){
            case IS_NON_TRANSLITERATION: 
                val = val.replaceAll("<<", "").replaceAll("=.*>>", "");
                break;
            case IS_RSS: // Same as below.
            case IS_TRANSLITERATION_REX: 
                val = val.replaceAll(">>", "").replaceAll("<<.*=", "");
                break;
            default:
                break;
            }
        }
        return val;          
    }

    /**
     * 
     * @param val Cumulus raw xml value felt.
     * @param defaultLang sprog værdi der skal bruges hvis der ikke er :
     * @return rfc4646 Sprogkode
     */
    public static String getCumulusLang(String val, String defaultLang){
        if(val.indexOf(LANG_SEPARATOR)>-1){
            String langCode = val.substring(0, val.indexOf(LANG_SEPARATOR));  
            if(langCode.matches(
                    "^[a-z][a-z][a-z]?(-[a-z]{4}(-[1-9]{3}|-[a-zA-Z]{2})?)?$")){
                return langCode;
            } else{
                log.warn("LanguageCode '" + langCode + "' is not rfc4646");
                return defaultLang;
            }
        } else {
            if(defaultLang.matches("^[a-z][a-z][a-z]?(-[a-z]{4}(-[1-9]{3}|-[a-zA-Z]{2})?)?$")){
                return defaultLang;
            } else {
                log.warn("LanguageCode '" + defaultLang + "' is not rfc4646");
                return defaultLang;
            }
        }    
    }

    /**
     * @param val The field value.
     * @param lang The language.
     * @param defLang The defined language.
     * @return Whether or not???
     */
    public static boolean isTocTitle(String val, String lang, String defLang){
        if(val.indexOf(LANG_SEPARATOR)>-1){
            if(val.startsWith(lang + LANG_SEPARATOR)){
                return true;
            }
            return false;
        } else if (lang.equals(defLang)){
            return true;
        } 
        return false;

    }

    /**
     * Retrieves the date in ISO format.
     * @param informat The input format.
     * @param outformat The output format.
     * @param indate The text date in the input format.
     * @return The date in the output format.
     */
    public static String getIsoDate(String informat, String outformat, String indate) {
        SimpleDateFormat informatter = new SimpleDateFormat(informat);
        Date date = null;
        try {
            date = informatter.parse(indate);
        } catch(ParseException parseError) {
            log.warn("Couldn't parse date", parseError);
        }
        if(date != null) {
            SimpleDateFormat outformatter = new SimpleDateFormat(outformat);
            return outformatter.format(date);
        } else {
            return "0000-00-00";
        }
    }

    /**
     * @param val The field value.
     * @param lang The language, if any.
     * @return The value, with the potential language removed.
     */
    public static String getTocTitle(String val, String lang) {
        if(val.startsWith(lang + LANG_SEPARATOR)){
            return val.substring(val.indexOf(LANG_SEPARATOR)+1,val.length());
        } else {   
            return val;
        }
    }
}

