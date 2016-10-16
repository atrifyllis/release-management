package gr.alx.release.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Created by alx on 10/15/2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BowerElements {
    private List<String> defaults;
    private Map<String, List<String>> endings;
}
