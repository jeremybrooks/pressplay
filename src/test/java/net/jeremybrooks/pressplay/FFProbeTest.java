/*
 * PressPlay is Copyright 2022 by Jeremy Brooks
 *
 * This file is part of PressPlay.
 *
 * PressPlay is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PressPlay is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PressPlay.  If not, see <http://www.gnu.org/licenses/>.
 */

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
        assertNotNull(url);
        Path p = Paths.get(url.toURI());
        Duration duration = FFProbe.getDuration(p.toString());
        assertNotNull(duration);
        assertEquals(256, duration.getSeconds());
        assertEquals(548000000, duration.getNano());
    }
}