package home.pometovnikita.foxtoolsresult;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ResponseBody
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoxtoolsProxyResult implements Iterable<FoxtoolsProxyResultItem> {

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private List<FoxtoolsProxyResultItem> items;

    public FoxtoolsProxyResult (ArrayList<FoxtoolsProxyResultItem> items) {
        this.items = items;
    }

    public FoxtoolsProxyResult () {
        this.items = new ArrayList<>();
    }

    public List<FoxtoolsProxyResultItem> getItems () {
        return items;
    }

    @Override
    public Iterator<FoxtoolsProxyResultItem> iterator () {
        return items.iterator();
    }

    public FoxtoolsProxyResult addItem (FoxtoolsProxyResultItem item) {
        items.add(item);
        return this;
    }
}
