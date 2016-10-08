package gr.alx.release.bower;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gr.alx.release.packagejson.PackageFileRepresentation;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by alx on 10/7/2016.
 */
@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BowerFileRepresentation extends PackageFileRepresentation {
    
}
