package FileSearch.actions;

import FileSearch.FSLog;
import FileSearch.dialogs.SearchDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class OpenFileSearch extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        FSLog.log.info("Action performed.");

        SearchDialog dialog = new SearchDialog();
        dialog.pack();
        dialog.setVisible(true);
    }
}
