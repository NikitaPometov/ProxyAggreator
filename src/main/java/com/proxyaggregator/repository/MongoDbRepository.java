package com.proxyaggregator.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.proxyaggregator.serialisable.foxtoolsresult.FoxtoolsProxyResult;
import com.proxyaggregator.serialisable.foxtoolsresult.FoxtoolsProxyResultItem;
import com.proxyaggregator.services.proxylist.ProxyValidator;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Repository
public class MongoDbRepository {
    final static String MONGO_DB_NAME = "proxyserversdb";
    final MongoClient mongoClient;
    final MongoDatabase mongoDatabase;
    MongoCollection<Document> mongoCollection;
    String PROXY_SERVERS_COLLECTION_NAME;

    private MongoDbRepository () {
        this.PROXY_SERVERS_COLLECTION_NAME = "proxyServers";
        this.mongoClient = new MongoClient();
        this.mongoDatabase = mongoClient.getDatabase(MONGO_DB_NAME);
        this.mongoCollection = mongoDatabase.getCollection(
            PROXY_SERVERS_COLLECTION_NAME);
    }

    public MongoDbRepository insertList (List<String> items) {
        items.forEach(this::insert);
        return this;
    }

    public MongoDbRepository insert (String item) {
        Document document = Document.parse(item);
        if (null == mongoCollection.find(document).first()) {
            mongoCollection.insertOne(document);
        }
        return this;
    }

    public MongoDbRepository deleteAllInvalid () {
        for (Document document : mongoCollection.find()) {
            if (validateProxy(document)) {
                mongoCollection.deleteOne(document);
            }
        }

        return this;
    }

    private boolean validateProxy (Document document) {
        String ip = document.getString("ip");
        int port = document.getInteger("port");
        return ProxyValidator.validate(ip, port);
    }

    public MongoDbRepository deleteAll () {
        mongoCollection.drop();
        return this;
    }

    public String findOneByCountry (String country) {
        FindIterable<Document> documents = mongoCollection.find(
            Filters.eq("country.nameEn", country));

        for (Document document : documents) {
            if (validateProxy(document)) {
                return document.toJson();
            }
        }
        return null;
    }

    public FoxtoolsProxyResult getAllAsFoxtoolsProxyResult () {
        return new FoxtoolsProxyResult(this.getAllAsArrayList());
    }

    public ArrayList<FoxtoolsProxyResultItem> getAllAsArrayList () {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<FoxtoolsProxyResultItem> results = new ArrayList<>();

        getAllAsStrings().forEach(json -> {
            try {
                results.add(mapper.readValue(json,
                    new TypeReference<FoxtoolsProxyResultItem>() {
                    }));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return results;
    }

    public List<String> getAllAsStrings () {
        ArrayList<String> jsons = new ArrayList<>();

        mongoCollection.find().forEach(
            (Consumer<? super Document>)document -> jsons.add(
                document.toJson()));

        return jsons;
    }
}
