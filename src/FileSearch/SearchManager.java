package FileSearch;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SearchManager {

    ArrayList<Search> searchHistory = new ArrayList<>();
    HashMap<Search, Future> tasks = new HashMap<>();
    ExecutorService executor = Executors.newFixedThreadPool(1);

    public void execute(Search search) {
        searchHistory.add(search);
        tasks.put(search, executor.submit(() -> {
            for (String searchPath : search.searchOptions.searchPaths) {
                searchPath(searchPath, search);
            }
            search.finished();
        }));
    }

    public void cancel(Search search) {
        Future f = tasks.get(search);
        if (f != null) {
            f.cancel(true);
        }
        search.cancel();
    }

    public void searchPath(String searchPath, Search search) {
        try {
            Files.walkFileTree(FileSystems.getDefault().getPath(searchPath), new Searcher(search));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
