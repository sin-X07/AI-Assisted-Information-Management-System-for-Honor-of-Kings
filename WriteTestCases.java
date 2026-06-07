import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class WriteTestCases {
    public static void main(String[] args) throws Exception {
        String content = Files.readString(Path.of("docs/test-cases.md.gen"));
        Files.writeString(Path.of("docs/test-cases.md"), content, StandardCharsets.UTF_8);
        System.out.println("Done");
    }
}
