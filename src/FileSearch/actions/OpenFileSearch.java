package FileSearch.actions;

import FileSearch.FSLog;
import FileSearch.SearchManager;
import FileSearch.dialogs.SearchDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class OpenFileSearch extends AnAction {
    SearchDialog dialog = new SearchDialog(new SearchManager());
    public void actionPerformed(AnActionEvent e) {
        FSLog.log.info("Action performed.");
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
