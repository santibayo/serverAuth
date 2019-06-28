package es.boalis.securiy.upstream;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class UpstreamFilter implements Filter {
    private  String endPoint;
    private  String genericError;
    private String headerName;
    private  UpstreamDao dao;

    /**
     * Parametros de configuracion:
     * url-endpoint - url del servicio de validacion
     * header-credential - cabecera con la credencial
     * default-error-url - la url por defecto cuando no se retorne un mensaje especifico
     * @param filterConfig
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        this.endPoint = filterConfig.getInitParameter("url-endpoint");
        this.headerName = filterConfig.getInitParameter("header-credential");
        this.genericError = filterConfig.getInitParameter("default-error-url");
        dao = new UpstreamDao(this.endPoint);
    }

    /**
     * ejecuta la validacion con el dao.
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        Enumeration<String> headerNames = request.getHeaderNames();
        String credential = request.getHeader(headerName);
        if (credential==null){
            credential="_NULL_";
        }
        RequestHttpCli requestCli = RequestHttpCli.builder(request.getProtocol(),request.getLocalName(),request.getLocalPort(),request.getRequestURI()).
                withCredential(credential).
                withMethod(request.getMethod());

        // limpiar las peticiones para evitar XSS o Inyecciones
        requestCli = this.clean(requestCli);

        ResponseHttpCli response = dao.connect(requestCli);
        int status = response.getResponseCode();
        if (status == 200){

            // Crear un requestWrapper y ponerlo.
            HeadersWrapper newRequest = new HeadersWrapper(request);
            newRequest.setSecurityData(response.getSecurityAuxInfo());
            newRequest.setUsername(response.getUsername());
            newRequest.setRole(response.getRole());
            filterChain.doFilter(newRequest,servletResponse);
            return;

        }else{
            String urlRedirect = response.getUrlError();
            if (urlRedirect==null){
                urlRedirect= this.genericError;
            }
            HttpServletResponse httpSerlvetResponse = (HttpServletResponse) servletResponse;
            httpSerlvetResponse.setStatus(302);
            httpSerlvetResponse.sendRedirect(urlRedirect);
            return;
        }
    }

    private RequestHttpCli clean(RequestHttpCli rq){
        return rq;
    }

    public void destroy() {

    }
}
