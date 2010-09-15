package ymconnectionserver.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONStringer;
import net.sf.json.util.JSONTokener;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.RandomStringUtils;

import ymconnectionserver.exceptions.NotOkResponseException;

public class YMUtils 
{
	protected static String YM_GET_PART_URL = "https://login.yahoo.com/WSLogin/V1/get_auth_token";
	protected static String YM_GET_TOKEN_URL = "https://api.login.yahoo.com/oauth/v2/get_token";
	protected static String YM_SESSION_URL = "http://developer.messenger.yahooapis.com/v1/session";
	
	public static final String PARAM_LOGIN = "login";
	public static final String PARAM_PASSWD = "passwd";	
	public static final String PARAM_OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
	public static final String PARAM_OAUTH_CONSUMER_KEY = "oauth_consumer_key";
	public static final String PARAM_OAUTH_TIMESTAMP = "oauth_timestamp";
	public static final String PARAM_OAUTH_NONCE = "oauth_nonce";
	public static final String PARAM_OAUTH_VERSION = "oauth_version";
	public static final String PARAM_FIELDS_BUDDY_LIST = "fieldsBuddyList";
	public static final String PARAM_OAUTH_SESSION_HANDLE = "oauth_session_handle";
	public static final String PARAM_OAUTH_TOKEN = "oauth_token";
	public static final String PARAM_OAUTH_SIGNATURE = "oauth_signature";
	public static final String PARAM_OAUTH_TOKEN_SECRET = "oauth_token_secret";
	public static final String PARAM_CRUMB = "c";

	public static String getYMPART(String login, String passwd, String consumerKey) throws HttpException, IOException, NotOkResponseException
	{       
        Map<String, String> map = new HashMap<String, String>();
        map.put(PARAM_LOGIN,login);
        map.put(PARAM_PASSWD,passwd);
        map.put(PARAM_OAUTH_CONSUMER_KEY,consumerKey);
                
		HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(generateURL(YM_GET_PART_URL,map));

        
        // Send POST request
        int statusCode = client.executeMethod(method);
        
        if (statusCode != HttpStatus.SC_OK) {
        	System.err.println("Call to YM server to get PART failed");
        	throw new NotOkResponseException("Call to YM server to get PART failed");
        }
                
        // Process the response from Yahoo! Web Services        
        BufferedReader br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
        String line;
        String requestToken="";
        while ((line = br.readLine()) != null) {
            requestToken = requestToken+line;
        }
        
        br.close();
        
        requestToken=requestToken.split("=")[1];
        
        return requestToken;		
	}
	
	public static InputStream getYMToken(String part, String consumerKey, String consumerSecretKey) throws HttpException, IOException, NotOkResponseException
	{
        Map<String, String> map = getCommonParametersMap(consumerKey);        
        
        map.put(PARAM_OAUTH_SIGNATURE,consumerSecretKey+"%26");        
        map.put(PARAM_OAUTH_TOKEN,part);

		HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(generateURL(YM_GET_TOKEN_URL,map));


        int statusCode = client.executeMethod(method);
        
        if (statusCode != HttpStatus.SC_OK) {
        	System.err.println("Call to YM server to get token failed");
        	throw new NotOkResponseException("Call to YM server to get token failed");
        }
        
        return method.getResponseBodyAsStream();
	}
	
	public static InputStream refreshYMToken(String token, String sessionHandle, String authTokenSecret, String consumerKey, String consumerSecretKey) throws HttpException, IOException, NotOkResponseException
	{
		Map<String, String> map = getCommonParametersMap(consumerKey);
        
		map.put(PARAM_OAUTH_SIGNATURE,consumerSecretKey+"%26"+authTokenSecret);        
		map.put(PARAM_OAUTH_TOKEN,token);
		map.put(PARAM_OAUTH_SESSION_HANDLE,sessionHandle);
        
		HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(generateURL(YM_GET_TOKEN_URL,map));
        
        int statusCode = client.executeMethod(method);
        
        if (statusCode != HttpStatus.SC_OK) {
        	System.err.println("Call to YM server to refresh token failed");
        	throw new NotOkResponseException("Call to YM server to refresh token failed");
        }
        
        return method.getResponseBodyAsStream();		
	}
		
