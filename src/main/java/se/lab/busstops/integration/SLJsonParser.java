package se.lab.busstops.integration;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class SLJsonParser {

    Map<String, List<String>> groupStopPointsNumbersByLineNumber(InputStream jsonInputStream) {
        Map<String, List<String>> stopPointsByLineNumber = new HashMap<>();

        //The consumer defines what should be done when an entry in the list of JourneyPatternPointOnLines is encountered
        Consumer<JsonParser> listEntryConsumer = (jsonParser) -> {
            LineStopPoint lineStopPoint = createLineStopPoint(jsonParser);

            final List<String> strings = stopPointsByLineNumber.computeIfAbsent(lineStopPoint.lineNumber, (a) -> new ArrayList<>());
            //There will be duplicates since most bus lines traffic in both directions, also the data seems to have duplicate stops.
            if (!strings.contains(lineStopPoint.journeyPatternPointNumber)) {
                strings.add(lineStopPoint.journeyPatternPointNumber);
            }

            //Just to be explicit...
            lineStopPoint = null;
        };

        traverseJsonWithConsumer(jsonInputStream, listEntryConsumer);

        return stopPointsByLineNumber;
    }

    LinkedHashMap<String, Set<String>> getStopPointNamesByLineNumber(
            final Map<String, List<String>> stopPointsByLineNumber,
            InputStream json) {

        final List<String> allStopIdsSorted = stopPointsByLineNumber.keySet()
                .stream()
                .flatMap(key -> stopPointsByLineNumber.get(key).stream())
                .distinct()
                .sorted()
                .toList();

        Map<String, String> stopNameByNumber = new HashMap<>();

        //The consumer defines what should be done when an entry in the list of StopPoints is encountered
        Consumer<JsonParser> listEntryConsumer = (jParser) -> {
            resolveStopPointByNumber(jParser, allStopIdsSorted).ifPresent(stopPoint -> stopNameByNumber.put(stopPoint.number,
                    stopPoint.name));
        };

        traverseJsonWithConsumer(json, listEntryConsumer);

        return stopPointsByLineNumber.entrySet().stream().map(entry -> {
                            final Set<String> strings = entry.getValue()
                                    .stream()
                                    .map(stopNumber -> stopNameByNumber.getOrDefault(stopNumber, stopNumber))
                                    .collect(Collectors.toSet());
                            return new SimpleEntry<>(entry.getKey(), strings);
                        }
                )
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    private void traverseJsonWithConsumer(InputStream json, Consumer<JsonParser> jsonParserConsumer) {
        try {
            JsonFactory jfactory = new JsonFactory();
            JsonParser jParser = jfactory.createParser(json);
            while (!jParser.isClosed()) {
                //First, skip tokens up to the first array (which contains the JourneyPatternPointOnLine entries)...
                JsonToken jsonToken = jParser.nextToken();
                if (jsonToken == JsonToken.START_ARRAY) {
                    //Create a 'LineStopPoint' instance for each entry. Once used, this instance is eligible for garbage collection.
                    while (jParser.nextToken() != JsonToken.END_ARRAY) {
                        jsonParserConsumer.accept(jParser);
                    }
                }
            }

        } catch (IOException e) {
            throw new SLJsonParseException(e);
        }
    }

    private LineStopPoint createLineStopPoint(JsonParser jsonParser) {
        try {
            if (jsonParser.currentToken() != JsonToken.START_OBJECT) {
                throw new IllegalStateException("Expected content to be an object");
            }

            LineStopPoint lineStopPoint = new LineStopPoint();

            //Go through each json property in this object:
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {

                String property = jsonParser.getCurrentName();
                jsonParser.nextToken();
                if (Objects.equals(property, "LineNumber")) {
                    lineStopPoint.lineNumber = jsonParser.getValueAsString();
                } else if (Objects.equals(property, "JourneyPatternPointNumber")) {
                    lineStopPoint.journeyPatternPointNumber = jsonParser.getValueAsString();
                }
            }

            return lineStopPoint;
        } catch (IOException ioException) {
            throw new SLJsonParseException(ioException);
        }
    }

    private Optional<StopPoint> resolveStopPointByNumber(JsonParser jsonParser, List<String> numbers) {
        try {

            if (jsonParser.currentToken() != JsonToken.START_OBJECT) {
                throw new IllegalStateException("Expected content to be an object");
            }

            StopPoint stopPoint = null;

            //Go through each json property in this object:
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {

                String numberProperty = jsonParser.getCurrentName();
                if (Objects.equals(numberProperty, "StopPointNumber")) {
                    jsonParser.nextToken();
                    String stopNumber = jsonParser.getValueAsString();
                    jsonParser.nextToken();

                    if (Collections.binarySearch(numbers, stopNumber) >= 0) {
                        String nameProperty = jsonParser.getCurrentName();

                        if (Objects.equals(nameProperty, "StopPointName")) {
                            jsonParser.nextToken();
                            String stopName = jsonParser.getValueAsString();
                            stopPoint = new StopPoint(stopNumber, stopName);
                        } else {
                            throw new IllegalStateException("Expected content to be an object");
                        }
                    }
                }
            }
            return stopPoint != null ? Optional.of(stopPoint) : Optional.empty();
        } catch (IOException ioException) {
            throw new SLJsonParseException(ioException);
        }
    }

    private static class LineStopPoint {

        String lineNumber;
        String journeyPatternPointNumber;
    }

    private static class StopPoint {

        String number;
        String name;

        public StopPoint(String number, String name) {
            this.number = number;
            this.name = name;
        }
    }
}
