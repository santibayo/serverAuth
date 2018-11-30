package es.boalis.securiy.upstream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public class HeadersWrapper extends HttpServletRequestWrapper {

    private String username;
    private String role;
    private Map<String,String> securityData;

    public HeadersWrapper(HttpServletRequest request) {
        super(request);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setSecurityData(Map<String, String> securityData) {
        this.securityData = securityData;
    }

    @Override
    public String getHeader(String name) {
        if (name.toLowerCase().equals("auth-user")){
            return username;
        }else if(name.toLowerCase().equals("role-user")){
            return role;
        }else if(name.toLowerCase().startsWith("sec-")){
            if (securityData==null){
                return null;
            }else{
                return securityData.get(name);
            }
        }else {
            return super.getHeader(name);
        }
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (name.toLowerCase().equals("auth-user")){
            if (name!=null) {
                List<String> values = Arrays.asList(name);
                Enumeration<String> ret = Collections.enumeration(values);
                return ret;
            }else{
                return null;
            }

        }else if(name.toLowerCase().equals("role-user")){
            if (role!=null) {
                List<String> values = Arrays.asList(role);
                Enumeration<String> ret = Collections.enumeration(values);
                return ret;
            }else{
                return null;
            }

        }else if(name.toLowerCase().startsWith("sec-")){
            if (securityData==null){
                return null;
            }else{
                List<String> values= Arrays.asList(securityData.get(name));
                Enumeration<String> ret = Collections.enumeration(values);
                return ret;
            }
        }else {
            return super.getHeaders(name);
        }
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Vector<String> mergedHeaders = new Vector<String>();
        if (this.username!=null){
            mergedHeaders.add(username);
        }

        if (this.role!=null){
            mergedHeaders.add(role);
        }

        if (securityData!=null){
            Iterator<String> names = securityData.keySet().iterator();
            while (names.hasNext()){
                String name = names.next();
                mergedHeaders.add(name);
            }
        }

        if (mergedHeaders.size()>0){
            List <String> list = Collections.list(super.getHeaderNames());
            mergedHeaders.addAll(list);
            Enumeration<String> headerNames = Collections.enumeration(mergedHeaders);
            return headerNames;

        }else {
            return super.getHeaderNames();
        }
    }

    @Override
    public int getIntHeader(String name) {
        return super.getIntHeader(name);
    }
}
