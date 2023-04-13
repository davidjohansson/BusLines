package se.lab.busstops.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

class SLJsonParserTest {

    @Test
    void shouldGroupStopPointsByLineNumber() {
        //given
        FileInputStream json = getInputStreamFromFile("journeyPatternPointOnline.json");

        //when
        Map<String, List<String>> stringStringMap = new SLJsonParser().groupStopPointsNumbersByLineNumber(json);

        //then
        assertThat(stringStringMap.keySet().size(), equalTo(3));
        assertThat(stringStringMap.get("1"), contains("10001", "10002", "10003"));
        assertThat(stringStringMap.get("2"), contains("20001", "20002", "20003"));
        assertThat(stringStringMap.get("3"), contains("30001", "30002", "30003"));
    }

    @Test
    void shouldResolveStopNames() {
        //given
        FileInputStream journeyPatternPointOnline = getInputStreamFromFile("journeyPatternPointOnline.json");
        Map<String, List<String>> stringStringMap = new SLJsonParser().groupStopPointsNumbersByLineNumber(journeyPatternPointOnline);

        FileInputStream json = getInputStreamFromFile("stopPoint.json");

        //when
        Map<String, Set<String>> stopNamesByLineNumber = new SLJsonParser().getStopPointNamesByLineNumber(stringStringMap, json);

        //then
        assertThat(stopNamesByLineNumber.keySet().size(), equalTo(3));
        assertThat(stopNamesByLineNumber.get("1"), contains("Arbetargatan", "John Bergs plan", "Stadshagsplan"));
        assertThat(stopNamesByLineNumber.get("2"), contains("Arbetargatan", "Celsiusgatan", "S:t Eriksgatan"));
        assertThat(stopNamesByLineNumber.get("3"), contains("Kungsbroplan", "Frihamnens färjeterminal", "Frihamnsporten"));
    }

    @Test
    void shouldThrowIfUnparseableJson() {
        assertThrows(SLJsonParseException.class, () -> {
            FileInputStream json = getInputStreamFromFile("invalid.txt");
            new SLJsonParser().groupStopPointsNumbersByLineNumber(json);
        });
    }

    protected FileInputStream getInputStreamFromFile(String fileName) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(fileName).getFile());
            return FileUtils.openInputStream(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read json from file " + fileName, e);
        }
    }
}