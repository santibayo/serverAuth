package es.boalis.securiy.upstream;

import java.util.HashMap;
import java.util.Map;

public class ResponseHttpCli {
    private int responseCode;
    private String username;
    private String role;
    private Map<String,String> securityAuxInfo = new HashMap<String,String>();
    private String urlError;



    public ResponseHttpCli(int responseCode){
        this.responseCode = responseCode;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public void putSecurityAuxInfo(String key,String value){
        this.securityAuxInfo.put(key,value);
    }

    public Map<String, String> getSecurityAuxInfo() {
        return securityAuxInfo;
    }

    public String getUrlError() {
        return urlError;
    }

    public void setUrlError(String urlError) {
        this.urlError = urlError;
    }
}
