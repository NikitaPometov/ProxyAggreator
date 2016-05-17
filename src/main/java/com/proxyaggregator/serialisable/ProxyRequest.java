package com.proxyaggregator.serialisable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProxyRequest {
    @JsonProperty(value = "country") private String country;

    @JsonProperty(value = "method") private String method;

    @JsonProperty(value = "url") private String url;

    @JsonProperty(value = "contentType") private String contentType;

    @JsonProperty(value = "payload") private String payload;

}
