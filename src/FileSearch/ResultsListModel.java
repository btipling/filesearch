package FileSearch;

import FileSearch.tools.Result;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;

public class ResultsListModel implements ListModel<String> {

    List<Result> results = new ArrayList<>();
    List<ListDataListener> listeners = new ArrayList<>();

    private void broadcastChange(ListDataEvent e) {
        for (ListDataListener l : listeners) {
            l.contentsChanged(e);
        }
    }

    private void broadcastAdd(ListDataEvent e) {
        for (ListDataListener l : listeners) {
            l.intervalAdded(e);
        }
    }

    public void update(List<Result> newResults) {
        List<Result> oldResults = this.results;
        int maxSize = oldResults.size();
        if (newResults.size() > maxSize) {
            maxSize = newResults.size();
        }
        results = newResults;
        broadcastChange(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, maxSize));
    }

    public void addedResults(int n) {
        int resultSizedIndex = results.size() - n;
        broadcastAdd(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, resultSizedIndex - n, resultSizedIndex));
    }

    @Override
    public int getSize() {
        return results.size();
    }

    @Override
    public String getElementAt(int index) {
        Result res = this.results.get(index);
        return res.getValue();
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
}
