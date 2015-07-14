package FileSearch.actions;

import FileSearch.FSLog;
import FileSearch.SearchManager;
import FileSearch.dialogs.SearchDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.awt.*;

public class OpenFileSearch extends AnAction {
    SearchDialog dialog = new SearchDialog(new SearchManager());
    public void actionPerformed(AnActionEvent e) {
        FSLog.log.info("Action performed.");
        dialog.setProject(e.getProject());
        dialog.setPreferredSize(new Dimension(800, 500));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
