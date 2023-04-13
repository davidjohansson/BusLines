package se.lab.busstops.application;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import se.lab.busstops.integration.SLClient;

@Component
public class BusStopsDataRepository implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(BusStopsDataRepository.class);

    private final SLClient slClient;
    private LinkedHashMap<String, Set<String>> stopPointNamesByLineNumber;

    @Autowired
    public BusStopsDataRepository(SLClient slClient) {
        this.slClient = slClient;
    }

    public Map<String, Set<String>> getStopPointNamesByLineNumber() {
        return stopPointNamesByLineNumber;
    }

    @Override
    public void run(ApplicationArguments args) {
        LinkedHashMap<String, List<String>> stringListMap = slClient.getStopPointNumbersByLineNumber()
                .entrySet().stream()
                .sorted(Entry.comparingByValue((o1, o2) -> Integer.compare(o2.size(), o1.size())))
                .limit(10)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        stopPointNamesByLineNumber = slClient.getStopPointNamesByLineNumber(stringListMap);

        logger.info("Done");
    }
}
