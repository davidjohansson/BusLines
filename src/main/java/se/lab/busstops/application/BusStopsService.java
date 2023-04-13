package se.lab.busstops.application;

import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BusStopsService {

    @Autowired
    BusStopsDataRepository busStopsDataRepository;

    public Map<String, Set<String>> getBusStops() {
        return busStopsDataRepository.getStopPointNamesByLineNumber();
    }
}
