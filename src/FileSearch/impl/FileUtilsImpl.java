package FileSearch.impl;

import FileSearch.tools.FileUtils;
import FileSearch.tools.PathManager;
import FileSearch.tools.Result;
import com.intellij.ide.actions.ShowFilePathAction;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.EmptyClipboardOwner;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;

public class FileUtilsImpl implements FileUtils {

    Project project;

    public FileUtilsImpl(Project project) {
        this.project = project;
    }

    @Override
    public void openFile(String path) {
        ShowFilePathAction.openFile(new File(path));
    }

    @Override
    public void openFolder(String path) {
        File file = new File(path);
        File folder = file.getParentFile();
        ShowFilePathAction.openFile(folder);
    }

    @Override
    public void copyFolderPath(String path) {
        File file = new File(path);
        File folder = file.getParentFile();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(folder.getAbsolutePath()), EmptyClipboardOwner.INSTANCE);
        notify("Folder path copied to clipboard", NotificationType.INFORMATION);
    }

    @Override
    public void copyPath(String path) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(path), EmptyClipboardOwner.INSTANCE);
        notify("Path copied to clipboard.", NotificationType.INFORMATION);
    }

    @Override
    public void openFileWithIDEA(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            notify("Path is a directory, cannot open it this way.", NotificationType.ERROR);
        }
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(path));
        if (virtualFile == null) {
            notify("Unable to open to the file.", NotificationType.ERROR);
            return;
        }
        FileEditorManager.getInstance(project).openFile(virtualFile, true);
    }

    @Override
    public boolean isHidden(String path) {
        File f = new File(path);
        return f.isHidden();
    }

    @Override
    public Result createResult(PathManager path) {
        return new ResultImpl(path);
    }

    public void notify(String message, NotificationType notificationType) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            String ID = "File Search";
            NotificationsConfiguration.getNotificationsConfiguration().register(ID, NotificationDisplayType.BALLOON, false);
            final Notification notification = new Notification("File Search", "File Search", message,
                    notificationType, null);
            Notifications.Bus.notify(notification, project);
        });
    }
}
