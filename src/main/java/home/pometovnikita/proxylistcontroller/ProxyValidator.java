package home.pometovnikita.proxylistcontroller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class ProxyValidator {
    public static boolean validate (String ip, int port) {
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = new RestTemplate().exchange(
                new URI(null, null, ip, port, null, null, null),
                HttpMethod.HEAD, new HttpEntity<>(null), String.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }

        return HttpStatus.OK.equals(responseEntity.getStatusCode());
    }
}
