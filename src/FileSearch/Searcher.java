package FileSearch;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class Searcher implements FileVisitor<Path> {

    FileVisitResult isRecursive = FileVisitResult.CONTINUE;
    Search search;

    public Searcher(Search search) {
        this.search = search;
        if (!search.searchOptions.recursive) {
            this.isRecursive = FileVisitResult.SKIP_SUBTREE;
        }
    }

    protected FileVisitResult check(Path dirOrFile) {
        if (Thread.currentThread().isInterrupted()) {
            return FileVisitResult.TERMINATE;
        }
        Path filenamePath = dirOrFile.getFileName();
        if (filenamePath == null) {
            return null;
        }
        String filename = filenamePath.toString();
        if (filename.contains(search.searchOptions.searchString)) {
            search.addResult(new Result(dirOrFile));
        }
        return null;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        FileVisitResult r = this.check(dir);
        if (r != null) {
            return r;
        }
        search.currentStatus(String.format("Searching %s", dir.toString()));
        return isRecursive;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        FileVisitResult r = this.check(file);
        if (r != null) {
            return r;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        if (Thread.currentThread().isInterrupted()) {
            return FileVisitResult.TERMINATE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (Thread.currentThread().isInterrupted()) {
            return FileVisitResult.TERMINATE;
        }
        return FileVisitResult.CONTINUE;
    }
}
