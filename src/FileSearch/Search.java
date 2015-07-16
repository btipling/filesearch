package FileSearch;

import java.util.ArrayList;

public class Search {
    protected SearchOptions searchOptions;
    private ArrayList<Result> results = new ArrayList<Result>();

    public Search(SearchOptions searchOptions) {
        this.searchOptions = searchOptions;
    }

    public void addResult(Result result) {
        results.add(result);
    }

    public ArrayList<Result> getResults() {
        return results;
    }
}
