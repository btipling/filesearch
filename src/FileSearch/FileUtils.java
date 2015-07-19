package FileSearch;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

public class FileUtils {
    public static void openFile(String path, Project project) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(path));
        FileEditorManager.getInstance(project).openFile(virtualFile, true);
    }
}
