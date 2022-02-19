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

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

import static net.jeremybrooks.pressplay.PressPlay.FFPROBE;

/**
 * Wrapper around the ffprobe command.
 */
public class FFProbe {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Executes the ffprobe command to find the duration of media at a specified path.
     * <p>
     * The command line and output is as follows:
     * <pre>{@code
     *   ffprobe -i media_file.mp3 -show_entries format=duration -v quiet
     *   [FORMAT]
     *   duration=238.471837
     *   [/FORMAT]
     * }</pre>
     * <p>
     * The duration line will be parsed and converted into a Java Duration object.
     * If the process exits with a non-zero code, the exit code will be in the logs.
     *
     * @param media the path to the media.
     * @return duration of the media object, or Duration.ZERO if the media is null
     * or doesn't exist.
     * @throws IOException if there is an error while determining the duration.
     */
    public static Duration getDuration(String media) throws IOException {
        if (media == null || media.trim().length() == 0) {
            return Duration.ZERO;
        }
        logger.debug("Getting duration for {}", media);
        Duration duration = null;
        ProcessBuilder processBuilder = new ProcessBuilder(
                FFPROBE,
                "-i",
                media,
                "-show_entries",
                "format=duration",
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
            Optional<String> durationString = Arrays.stream(output.split("\\r?\\n"))
                    .filter(s -> s.startsWith("duration"))
                    .findFirst();
            if (durationString.isPresent()) {
                String line = durationString.get();
                float seconds = Float.parseFloat(line.substring(line.indexOf('=') + 1));
                duration = Duration.ofMillis((long) (seconds * 1000));
            }
        } catch (NumberFormatException nfe) {
            logger.error("Could not parse duration.", nfe);
        } catch (InterruptedException ie) {
            logger.warn("Interrupted while waiting for the process to finish.", ie);
        }
        return duration;
    }
}
