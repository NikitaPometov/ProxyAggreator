package home.pometovnikita.proxylistcontroller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.pometovnikita.foxtoolsresult.FoxtoolsProxyResult;
import home.pometovnikita.foxtoolsresult.FoxtoolsProxyResultItem;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Component
public class ProxyListReceiver {

    private final RestTemplate restTemplate = new RestTemplate();

    public FoxtoolsProxyResult receiveFoxtoolsProxyList () throws IOException {
        JSONObject request = new JSONObject();

// set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(request.toString(),
            headers);

// send request and parse result
        final String URI = "http://api.foxtools.ru/v2/Proxy";
        ResponseEntity<String> loginResponse = restTemplate.exchange(URI,
            HttpMethod.GET, entity, String.class);
        JSONObject userJson;
        if (loginResponse.getStatusCode() == HttpStatus.OK) {
            userJson = new JSONObject(loginResponse.getBody());
        } else {
            userJson = new JSONObject("A:B");
        }

        ObjectMapper mapper = new ObjectMapper();
        final String items = userJson.getJSONObject("response").getJSONArray(
            "items").toString();

        return new FoxtoolsProxyResult(mapper.readValue(items,
            new TypeReference<List<FoxtoolsProxyResultItem>>() {
            }));
    }

}