package ymconnectionserver.actions;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.json.JSONObject;

import ymconnectionserver.utils.YMUtils;

public class LoginAction extends AbstractActionBase
{
	private String login;
	private String passwd;
	private InputStream responseDataStream;
	
	public InputStream getResponseDataStream() {
		return responseDataStream;
	}

	public void setResponseDataStream(InputStream responseDataStream) {
		this.responseDataStream = responseDataStream;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	public String execute() throws Exception
	{        
        String part = YMUtils.getYMPART(login, passwd, OAUTH_CONSUMER_KEY);
                
        responseDataStream = YMUtils.getYMToken(part, OAUTH_CONSUMER_KEY, OAUTH_CONSUMER_SECRET);
        
        String requestTokenResponse=getStringFromStream(responseDataStream);
        
        Map<String, String> keyValuePairs = getMapOfKeyValuePairs(requestTokenResponse);
                               
        responseDataStream = YMUtils.getLoginSession(keyValuePairs.get(YMUtils.PARAM_OAUTH_TOKEN), keyValuePairs.get(YMUtils.PARAM_OAUTH_TOKEN_SECRET),
        		OAUTH_CONSUMER_KEY, OAUTH_CONSUMER_SECRET);
        
        String createSessionResponse = YMUtils.getStringFromStream(responseDataStream);
                
        JSONObject jsonObj = new JSONObject(createSessionResponse);
        jsonObj.put(YMUtils.PARAM_OAUTH_SESSION_HANDLE, keyValuePairs.get(YMUtils.PARAM_OAUTH_SESSION_HANDLE));
        jsonObj.put(YMUtils.PARAM_OAUTH_TOKEN, keyValuePairs.get(YMUtils.PARAM_OAUTH_TOKEN));
        jsonObj.put(YMUtils.PARAM_OAUTH_TOKEN_SECRET, keyValuePairs.get(YMUtils.PARAM_OAUTH_TOKEN_SECRET));
        
        responseDataStream = new ByteArrayInputStream(jsonObj.toString().getBytes());

		return SUCCESS;
	}
		
	public static void main(String[] arg) throws Exception
	{
		LoginAction action = new LoginAction();
		action.login = "thecompknight@yahoo.co.in";
		action.passwd = "shekrules";
		action.execute();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(action.responseDataStream));
        String line;
        String requestToken="";
        while ((line = br.readLine()) != null) {
            requestToken = requestToken+line;
        }
        
        br.close();
        
        System.out.println(requestToken);

	}
}
