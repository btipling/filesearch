package FileSearch;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Search {
    private AtomicBoolean canceled = new AtomicBoolean(false);
    protected SearchOptions searchOptions;
    private ArrayList<Result> results = new ArrayList<Result>();
    private ArrayList<SearchResultListener> listeners = new ArrayList<>();

    public Search(SearchOptions searchOptions) {
        this.searchOptions = searchOptions;
    }

    public void addResult(Result result) {
        if (canceled.get()) {
            return;
        }
        results.add(result);
        for (SearchResultListener l : listeners) {
            l.onReceivedResult(this);
        }
    }

    public ArrayList<Result> getResults() {
        return results;
    }

    public void finished() {
        if (canceled.get()) {
            return;
        }
        for (SearchResultListener l : listeners) {
            l.onFinishedResults(this);
        }
    }

    public void addListener(SearchResultListener listener) {
        listeners.add(listener);
    }

    public void cancel() {
        canceled.set(true);
        listeners.clear();
    }

    public void clear() {
        results.clear();
    }
}
