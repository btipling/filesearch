package FileSearch.tools;

public interface FileUtils {
    void openFile(String path);
    void openFolder(String path);
    void copyFolderPath(String path);
    void copyPath(String path);
    void openFileWithIDEA(String path);
    boolean isHidden(String path);
    Result createResult(PathManager path);
}