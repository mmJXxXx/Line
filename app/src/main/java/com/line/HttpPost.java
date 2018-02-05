package com.line;

/**
    HTTP POST 传输类（基本格式）
*/
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpPost {
    public static String sendHttpPost(String address,String ask) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL( address );
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput( true );
            connection.setDoInput( true );
//            connection.setUseCaches( false );
            connection.setRequestMethod( "POST" );
//            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");

            connection.setConnectTimeout( 1000 );
            connection.setReadTimeout( 1000 );
            PrintWriter out = new PrintWriter( connection.getOutputStream() );
            out.print( ask );
            out.flush();
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append( line );
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Network Error";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
