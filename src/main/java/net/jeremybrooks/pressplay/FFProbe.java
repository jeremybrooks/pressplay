/*
 * PressPlay is Copyright 2022-2025 by Jeremy Brooks
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static net.jeremybrooks.pressplay.PressPlay.FFPROBE;

/**
 * Wrapper around the ffprobe command.
 */
public class FFProbe {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Executes the ffprobe command to get metadata about the media.
     *
     * <p>Various metadata is parsed from the media and converted into a MediaMetadata
     * object. If debug logging is enabled, the parsed data will be logged.</p>
     *
     * @param media the path to the media.
     * @return object with the metadata that was parsed from the media,
     * or null if the input string is null or empty.
     * @throws IOException if there is an error while parsing the media.
     */
    public static MediaMetadata getMediaMetadata(String media) throws IOException {
        if (media == null || media.trim().isEmpty()) {
            return null;
        }
        MediaMetadata metadata = null;
        logger.debug("Getting metadata for {}", media);
        ProcessBuilder processBuilder = new ProcessBuilder(
                FFPROBE,
                "-i",
                media,
                "-show_entries",
                "format",
                "-of",
                "json",
                "-v",
                "quiet")
                .redirectErrorStream(true);

        Process process = processBuilder.start();
        String output = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.warn("Process exited with non-zero result: {}", exitCode);
            }
            logger.debug("Output of process is \n{}", output);
            metadata = new Gson().fromJson(output, MediaMetadata.class);

        } catch (InterruptedException ie) {
            logger.warn("Interrupted while waiting for the process to finish.", ie);
        }
        return metadata;
    }
}
