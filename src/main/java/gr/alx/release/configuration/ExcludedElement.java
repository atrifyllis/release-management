package gr.alx.release.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by alx on 10/15/2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcludedElement {

    private List<String> folders;
}
