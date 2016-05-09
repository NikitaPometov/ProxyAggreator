package home.pometovnikita.foxtoolsresult;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryResult {

    @JsonProperty private String nameEn;
}
