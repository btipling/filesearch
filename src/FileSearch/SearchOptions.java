package FileSearch;

import java.io.Serializable;

public class SearchOptions implements Serializable {
    public enum MatchOption {
        EXACT_FILE, MATCH_FILE, MATCH_PATH
    }
    public String searchString;
    public String[] searchPaths;
    public Boolean regex;
    public Boolean caseSensitive;
    public MatchOption match;
    public Boolean recursive;
    public Boolean searchHiddenDirs;
}
