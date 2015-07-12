package FileSearch;

import java.util.ArrayList;

public class Search {
    protected SearchOptions searchOptions;
    protected ArrayList<Result> results = new ArrayList<Result>();

    public Search(SearchOptions searchOptions) {
        this.searchOptions = searchOptions;
    }

    public void addResult(Result result) {
        results.add(result);
    }


}
