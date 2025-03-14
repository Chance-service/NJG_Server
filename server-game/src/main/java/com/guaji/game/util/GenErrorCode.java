package com.guaji.game.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.guaji.os.MyException;

public class GenErrorCode {

	public static void main(String[] args) {
		System.err.println("ID" + "\t" + "提示文字");
		
		String path = System.getProperty("user.dir") + "/Status.proto";
		
		if(args.length > 0) {
			path = args[0];
		}
		
		File file   = new File(path);
        try {
        	String lineText = "";
        	String lastText = "";
        	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        	String content = "";
        	 while ((lineText = reader.readLine()) != null) {
        		 int pos = lineText.indexOf('=');
        		 if (pos >= 0) {
        			 String code = lineText.substring(pos+1).replace(';', ' ').trim();
        			 try {
	        			 if (code.length() > 0 && Integer.valueOf(code) > 0 && lastText != null) {
	        				 int begin = lastText.indexOf("//");
	        				 if (begin >= 0) {
	        					 lastText = lastText.substring(begin + 2).trim();
	        				 }
	        				 System.err.println(code + "\t" + lastText);
	        				 content += (code + "\t" + lastText + "\n");
	        			 }
        			 } catch (Exception e) {
        			 }
        		 }
        		 lastText = lineText;
             }
             reader.close();
             write(content, System.getProperty("user.dir") + "/ErrorCode.txt", "UTF-8");
        } catch (Exception e) {
        	MyException.catchException(e);
        }
	}
	
	public static void write(String fileContent, String fileName,  
            String encoding) {  
        OutputStreamWriter osw = null;  
        try {  
            osw = new OutputStreamWriter(new FileOutputStream(fileName), encoding);  
            osw.write(fileContent);  
            osw.flush();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        finally{  
            if(osw!=null)  
                try {  
                    osw.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
        }  
    }  
}
