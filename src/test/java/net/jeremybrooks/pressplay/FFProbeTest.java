package net.jeremybrooks.pressplay;

import org.junit.Test;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FFProbeTest {

    @Test
    public void getDuration() throws Exception {
        URL url = FFProbeTest.class.getResource("/Double Violin Concerto 1st Movement - J.S. Bach.mp3");
        Path p = Paths.get(url.toURI());
        Duration duration = FFProbe.getDuration(p.toString());
        assertNotNull(duration);
        assertEquals(256, duration.getSeconds());
        assertEquals(548000000, duration.getNano());
    }
}