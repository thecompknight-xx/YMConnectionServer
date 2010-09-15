package ymconnectionserver.actions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.json.JSONObject;

import ymconnectionserver.utils.YMUtils;

public class RefreshTokenAction extends AbstractActionBase
{
	private InputStream responseDataStream;
	private String token;
	private String sessionHandle;
	private String tokenSecret;
	
	public String getTokenSecret() {
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSessionHandle() {
		return sessionHandle;
	}

	public void setSessionHandle(String sessionHandle) {
		this.sessionHandle = sessionHandle;
	}

	public InputStream getResponseDataStream() {
		return responseDataStream;
	}

	public void setResponseDataStream(InputStream responseDataStream) {
		this.responseDataStream = responseDataStream;
	}

	public String execute() throws Exception
	{         		
        responseDataStream = YMUtils.refreshYMToken(token, sessionHandle, tokenSecret, OAUTH_CONSUMER_KEY, OAUTH_CONSUMER_SECRET);
        
        String refreshTokenResponse=getStringFromStream(responseDataStream);
        
        Map<String, String> keyValuePairs = getMapOfKeyValuePairs(refreshTokenResponse);
        
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(YMUtils.PARAM_OAUTH_SESSION_HANDLE, keyValuePairs.get(YMUtils.PARAM_OAUTH_SESSION_HANDLE));
        jsonObj.put(YMUtils.PARAM_OAUTH_TOKEN, keyValuePairs.get(YMUtils.PARAM_OAUTH_TOKEN));
        jsonObj.put(YMUtils.PARAM_OAUTH_TOKEN_SECRET, keyValuePairs.get(YMUtils.PARAM_OAUTH_TOKEN_SECRET));
        
        responseDataStream = new ByteArrayInputStream(jsonObj.toString().getBytes());
        
		return SUCCESS;
	}
	
	public static void main(String[] arg) throws Exception
	{
		RefreshTokenAction action = new RefreshTokenAction();
		action.sessionHandle="ADCBdky1ObinWzpX_cFknojHpjCqEq6iBbmuaE.LzLqqhbxd0me7Xb7LeA--";
		action.token="A%3D91RZ8yPQmDCSRMXt3x8kjpEV5I8ZqpJrq_KtU3kVPG5UvKypWW.P3BfoEB3BonYhK3TZinxPTvf3OWJNKzkHRXqVhQ16VkqSr68cUDXybHW7lhJX56eQEribEK9rs4irdhbcW7ZCX.G2DKF4fI.Cg_ozPUDSpTH6R5K0bguG6rLiB8ynAk3iAixaUgh8hKkQS4cms59jKNrM57vFWZ7fvAsYNJZ0Bf4e.E41T4tCHpB5af3LRBO_mZMLllwhLTK7iDvC_Kiy2FQ8n9LOFAyZOeiYFlPCEyin1Tw7vam8an9i_fLH2c93nwnbtIGTjDsHDB7FFsNNKx4dFYBB5t3xTX3JsidgFCgBPMgQl_MOIVw8cfI_NB9nwXY3.1e9HXdiIb2rWaqosWOMec9NdbV1VdhVrGqy3_Ycwc_B.cC4MaFT0kBRN8X8RIssyo.R0xC_14JuqwEdY7ZvwpjMXBAD72oK3TLKm9p97oVoXqNm6GKgwiu0hAwMyOKjmpBtCYPA0sT2xBdcDGauUCUlop.6.EVp4rSHXaQzScL_bs.nNq3nf_fGTH7LITEuFCh3HC05yY_YR4VovtQqP0xkIGo3VY1y4.lAvq6DLtFwTsF9wXOHl93IKhC1_v8WtImeis8JnMTZ1aW19yjPwoPPr8Eo7bVQ6LnSmmL3hfmreVaspGgXJ1G3Y1ToPhJWeRJcQ1meCyfJXVdjUvsgr35HvGO80VW2Kc9pUZ4TE6zZoxc-";
		
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
