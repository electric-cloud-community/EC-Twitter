/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.twitter;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;
import java.io.IOException;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author crojas
 */
public class ECTwitterRestClient {

    private WebResource webResource;
    private Client client;
    private static final String BASE_URI = "http://twitter.com";
    private static final String OAUTH_BASE_URL = "http://twitter.com/oauth";
    /**
     * Please, specify the consumer_key string obtained from service API pages
     */
    private static final String CONSUMER_KEY = "le0Sj5cpPQQUNbJ8JHoYQ";
    /**
     * Please, specify the consumer_secret string obtained from service API pages
     */
    private static final String CONSUMER_SECRET = "vLN55OU7GCcBPsHmVZFsZEyfp7YkOEYeTo6Kfr0ukVs";
    private OAuthParameters oauth_params;
    private OAuthSecrets oauth_secrets;
    private OAuthClientFilter oauth_filter;
    private String oauth_access_token;
    private String oauth_access_token_secret;
    
    public ECTwitterRestClient(String format) {
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = Client.create(config);
        String resourcePath = java.text.MessageFormat.format("statuses/update.{0}", new Object[]{format});
        webResource = client.resource(BASE_URI).path(resourcePath);
        oauth_access_token_secret = "";
        oauth_access_token = "";
    }
    
    public void setResourcePath(String format) {
        String resourcePath = java.text.MessageFormat.format("statuses/update.{0}", new Object[]{format});
        webResource = client.resource(BASE_URI).path(resourcePath);
    }

    /**
     * @param responseType Class representing the response
     * @param status form parameter
     * @param in_reply_to_status_id form parameter
     * @return response object (instance of responseType class)
     */
    public <T> T updateStatus(Class<T> responseType, String status, String in_reply_to_status_id) throws UniformInterfaceException {
        String[] formParamNames = new String[]{"status", "in_reply_to_status_id"};
        String[] formParamValues = new String[]{status, in_reply_to_status_id};
        return webResource.type(javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED).post(responseType, getQueryOrFormParams(formParamNames, formParamValues));
    }

    private MultivaluedMap getQueryOrFormParams(String[] paramNames, String[] paramValues) {
        MultivaluedMap<String, String> qParams = new com.sun.jersey.api.representation.Form();
        for (int i = 0; i < paramNames.length; i++) {
            if (paramValues[i] != null) {
                qParams.add(paramNames[i], paramValues[i]);
            }
        }
        return qParams;
    }

    public void close() {
        client.destroy();
    }

    /**
     * 
     * You need to call this method at the beginning to authorize the application to work with user data.
     * The method obtains the OAuth access token string, that is appended to each API request later.
     *
     * @param ini Name of the ini file
     * @throws IOException
     * @throws UniformInterfaceException 
     */
    public void login(String ini) throws IOException, UniformInterfaceException {
        IniHandler readIni = new IniHandler(ini);
        oauth_access_token_secret = readIni.getValue("tokensecret");
        oauth_access_token = readIni.getValue("token");
        if((oauth_access_token_secret == null)||(oauth_access_token == null)||(oauth_access_token_secret.isEmpty())||(oauth_access_token.isEmpty())){
            Form requestTokenResponse = getOAuthRequestToken();
            String oauth_verifier = authorizeConsumer(requestTokenResponse);
            Form accessTokenResponse = getOAuthAccessToken(requestTokenResponse, oauth_verifier);
            oauth_access_token_secret = accessTokenResponse.getFirst("oauth_token_secret");
            oauth_access_token = accessTokenResponse.getFirst("oauth_token");
            readIni.setValue("tokensecret", oauth_access_token_secret);
            readIni.setValue("token", oauth_access_token);
        }
    }

    private Form getOAuthRequestToken() throws UniformInterfaceException {
        WebResource resource = client.resource(OAUTH_BASE_URL).path("request_token");
        oauth_params = new OAuthParameters().consumerKey(CONSUMER_KEY).signatureMethod(com.sun.jersey.oauth.signature.HMAC_SHA1.NAME).version("1.0").nonce().timestamp();
        oauth_secrets = new OAuthSecrets().consumerSecret(CONSUMER_SECRET);
        oauth_filter = new OAuthClientFilter(client.getProviders(), oauth_params, oauth_secrets);
        resource.addFilter(oauth_filter);
        return resource.get(Form.class);
    }

    private Form getOAuthAccessToken(Form requestTokenResponse, String oauth_verifier) throws UniformInterfaceException {
        WebResource resource = client.resource(OAUTH_BASE_URL).path("access_token");
        oauth_params.token(requestTokenResponse.getFirst("oauth_token")).signatureMethod(com.sun.jersey.oauth.signature.HMAC_SHA1.NAME).version("1.0").nonce().timestamp().verifier(oauth_verifier);
        oauth_secrets.tokenSecret(requestTokenResponse.getFirst("oauth_token_secret"));
        resource.addFilter(oauth_filter);
        return resource.get(Form.class);
    }

    /**
     * The method sets the OAuth parameters for webResource.
     * The method needs to be called after login() method, or when the webResource path is changed
     */
    public void initOAuth() {
        oauth_params = new OAuthParameters().consumerKey(CONSUMER_KEY).token(oauth_access_token).signatureMethod(com.sun.jersey.oauth.signature.HMAC_SHA1.NAME).version("1.0").nonce().timestamp();
        oauth_secrets = new OAuthSecrets().consumerSecret(CONSUMER_SECRET).tokenSecret(oauth_access_token_secret);
        oauth_filter = new OAuthClientFilter(client.getProviders(), oauth_params, oauth_secrets);
        webResource.addFilter(oauth_filter);
    }

    /**
     * The method increases OAuth nonce and timestamp parameters to make each request unique.
     * The method should be called when repetitive requests are sent to service API provider:
     * <pre>
     * client.initOauth();
     * client.getXXX(...);
     * client.makeOAuthRequestUnique();
     * client.getYYY(...);
     * client.makeOAuthRequestUnique();
     * client.getZZZ(...);
     * </pre>
     */
    public void makeOAuthRequestUnique() {
        if (oauth_params != null) {
            oauth_params.nonce().timestamp();
        }
    }

    private java.lang.String authorizeConsumer(Form requestTokenResponse) throws IOException {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI("http://twitter.com/oauth/authorize?oauth_token=" + requestTokenResponse.getFirst("oauth_token")));
        } catch (java.net.URISyntaxException ex) {
            ex.printStackTrace();
        }
        java.io.BufferedReader br = null;
        String oauth_verifier = null;
        try {
            br = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
            System.out.print("Type oauth_verifier string (taken from callback page url):");
            oauth_verifier = br.readLine();
        } finally {
            br.close();
        }
        return oauth_verifier;
    }
}
    
