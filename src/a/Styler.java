package a;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Styler {

    List<String> content;

    boolean loadFile(String name) {
        try {
            content = Files.readAllLines(Path.of(name));
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
