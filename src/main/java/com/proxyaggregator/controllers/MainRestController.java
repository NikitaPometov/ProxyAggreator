package com.proxyaggregator.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proxyaggregator.ProxyClient;
import com.proxyaggregator.serialisable.ProxyRequest;
import com.proxyaggregator.serialisable.Response;
import com.proxyaggregator.serialisable.foxtoolsresult.FoxtoolsProxyResultItem;
import com.proxyaggregator.services.proxylist.ProxyListReceiver;
import com.proxyaggregator.repository.MongoDbRepository;
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
    @Autowired MongoDbRepository mongoDbRepository;
    @Autowired ProxyListReceiver proxyListReceiver;
    @Autowired ProxyClient proxyClient;

    @RequestMapping(value = "/deleteAll")
    public void clearDatabase () {
        mongoDbRepository.deleteAll();
    }

    @RequestMapping(value = "/getList", produces = "application/json")
    public List<FoxtoolsProxyResultItem> getProxiesList () {
        try {
            fillUpProxyDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mongoDbRepository.getAllAsArrayList();
    }

    @RequestMapping(value = "/refreshList")
    public void refreshProxyList () {
        mongoDbRepository.deleteAllInvalid();
        try {
            fillUpProxyDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillUpProxyDatabase () throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> results = new ArrayList<>();

        proxyListReceiver.receiveFoxtoolsProxyList().forEach((item) -> {
            try {
                results.add(objectMapper.writeValueAsString(item));
            } catch (JsonProcessingException e) {
                System.out.println(e.getLocation() + e.getMessage());
            }
        });
        mongoDbRepository.insertList(results);
    }

    @RequestMapping(value = "/", consumes = { MediaType.APPLICATION_JSON_VALUE,
        MediaType.TEXT_PLAIN_VALUE })
    public Response sendAndReceiveThroughProxy (
        @RequestBody ProxyRequest body) {

        try {
            return proxyClient.sendRequest(body);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
