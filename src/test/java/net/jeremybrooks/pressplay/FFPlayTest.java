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

import static org.junit.Assert.*;

public class FFPlayTest {


    @Test
    public void getMedia() throws Exception {
        URL url = FFPlayTest.class.getResource("/Double Violin Concerto 1st Movement - J.S. Bach.mp3");
        assertNotNull(url);
        Path path = Paths.get(url.toURI());
        FFPlay<Path> ffPlay = new FFPlay.Builder<Path>()
                .media(path)
                .build();
        assertNotNull(ffPlay);
        assertEquals(path, ffPlay.getMedia());
    }

    @Test
    public void getMediaMetadata() throws Exception {
        URL url = FFPlayTest.class.getResource("/Double Violin Concerto 1st Movement - J.S. Bach.mp3");
        assertNotNull(url);
        Path path = Paths.get(url.toURI());
        FFPlay<Path> ffPlay = new FFPlay.Builder<Path>()
                .media(path)
                .build();
        assertNotNull(ffPlay);
        assertNotNull(ffPlay.getMediaMetadata());
        MediaMetadata metadata = ffPlay.getMediaMetadata();
        assertEquals(256, metadata.getDuration().getSeconds());
        assertEquals(548000000, metadata.getDuration().getNano());
        assertEquals("01 - Track 1", metadata.getTitle());
        assertEquals("Unknown Artist", metadata.getArtist());
        assertEquals("Unknown Album", metadata.getAlbum());
        assertEquals("1", metadata.getTrack());
        assertEquals("", metadata.getDisc());
        assertFalse(metadata.isCompilation());

    }

    @Test
    public void testNoMetadataParse() throws Exception {
        URL url = FFPlayTest.class.getResource("/Double Violin Concerto 1st Movement - J.S. Bach.mp3");
        assertNotNull(url);
        Path path = Paths.get(url.toURI());
        FFPlay<Path> ffPlay = new FFPlay.Builder<Path>()
                .media(path)
                .noMetadata()
                .build();
        assertNotNull(ffPlay);
        assertNull(ffPlay.getMediaMetadata());
    }

    @Test
    public void testDefaultSeekTime() throws Exception {
        URL url = FFPlayTest.class.getResource("/Double Violin Concerto 1st Movement - J.S. Bach.mp3");
        assertNotNull(url);
        Path path = Paths.get(url.toURI());
        FFPlay<Path> ffPlay = new FFPlay.Builder<Path>()
                .media(path)
                .build();
        assertNotNull(ffPlay);
        assertEquals(Duration.ZERO, ffPlay.getSeekTime());
    }

    @Test
    public void testSetSeekTime() throws Exception {
        URL url = FFPlayTest.class.getResource("/Double Violin Concerto 1st Movement - J.S. Bach.mp3");
        assertNotNull(url);
        Path path = Paths.get(url.toURI());
        FFPlay<Path> ffPlay = new FFPlay.Builder<Path>()
                .media(path)
                .seekTime(Duration.ofSeconds(10))
                .build();
        assertNotNull(ffPlay);
        assertEquals(Duration.ofSeconds(10), ffPlay.getSeekTime());
    }

    @Test
    public void testSetSeekTimeAfterBuild() throws Exception {
        URL url = FFPlayTest.class.getResource("/Double Violin Concerto 1st Movement - J.S. Bach.mp3");
        assertNotNull(url);
        Path path = Paths.get(url.toURI());
        FFPlay<Path> ffPlay = new FFPlay.Builder<Path>()
                .media(path)
                .build();
        assertNotNull(ffPlay);
        ffPlay.setSeekTime(Duration.ofSeconds(20));
        assertEquals(Duration.ofSeconds(20), ffPlay.getSeekTime());
    }

    @Test
    public void testDefaultDisplayFlag() throws Exception {
        URL url = FFPlayTest.class.getResource("/Double Violin Concerto 1st Movement - J.S. Bach.mp3");
        assertNotNull(url);
        Path path = Paths.get(url.toURI());
        FFPlay<Path> ffPlay = new FFPlay.Builder<Path>()
                .media(path)
                .build();
        assertFalse(ffPlay.isDisplay());

    }

    @Test
    public void testSetDisplayFlag() throws Exception {
        URL url = FFPlayTest.class.getResource("/Double Violin Concerto 1st Movement - J.S. Bach.mp3");
        assertNotNull(url);
        Path path = Paths.get(url.toURI());
        FFPlay<Path> ffPlay = new FFPlay.Builder<Path>()
                .media(path)
                .display()
                .build();
        assertTrue(ffPlay.isDisplay());
    }
}
