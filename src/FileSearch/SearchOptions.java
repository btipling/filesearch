package FileSearch;

import java.io.Serializable;

public class SearchOptions implements Serializable {
    public String searchString;
    public String[] searchPaths;
    public Boolean regex;
    public Boolean caseSensitive;
    public Boolean wholePath;
    public Boolean recursive;
}
