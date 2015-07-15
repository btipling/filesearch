package FileSearch;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;

public class SearchManager {

    ArrayList<Search> searchHistory = new ArrayList<>();

    public void execute(Search search) {
        searchHistory.add(search);
        for (String searchPath : search.searchOptions.searchPaths) {
            searchPath(searchPath, search);
        }
    }

    public void searchPath(String searchPath, Search search) {
        try {
            Files.walkFileTree(FileSystems.getDefault().getPath(searchPath), new Searcher(search));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
