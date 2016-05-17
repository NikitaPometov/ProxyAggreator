package com.proxyaggregator.serialisable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    @JsonProperty(value = "proxy") private String proxy;

    @JsonProperty(value = "httpStatus") private int httpStatus;

    @JsonProperty(value = "responseBody") private String responseBody;

    public Response (String proxy, int httpStatus, String responseBody) {
        this.proxy = proxy;
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
    }
}
