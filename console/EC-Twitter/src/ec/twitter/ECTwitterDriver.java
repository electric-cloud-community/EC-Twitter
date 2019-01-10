/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.twitter;

import com.sun.jersey.api.client.UniformInterfaceException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 *
 * @author crojas
 */
public class ECTwitterDriver {
    private ECTwitterRestClient client;
    
    public void updateStatus(String message, String iniFileName) throws MalformedURLException, IOException{
        //Create an instance of the internal service class
        client = new ECTwitterRestClient("xml");


        //Log in, get tokens, and append the tokens to the consumer and secret
        //keys
        client.login(iniFileName);
        client.initOAuth();

        client.makeOAuthRequestUnique();
        try {
            client.updateStatus(String.class, message, null);
        } catch(UniformInterfaceException ex) {
            System.out.println("Exception when calling updateStatus = " + ex.getResponse().getEntity(String.class));
        }
    }

}
