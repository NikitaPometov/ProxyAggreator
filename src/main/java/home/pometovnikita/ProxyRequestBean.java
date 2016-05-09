package home.pometovnikita;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProxyRequestBean {
    @JsonProperty(value = "country") private String country;

    @JsonProperty(value = "method") private String method;

    @JsonProperty(value = "url") private String url;

    @JsonProperty(value = "contentType") private String contentType;

    @JsonProperty(value = "payload") private String payload;

}
