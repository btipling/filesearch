package FileSearch;

import java.io.Serializable;

public class SearchOptions implements Serializable {
    String searchString;
    String searchPath;
    Boolean isRegex;
    Boolean wholePath;
    Boolean recursive;
}
