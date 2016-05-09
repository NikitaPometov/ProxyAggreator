package home.pometovnikita;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.pometovnikita.foxtoolsresult.FoxtoolsProxyResultItem;
import home.pometovnikita.mongodbcontroller.MongoDbController;
import home.pometovnikita.proxylistcontroller.ProxyListReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MainRestController {
    @Autowired MongoDbController mongoDbController;
    @Autowired ProxyListReceiver proxyListReceiver;
    @Autowired ProxyClient proxyClient;

    @RequestMapping(value = "/deleteAll")
    public void clearDatabase () {
        mongoDbController.deleteAll();
    }

    @RequestMapping(value = "/getList", produces = "application/json")
    public List<FoxtoolsProxyResultItem> getProxiesList () {
        try {
            updateProxyDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mongoDbController.getAllAsArrayList();
    }

    private void updateProxyDatabase () throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> results = new ArrayList<>();

        proxyListReceiver.receiveFoxtoolsProxyList().forEach((item) -> {
            try {
                results.add(objectMapper.writeValueAsString(item));
            } catch (JsonProcessingException e) {
                System.out.println(e.getLocation() + e.getMessage());
            }
        });
        mongoDbController.insertList(results);
    }

    @RequestMapping(value = "/", consumes = { MediaType.APPLICATION_JSON_VALUE,
        MediaType.TEXT_PLAIN_VALUE })
    public ResponseBean sendAndReceiveThroughProxy (
        @RequestBody ProxyRequestBean body) {

        try {
            return proxyClient.sendRequest(body);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
