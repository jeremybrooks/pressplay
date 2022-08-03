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

import static org.junit.Assert.*;

public class FFProbeTest {

    @Test
    public void getMediaMetadata() throws Exception {
        URL url = FFProbeTest.class.getResource("/Double Violin Concerto 1st Movement - J.S. Bach.mp3");
        assertNotNull(url);
        Path p = Paths.get(url.toURI());
        MediaMetadata metadata = FFProbe.getMediaMetadata(p.toString());
        assertNotNull(metadata);
        assertEquals(256, metadata.getDuration().getSeconds());
        assertEquals(548000000, metadata.getDuration().getNano());
        assertEquals("01 - Track 1", metadata.getTitle());
        assertEquals("Unknown Artist", metadata.getArtist());
        assertEquals("Unknown Album", metadata.getAlbum());
        assertEquals("1", metadata.getTrack());
        assertEquals(1, metadata.getTrackNumber());
        assertEquals(0, metadata.getTotalTracks());
        assertEquals("", metadata.getDisc());
        assertEquals(0, metadata.getDiscNumber());
        assertEquals(0, metadata.getTotalDiscs());
        assertFalse(metadata.isCompilation());
    }

    @Test
    public void testNoTrackNoDisc() throws Exception {
        URL url = FFProbeTest.class.getResource("/test-notrack-nodisc.mp3");
        assertNotNull(url);
        Path p = Paths.get(url.toURI());
        MediaMetadata metadata = FFProbe.getMediaMetadata(p.toString());
        assertNotNull(metadata);
        assertEquals("Artist One", metadata.getArtist());
        assertEquals("Album One", metadata.getAlbum());
        assertEquals("", metadata.getTrack());
        assertEquals(0, metadata.getTrackNumber());
        assertEquals(0, metadata.getTotalTracks());
        assertEquals("", metadata.getDisc());
        assertEquals(0, metadata.getDiscNumber());
        assertEquals(0, metadata.getTotalDiscs());
    }

    @Test
    public void testDiscsAndTracks() throws Exception {
        URL url = FFProbeTest.class.getResource("/test-discs-and-tracks.mp3");
        assertNotNull(url);
        Path p = Paths.get(url.toURI());
        MediaMetadata metadata = FFProbe.getMediaMetadata(p.toString());
        System.out.println(metadata);
        assertNotNull(metadata);
        assertEquals("5/10", metadata.getTrack());
        assertEquals(5, metadata.getTrackNumber());
        assertEquals(10, metadata.getTotalTracks());
        assertEquals("2/2", metadata.getDisc());
        assertEquals(2, metadata.getDiscNumber());
        assertEquals(2, metadata.getTotalDiscs());
    }

    @Test
    public void testDiscsNoTracks() throws Exception {
        URL url = FFProbeTest.class.getResource("/test-discs-no-tracks.mp3");
        assertNotNull(url);
        Path p = Paths.get(url.toURI());
        MediaMetadata metadata = FFProbe.getMediaMetadata(p.toString());
        assertNotNull(metadata);
        assertEquals(1, metadata.getDiscNumber());
        assertEquals(2, metadata.getTotalDiscs());
    }

    @Test
    public void testTracksNoDisc() throws Exception {
        URL url = FFProbeTest.class.getResource("/test-tracks-no-disc.mp3");
        assertNotNull(url);
        Path p = Paths.get(url.toURI());
        MediaMetadata metadata = FFProbe.getMediaMetadata(p.toString());
        assertNotNull(metadata);
        assertEquals(1, metadata.getTrackNumber());
        assertEquals(4, metadata.getTotalTracks());
        assertEquals(0, metadata.getDiscNumber());
        assertEquals(0, metadata.getTotalDiscs());
    }

    @Test
    public void testVariousArtists() throws Exception {
        URL url = FFProbeTest.class.getResource("/test-various-artists.mp3");
        assertNotNull(url);
        Path p = Paths.get(url.toURI());
        MediaMetadata metadata = FFProbe.getMediaMetadata(p.toString());
        assertNotNull(metadata);
        assertEquals("Various Artists", metadata.getAlbumArtist());
        assertTrue(metadata.isCompilation());
    }
}