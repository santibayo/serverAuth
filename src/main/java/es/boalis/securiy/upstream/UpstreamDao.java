package es.boalis.securiy.upstream;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UpstreamDao {
    private String endPoint;
    public UpstreamDao(String endpoint){
        this.endPoint = endpoint;
    }

    /**
     * Escribe la peticion
     * Sc-Req-Method - metodo original
     * Sc-Req-Protocol - protocolo
     * Sc-Req-Host - nombre del nodo
     * Sc-Req-Uri - uri
     * Sc-Req-Credential - credencial
     *
     * Si se valida retorna 200 y las siguientes cabeceras:
     * Sc-Auth-User - usuario validado en la credencial
     * Sc-Role-User - rol del usuario validado
     * y un set de cabeceras con el siguiente patron Sc-Res-XXXXXXXX
     * para informar de datos adicionales de seguridad.
     *
     * Si no es correcto se retorna un error y la url Sc-Res-UrlError apuntando al host
     *
     * @param requestDao
     * @return
     * @throws IOException
     */
    public ResponseHttpCli connect(RequestHttpCli requestDao)throws IOException {
        URL url = new URL(this.endPoint);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        // Expirar a los 10 segundos si la conexión no se establece
        urlConnection.setConnectTimeout(10000);
        // Esperar solo 15 segundos para que finalice la lectura
        urlConnection.setReadTimeout(15000);
        urlConnection.setRequestProperty("Sc-Req-Method", requestDao.getMethod());
        urlConnection.setRequestProperty("Sc-Req-Protocol", requestDao.getProtocol());
        urlConnection.setRequestProperty("Sc-Req-Host", requestDao.getHostname());
        urlConnection.setRequestProperty("Sc-Req-Uri", requestDao.getUri());
        urlConnection.setRequestProperty("Sc-Req-Credential", requestDao.getCredential());

        // data dentro
        int status = urlConnection.getResponseCode();

        // preparando respuesta
        ResponseHttpCli response = new ResponseHttpCli(status);
        if (status == 200) {
            String userId = urlConnection.getHeaderField("Sc-Auth-User");
            String roleRequested = urlConnection.getHeaderField("Sc-Role-User");
            response.setUsername(userId);
            response.setRole(roleRequested);
            Map<String, List<String>> headersResponse = urlConnection.getHeaderFields();
            Iterator it = headersResponse.keySet().iterator();
            Map<String, String> responseDataHeaders = new HashMap<String, String>();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (key.toLowerCase().startsWith("Sc-Res-")) {
                    response.putSecurityAuxInfo(key, urlConnection.getHeaderField(key));
                }
            }
            urlConnection.disconnect();
            return response;
        } else {
            String redirectUrl = urlConnection.getHeaderField("Sc-Res-UrlError");
            response.setUrlError(redirectUrl);
            urlConnection.disconnect();
            return response;
        }

    }
}
