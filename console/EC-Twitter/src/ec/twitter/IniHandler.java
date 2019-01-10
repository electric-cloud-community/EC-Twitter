/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.twitter;

import java.util.*;
import java.io.*;

/**
 *
 * @author crojas
 */
public class IniHandler {
    String fileName;
    public IniHandler(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * get and return the specified value from the config file
     * @param key - name of the value you want to retrieve
     * @return the specified value
     */
    public String getValue(String key){
        String value = "";
        try{
            Properties p = new Properties();
            p.load(new FileInputStream(this.fileName));
            value = p.getProperty(key);
        }
        catch (Exception e) {
        }
        return value;
    }
    
    /**
     * sets a value inside the config file
     * @param key - name of the value you want to set
     * @param value - value to store
     */
    public void setValue(String key, String value){
        try{
            //check the file existence
            File file = new File(this.fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            Properties p = new Properties();
            p.load(new FileInputStream(this.fileName));
            p.setProperty(key, value);
            p.store(new FileOutputStream(this.fileName),"");
        }
        catch (Exception e) {
            System.out.println("Unable to set values inside the config file: " + this.fileName );
        }
    }
}
