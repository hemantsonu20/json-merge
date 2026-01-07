package json;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import com.github.hemantsonu20.json.JsonMerge;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonMergeTest {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @TestFactory
    public Stream<DynamicTest> generateTestsFromTestFiles() throws URISyntaxException, IOException {

        Path testFolderPath = Paths.get(ClassLoader.getSystemResource("json-merge-test").toURI());
        Stream<Path> testFilesPath = Files.find(testFolderPath,
                                                Integer.MAX_VALUE,
                                                (path, attr) -> attr.isRegularFile());

        return testFilesPath.map(this::jsonMergeTest);
    }

    private DynamicTest jsonMergeTest(Path path) {
        try {
            byte[] fileContent = Files.readAllBytes(path);
            return DynamicTest.dynamicTest(path.getFileName().toString(), () -> {

                ArrayNode testData = (ArrayNode) OBJECT_MAPPER.readTree(fileContent);
                String srcJsonStr = OBJECT_MAPPER.writeValueAsString(testData.get(0));
                String targetJsonStr = OBJECT_MAPPER.writeValueAsString(testData.get(1));
                JsonNode expectedMergedNode = testData.get(2);

                String actualMergedJsonStr = JsonMerge.merge(srcJsonStr, targetJsonStr);
                JsonNode actualMergedNode = OBJECT_MAPPER.readTree(actualMergedJsonStr);

                assertEquals(expectedMergedNode, actualMergedNode);

            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
