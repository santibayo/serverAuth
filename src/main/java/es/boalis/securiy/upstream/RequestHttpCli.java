package es.boalis.securiy.upstream;

public class RequestHttpCli {

    private String credential;
    private String method="GET";
    private String uri;
    private String hostname;
    private String protocol;
    private String port;

    public static RequestHttpCli builder(String protocol,String hostname,int port,String uri){
        RequestHttpCli cli =  new RequestHttpCli();
        cli.protocol = protocol;
        cli.hostname = hostname;
        cli.port = Integer.toString(port);
        cli.uri = uri;
        return cli;
    }

    public RequestHttpCli withCredential(String credential){
         this.credential = credential;
         return this;
    }

    public RequestHttpCli withMethod(String method){
        this.method = method;
        return this;
    }


    public String getCredential() {
        return credential;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getHostname() {
        return hostname;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getPort() {
        return port;
    }
}
