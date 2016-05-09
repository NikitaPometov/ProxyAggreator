package home.pometovnikita.foxtoolsresult;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FoxtoolsProxyResultItem {
    @JsonProperty private String ip;

    @JsonProperty private int port;

    @JsonProperty private CountryResult country;
}
