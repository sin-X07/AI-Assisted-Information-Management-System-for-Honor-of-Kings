import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Base64;

public class GenFile {
    public static void main(String[] args) throws Exception {
        // We'll read from a base64 string
        String content = "";
        // Read input line by line
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        content = sb.toString();
        // We're reading the raw java content, write it directly
        Path out = Paths.get(args[0]);
        Files.write(out, content.getBytes(StandardCharsets.UTF_8));
        System.out.println("Written " + content.length() + " chars to " + out);
    }
}
