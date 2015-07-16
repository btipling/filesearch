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

    protected void check(Path dirOrFile) {
        if (dirOrFile.getFileName().toString().contains(search.searchOptions.searchString)) {
            search.addResult(new Result(dirOrFile));
        }
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        this.check(dir);
        return isRecursive;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        this.check(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