	public static InputStream getLoginSession(String token, String authTokenSecret, String consumerKey, String consumerSecretKey) throws HttpException, IOException, NotOkResponseException
	{	
		Map<String, String> map = getCommonParametersMap(consumerKey);
                
		map.put(PARAM_FIELDS_BUDDY_LIST, "%2Bgroups");
		map.put(PARAM_OAUTH_TOKEN, token);
		map.put(PARAM_OAUTH_SIGNATURE, consumerSecretKey+"%26"+authTokenSecret);
        
		HttpClient client = new HttpClient();
        GetMethod getMethod = new GetMethod(generateURL(YM_SESSION_URL,map));

        getMethod.addRequestHeader("Content-Type", "application/json;charset=utf-8");
        
        int statusCode = client.executeMethod(getMethod);
        
	        
        if (statusCode != HttpStatus.SC_OK) {
        	System.err.println("Call to YM server to get a session failed");
        	throw new NotOkResponseException("Call to YM server to get a session failed");
        }
        
        String crumbResponse = getStringFromStream(getMethod.getResponseBodyAsStream());
        
        org.json.JSONObject jsonObj =null;
        
        try {
			jsonObj = new org.json.JSONObject(crumbResponse);
			
		} catch (ParseException e) {
        	System.err.println("Call to YM server to get a session failed");
        	throw new NotOkResponseException("Call to YM server to get a session failed");
		}
		
		map.put(PARAM_CRUMB, (String) jsonObj.get("crumb"));
		
        PostMethod postMethod = new PostMethod(generateURL(YM_SESSION_URL,map));

        postMethod.addRequestHeader("Content-Type", "application/json;charset=utf-8");
        
        RequestEntity entity = new StringRequestEntity("{}", "application/json", "utf-8");
        postMethod.setRequestEntity(entity);
        
        statusCode = client.executeMethod(postMethod);
        
	        
        if (statusCode != HttpStatus.SC_OK) {
        	System.err.println("Call to YM server to refresh token failed");
        	throw new NotOkResponseException("Call to YM server to get a session failed");
        }
		
        
        return postMethod.getResponseBodyAsStream();
        
        		
		
	}
	
	private static Map<String, String> getCommonParametersMap(String consumerKey)
	{
		Map<String,String> map = new HashMap<String, String>();
		
		return getCommonParametersMap(map,consumerKey);
	}
	
	private static Map<String, String> getCommonParametersMap(Map<String, String> map, String consumerKey)
	{
        map.put(PARAM_OAUTH_VERSION,"1.0");
        map.put(PARAM_OAUTH_NONCE,RandomStringUtils.randomAlphanumeric(128));
        map.put(PARAM_OAUTH_TIMESTAMP,""+System.currentTimeMillis()/1000);
        map.put(PARAM_OAUTH_CONSUMER_KEY,consumerKey);
        map.put(PARAM_OAUTH_SIGNATURE_METHOD,"PLAINTEXT");
        
        return map;
	}
	
	private static String generateURL(String baseURL, Map<String, String> keyValuePairs)
	{
		baseURL = baseURL + "?";
		for(Entry<String, String> entry : keyValuePairs.entrySet())
		{
			baseURL = baseURL + entry.getKey()+"="+entry.getValue()+"&";
		}
		
		//Strip off the last &
		return baseURL.substring(0,baseURL.length()-1);		
	}
	
	public static String getStringFromStream(InputStream stream) throws IOException
	{
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line;
        String str="";
        while ((line = br.readLine()) != null) {
        	str = str+line;
        }
        
        br.close();
        
        return str;

	}


}
