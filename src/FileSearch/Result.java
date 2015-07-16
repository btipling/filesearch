package FileSearch;

import java.nio.file.Path;

public class Result {
    private final Path path;

    public Result(Path path) {
        this.path = path;
    }

    public String toString() {
        return this.path.toString();
    }
}
