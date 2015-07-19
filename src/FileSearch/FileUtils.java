package FileSearch;

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

public class FileUtils {
    public static void openFile(String path) {
        ShowFilePathAction.openFile(new File(path));
    }

    public static void openFolder(String path) {
        File file = new File(path);
        File folder = file.getParentFile();
        ShowFilePathAction.openFile(folder);
    }

    public static void copyFolderPath(String path, Project project) {
        File file = new File(path);
        File folder = file.getParentFile();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(folder.getAbsolutePath()), EmptyClipboardOwner.INSTANCE);
        notify("Folder path copied to clipboard", NotificationType.INFORMATION, project);
    }

    public static void copyPath(String path, Project project) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(path), EmptyClipboardOwner.INSTANCE);
        notify("Path copied to clipboard.", NotificationType.INFORMATION, project);
    }

    public static void openFileWithIDEA(String path, Project project) {
        File file = new File(path);
        if (file.isDirectory()) {
            notify("Path is a directory, cannot open it this way.", NotificationType.ERROR, project);
        }
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(path));
        if (virtualFile == null) {
            notify("Unable to open to the file.", NotificationType.ERROR, project);
            return;
        }
        FileEditorManager.getInstance(project).openFile(virtualFile, true);
    }

    public static void notify(String message, NotificationType notificationType, Project project) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            String ID = "File Search";
            NotificationsConfiguration.getNotificationsConfiguration().register(ID, NotificationDisplayType.BALLOON, false);
            final Notification notification = new Notification("File Search", "File Search", message,
                    notificationType, null);
            Notifications.Bus.notify(notification, project);
        });
    }
}
