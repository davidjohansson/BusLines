package se.lab.busstops.api;

import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.lab.busstops.application.BusStopsService;

@RestController
public class BusStopsController {

    @Autowired
    BusStopsService busStopService;

    @GetMapping("/stops")
    public Map<String, Set<String>> handle() {
        return busStopService.getBusStops();
    }
}
