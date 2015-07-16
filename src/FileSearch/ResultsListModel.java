package FileSearch;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;

public class ResultsListModel implements ListModel<String> {

    List<Result> results = new ArrayList<>();
    List<ListDataListener> listeners = new ArrayList<>();

    public void update(List<Result> newResults) {
        List<Result> oldResults = this.results;
        int maxSize = oldResults.size();
        if (newResults.size() > maxSize) {
            maxSize = newResults.size();
        }
        this.results = newResults;
        ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, maxSize);
        for (ListDataListener l : listeners) {
            l.contentsChanged(e);
        }
    }

    @Override
    public int getSize() {
        return results.size();
    }

    @Override
    public String getElementAt(int index) {
        Result res = this.results.get(index);
        return res.toString();
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
