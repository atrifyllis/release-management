package gr.alx.release.bower;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by alx on 10/9/2016.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class Dependencies {

    @JsonProperty("ct-common-ui")
    private String ctCommonUi;
    @JsonProperty("ct-product-manager-ui")
    private String ctProductManagerUi;
}
