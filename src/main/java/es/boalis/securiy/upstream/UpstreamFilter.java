package es.boalis.securiy.upstream;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class UpstreamFilter implements Filter {
    private  String endPoint;
    private  String genericError;
    public void init(FilterConfig filterConfig) throws ServletException {
        this.endPoint = filterConfig.getInitParameter("url-endpoint");
        this.genericError =  filterConfig.getInitParameter("gen-error");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        Enumeration<String> headerNames = request.getHeaderNames();
        URL url = new URL(this.endPoint);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        // Expirar a los 10 segundos si la conexión no se establece
        urlConnection.setConnectTimeout(10000);
        // Esperar solo 15 segundos para que finalice la lectura
        urlConnection.setReadTimeout(15000);
        while(headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            String value = request.getHeader(headerName);
            urlConnection.setRequestProperty(headerName,value);
        }
        urlConnection.setRequestProperty("Req-Uri",request.getRequestURI());
        urlConnection.setRequestProperty("Req-Host",request.getLocalName());
        urlConnection.setRequestProperty("Req-Method",request.getMethod());

        int status = urlConnection.getResponseCode();
        if (status == 200){
            String userId = urlConnection.getHeaderField("Auth-User");
            String roleRequested = urlConnection.getHeaderField("Role-User");
            Map<String, List<String>> headersResponse = urlConnection.getHeaderFields();
            Iterator it = headersResponse.keySet().iterator();
            Map<String,String> responseDataHeaders = new HashMap<String, String>();
            while (it.hasNext()){
                String key = (String) it.next();
                if (key.toLowerCase().startsWith("sec-")){
                    responseDataHeaders.put(key,urlConnection.getHeaderField(key));
                }
            }
            urlConnection.disconnect();
            // Crear un requestWrapper y ponerlo.
            HeadersWrapper newRequest = new HeadersWrapper(request);
            newRequest.setSecurityData(responseDataHeaders);
            newRequest.setUsername(userId);
            newRequest.setRole(roleRequested);
            filterChain.doFilter(newRequest,servletResponse);
            return;

        }else{
                String urlRedirect = urlConnection.getHeaderField("Redir-Url");
                if (urlRedirect==null){
                    urlRedirect= this.genericError;
                }
                HttpServletResponse httpSerlvetResponse = (HttpServletResponse) servletResponse;
                httpSerlvetResponse.sendRedirect(urlRedirect);
                httpSerlvetResponse.setHeader("Status-Code",Integer.toString(status));
                urlConnection.disconnect();
        }
    }

    public void destroy() {

    }
}
