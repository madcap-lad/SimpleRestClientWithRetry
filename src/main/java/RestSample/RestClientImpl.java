package RestSample;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jeet on 23-01-2017.
 */
public class RestClientImpl {

    final int RETRY_DELAY=2;
    final int MAX_RETRY_COUNT = 3;

    public String executeGet(String host, String port, String restPath, MultivaluedMap<String, String> queryParamMap) {
        WebTarget webTarget = getWebTarget(host,port,restPath);
        WebTarget webTargetWithParams = addParameters(webTarget,queryParamMap);
        RetryPolicy retryPolicy = new RetryPolicy();
        retryPolicy.retryOn(Exception.class).withDelay(RETRY_DELAY, TimeUnit.SECONDS).withMaxRetries(MAX_RETRY_COUNT);
        String response = Failsafe.with(retryPolicy).get(()->webTargetWithParams.request(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).get(String.class));
//        String response = webTargetWithParams.request(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);
        return  response;
    }

    public  String executePost(String host, String port, String restPath, String parameters)
    {
        WebTarget webTarget = getWebTarget(host,port,parameters);
        webTarget.queryParam(parameters);
        RetryPolicy retryPolicy = new RetryPolicy();
        retryPolicy.retryOn(Exception.class).withDelay(RETRY_DELAY, TimeUnit.SECONDS).withMaxRetries(MAX_RETRY_COUNT);
        String response = Failsafe.with(retryPolicy).get(()->webTarget.request(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(parameters,MediaType.APPLICATION_JSON_TYPE),String.class));
//        String response = webTarget.request(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(parameters,MediaType.APPLICATION_JSON_TYPE),String.class);
        return response;
    }

    private WebTarget getWebTarget(String host, String port, String restPath) {
        Client client = ClientBuilder.newClient();
        String url = host;
        if (port != null)
            host = host.concat(":").concat(port);
        WebTarget target = client.target(url);
        return target;
    }

    WebTarget addParameters(WebTarget webTarget, MultivaluedMap<String, String> parameters) {

        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            webTarget.queryParam(entry.getKey(), entry.getValue().toArray());
        }
        return  webTarget;
    }


}
