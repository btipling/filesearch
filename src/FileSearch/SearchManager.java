package FileSearch;

import java.util.ArrayList;

public class SearchManager {

    ArrayList<Search> searchHistory = new ArrayList<Search>();

    public void execute(Search search) {
        searchHistory.add(search);
    }
}
