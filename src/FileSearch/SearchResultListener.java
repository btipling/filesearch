package FileSearch;

public interface SearchResultListener {
    void onFinishedResults(Search search);
    void onReceivedResult(Search search);
}
