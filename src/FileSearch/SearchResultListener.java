package FileSearch;

import FileSearch.tools.Result;

public interface SearchResultListener {
    void onFinishedResults(Search search);
    void onReceivedResult(Search search, Result result);
    void onStatusUpdate(String status);
    void onResultsUpdate(Search search);
}
