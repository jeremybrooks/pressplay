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

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class MediaMetadataTest {

    @Test
    public void testParseJson() throws Exception {
        InputStream in = FFProbeTest.class.getResourceAsStream("/metadata.json");
        assertNotNull(in);
        String json = IOUtils.toString(in, StandardCharsets.UTF_8);
        MediaMetadata metadata = new Gson().fromJson(json, MediaMetadata.class);
        assertNotNull(metadata);
        assertEquals("238.471837", metadata.getDurationAsString());
        assertEquals(238, metadata.getDuration().getSeconds());
        assertEquals(471000000, metadata.getDuration().getNano());
        assertEquals("1/1", metadata.getDisc());
        assertEquals("Cars", metadata.getTitle());
        assertEquals("Gary Numan", metadata.getArtist());
        assertEquals("Just Can't Get Enough: New Wave Hits Of The '80s Vol. 3", metadata.getAlbum());
        assertEquals("New Wave", metadata.getGenre());
        assertEquals("1/16", metadata.getTrack());
        assertEquals("1994", metadata.getDate());
        assertTrue(metadata.isCompilation());
    }

}