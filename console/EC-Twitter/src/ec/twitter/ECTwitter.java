/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.twitter;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 *
 * @author crojas
 */
public class ECTwitter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //get the current jar location
        String iniFile = ECTwitter.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        iniFile = (iniFile.replaceAll("EC-Twitter.jar", "INI.ini"));
        iniFile = iniFile.substring(1, iniFile.length());
       
        String message;
        
        if(args.length == 0){
            printUsage();
            return;
        }
        message = args[0].toString();
        if(args.length > 1){
            iniFile = args[1].toString();
        }
        if(message.length() > 140){
            System.out.println("Update messages should have less than 140 characters");
            return;
        }
        ECTwitterDriver driver = new ECTwitterDriver();
        try {
            driver.updateStatus(message, iniFile);
            System.out.println("The tweet was tweeted successfully.");
        } catch (MalformedURLException ex) {
            System.out.println(ECTwitter.class.getName() + ":" + ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ECTwitter.class.getName() + ":" + ex.getMessage());
        }   
    }
    
    public static void printUsage(){
        System.out.println("EC-Twitter version 1.0.0");
        System.out.println("Usage: java -jar EC-Twitter.jar message [configuration file]");
    }
}
