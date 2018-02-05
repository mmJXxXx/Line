package com.line;
/**
 对POST所获数据进行解码与调试
 */
public class urldispose {
    private static String ascii2native(String asciicode) {
        String[] asciis = asciicode.split( "\\\\u" );
        String nativeValue = asciis[0];
        try {
            for (int i = 1; i < asciis.length; i++) {
                String code = asciis[i];
                nativeValue += (char) Integer.parseInt( code.substring( 0, 4 ), 16 );
                if (code.length() > 4) {
                    nativeValue += code.substring( 4, code.length() );
                }
            }
        } catch (NumberFormatException e) {
            return asciicode;
        }
        return nativeValue;
    }

    static String geturlback(String urlback) {
        if (!urlback.equals("")) {
        String s = urlback.substring( urlback.indexOf(","));
        s=s.substring(1);
        s=s.substring( s.indexOf( "," ) );
        s=s.substring(1);
        s=s.substring(1);
        s=s.substring(1);
        s=s.substring( 0,s.length()-2);
        s=ascii2native( s );
            return s;
        } else {
            return urlback;
        }
        }
}

