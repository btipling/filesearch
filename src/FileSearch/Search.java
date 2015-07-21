package FileSearch;

import FileSearch.tools.Result;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Search {
    private AtomicBoolean stopped = new AtomicBoolean(false);
    protected SearchOptions searchOptions;
    private ArrayList<Result> results = new ArrayList<>();
    private ArrayList<SearchResultListener> listeners = new ArrayList<>();

    public Search(SearchOptions searchOptions) {
        this.searchOptions = searchOptions;
    }

    public void addResult(Result result) {
        if (stopped.get()) {
            return;
        }
        results.add(result);
        for (SearchResultListener l : listeners) {
            l.onReceivedResult(this, result);
        }
    }

    public void currentStatus(String status) {
        if (stopped.get()) {
            return;
        }
        for (SearchResultListener l : listeners) {
            l.onStatusUpdate(status);
        }
    }

    public ArrayList<Result> getResults() {
        return results;
    }

    public void finished() {
        if (stopped.get()) {
            return;
        }
        stopped.set(true);
        for (SearchResultListener l : listeners) {
            l.onFinishedResults(this);
        }
    }

    public void addListener(SearchResultListener listener) {
        listeners.add(listener);
    }

    public void cancel() {
        stopped.set(true);
        listeners.clear();
    }

    public boolean isStopped() {
        return stopped.get();
    }

    public void clear() {
        results.clear();
        for (SearchResultListener l : listeners) {
            l.onResultsUpdate(this);
        }
    }
}
