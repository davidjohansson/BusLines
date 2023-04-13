package se.lab.busstops.integration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

@Component
public class SLClient {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private Environment env;

    //TODO: Error handling
    public Map<String, List<String>> getStopPointNumbersByLineNumber() {
        String url = addApiKeyOrThrow("https://api.sl.se/api2/LineData.json?model=jour&DefaultTransportModeCode=BUS");
        return restTemplate.execute(url,
                HttpMethod.GET,
                null,
                (response) -> new SLJsonParser().groupStopPointsNumbersByLineNumber(response.getBody()));
    }

    //TODO: Error handling
    public LinkedHashMap<String, Set<String>> getStopPointNamesByLineNumber(Map<String, List<String>> stopPointNumbersByLineNumber) {
        String url = addApiKeyOrThrow("https://api.sl.se/api2/LineData.json?model=stop");
        return restTemplate.execute(url,
                HttpMethod.GET,
                null,
                (response) -> new SLJsonParser().getStopPointNamesByLineNumber(stopPointNumbersByLineNumber, response.getBody()));
    }

    private String addApiKeyOrThrow(String url) {
        String apiKey = env.getProperty("sl.apikey");
        if (ObjectUtils.isEmpty(apiKey)) {
            throw new RuntimeException(
                    "No api key found in config, the app will not start without it. One needs to be set in application.yml under 'sl.api'");
        }
        return "%s&key=%s".formatted(url, apiKey);
    }
}
