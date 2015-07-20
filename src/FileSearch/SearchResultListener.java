package FileSearch;

import FileSearch.impl.ResultImpl;

public interface SearchResultListener {
    void onFinishedResults(Search search);
    void onReceivedResult(Search search, ResultImpl result);
    void onStatusUpdate(String status);
    void onResultsUpdate(Search search);
}
