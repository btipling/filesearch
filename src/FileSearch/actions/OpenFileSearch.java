package FileSearch.actions;

import FileSearch.FSLog;
import FileSearch.SearchManager;
import FileSearch.dialogs.SearchDialog;
import FileSearch.impl.FileUtilsImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import java.awt.*;

public class OpenFileSearch extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        FSLog.log.info("Action performed.");
        Project project = e.getProject();
        FileUtilsImpl fileUtils = new FileUtilsImpl(project);
        SearchDialog dialog = new SearchDialog(new SearchManager  (fileUtils), project, fileUtils);
        dialog.setPreferredSize(new Dimension(800, 500));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
