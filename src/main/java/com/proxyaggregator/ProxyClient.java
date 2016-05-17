package com.proxyaggregator;

import com.proxyaggregator.repository.MongoDbRepository;
import com.proxyaggregator.serialisable.ProxyRequest;
import com.proxyaggregator.serialisable.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class ProxyClient {

    @Autowired MongoDbRepository mongoDbRepository;

    public Response sendRequest (ProxyRequest proxyRequest)
        throws IOException {

        JSONObject jsonObject = new JSONObject(proxyRequest);

        HttpHeaders headers = getHttpHeaders(jsonObject);

        HttpEntity<String> entity = new HttpEntity<>(getPayload(jsonObject),
            headers);

        RestTemplate restTemplate = getRestTemplate(getCountry(jsonObject));
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            getUrl(jsonObject), getMethod(jsonObject), entity, String.class);

        return parseResponse(responseEntity);
    }

    private HttpHeaders getHttpHeaders (JSONObject jsonObject)
        throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getType(jsonObject));
        headers.setLocation(getUrl(jsonObject));
        headers.setAccessControlRequestMethod(getMethod(jsonObject));
        return headers;
    }

    private String getPayload (JSONObject jsonObject) {
        return jsonObject.getString("payload");
    }

    private RestTemplate getRestTemplate (String country) {
        SimpleClientHttpRequestFactory requestFactory =
            new SimpleClientHttpRequestFactory();

        JSONObject json = new JSONObject(
            mongoDbRepository.findOneByCountry(country));

        Proxy proxy = new Proxy(Proxy.Type.HTTP,
            new InetSocketAddress(json.getString("ip"), json.getInt("port")));
        requestFactory.setProxy(proxy);

        return new RestTemplate(requestFactory);
    }

    private String getCountry (JSONObject jsonObject) {
        return jsonObject.getString("country");
    }

    private URI getUrl (JSONObject jsonObject) throws IOException {
        try {
            return new URI(jsonObject.getString("url"));
        } catch (URISyntaxException e) {
            throw new IOException("URL is invalid");
        }
    }

    private HttpMethod getMethod (JSONObject jsonObject) throws IOException {
        switch (jsonObject.getString("method")) {
        case ("POST"):
            return HttpMethod.POST;
        case ("GET"):
            return HttpMethod.GET;
        case ("PUT"):
            return HttpMethod.PUT;
        case ("DELETE"):
            return HttpMethod.DELETE;
        default:
            throw new IOException("Http method is invalid");
        }
    }

    private Response parseResponse (ResponseEntity<String> responseEntity) {
        int httpStatus = responseEntity.getStatusCode().value();
        return new Response("", httpStatus, responseEntity.getBody());
    }

    private MediaType getType (JSONObject jsonObject) throws IOException {
        switch (jsonObject.getString("contentType")) {
        case ("application/json"):
            return MediaType.APPLICATION_JSON;
        case ("text/plain"):
            return MediaType.TEXT_PLAIN;
        default:
            throw new IOException("Content type is invalid");
        }
    }
}
