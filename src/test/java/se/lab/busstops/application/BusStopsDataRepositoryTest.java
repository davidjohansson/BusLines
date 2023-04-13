package se.lab.busstops.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.DefaultApplicationArguments;
import se.lab.busstops.integration.SLClient;

class BusStopsDataRepositoryTest {

    @Test
    void run() {

        //given
        SLClient slClient = Mockito.mock(SLClient.class);
        BusStopsDataRepository busStopsDataRepository = new BusStopsDataRepository(slClient);

        ///when
        busStopsDataRepository.run(new DefaultApplicationArguments());

        //then

    }
}