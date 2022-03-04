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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Duration;

import static net.jeremybrooks.pressplay.PressPlay.FFPLAY;

/**
 * Wrapper around the ffplay command.
 * <p>To play a media asset, create a new instance of FFPlay using the fluent builder.
 * The media must be specified, the other options are, well, optional.</p>
 * <p>You can use any object as the media, as long as the {@code toString()} method of the
 * object results in a string that ffplay can make sense of. This means you can use a
 * Path, File, URL, or a plain old String to specify where to find the media to play.
 * For example, to play a media file located at a Path:</p>
 * <pre>
 * {@code
 *   Path media = Paths.get("/path/to/the/file.mp3");
 *   FFPlay<Path> player = new FFPlay.Builder<Path>()
 *           .media(media)
 *           .build();
 *   player.play();
 * }
 * </pre>
 * <p>After building an FFPlay object, you can access the metadata that was parsed from the
 * object:</p>
 * <pre>
 * {@code
 *   Path media = Paths.get("/path/to/the/file.mp3");
 *   FFPlay<Path> player = new FFPlay.Builder<Path>()
 *           .media(media)
 *           .build();
 *   MediaMetadata metadata = player.getMediaMetadata();
 *   System.out.println("Playing " + metadata.getTitle() + " by " + metadata.getArtist());
 *   player.play();
 * }
 * </pre>
 */
public class FFPlay<T> {
    private static final Logger logger = LogManager.getLogger();
    private T media;
    private MediaMetadata mediaMetadata;
    private Duration seekTime;
    private boolean display;
    private Process process;
    private boolean stopCalled;
    private Thread shutdownThread;

    /**
     * Builder to create an instance of FFPlay.
     *
     * @param <T> the class that will be used to provide the path to the media to play.
     */
    public static class Builder<T> {
        private T media;
        private Duration seekTime;
        private boolean display = false;
        private boolean parseMetadata = true;

        /**
         * Set the media to play.
         *
         * @param media object representing the media to play.
         * @return builder for chaining.
         */
        public Builder<T> media(T media) {
            this.media = media;
            return this;
        }

        /**
         * Set the seek time. The media will begin playing from this location.
         *
         * @param seekTime the time to begin playback of the media.
         * @return builder for chaining.
         */
        public Builder<T> seekTime(Duration seekTime) {
            this.seekTime = seekTime;
            return this;
        }

        /**
         * Sets a flag indicating that ffplay should display a GUI during playback.
         *
         * <p>If this method is not called, no GUI will be displayed during playback.</p>
         *
         * @return builder for chaining.
         */
        public Builder<T> display() {
            this.display = true;
            return this;
        }

        /**
         * Sets a flag disabling parsing of metadata for the media.
         *
         * <p>By default, metadata will be parsed when build is called.
         * If you do not want to parse the metadata, call this method and no
         * metadata will be parsed.</p>
         *
         * @return builder for chaining.
         */
        public Builder<T> noMetadata() {
            this.parseMetadata = false;
            return this;
        }

        /**
         * Build the ffplay object with the parameters that have been set.
         *
         * @return instance of ffplay ready to play the media.
         */
        public FFPlay<T> build() {
            if (media == null) {
                throw new IllegalArgumentException("Media cannot be null.");
            }
            if (seekTime == null) {
                seekTime = Duration.ZERO;
            }
            return new FFPlay<>(this);
        }
    }

    /* Private constructor to force use of Builder. */
    private FFPlay() {
    }

    private FFPlay(Builder<T> builder) {
        this.media = builder.media;
        this.display = builder.display;
        try {
            if (builder.parseMetadata) {
                this.mediaMetadata = FFProbe.getMediaMetadata(media.toString());
            }
            if (this.mediaMetadata == null) {
                this.seekTime = Duration.ZERO;
            } else if (builder.seekTime.toMillis() > mediaMetadata.getDuration().toMillis()) {
                this.seekTime = Duration.ZERO;
            } else {
                this.seekTime = builder.seekTime;
            }
        } catch (Exception e) {
            logger.warn("Error parsing metadata, using seek time of ZERO", e);
            this.seekTime = Duration.ZERO;
        }
    }

    /**
     * Play the media using ffplay.
     *
     * <p>This method will start an ffplay process on a separate Thread, so the
     * caller will not be blocked. It will also register a shutdown hook to
     * stop the process in the event that the calling application exits before
     * the playback has completed or the stop method has been called.</p>
     *
     * <p>Once the playback has completed or the stop method has been called, the
     * shutdown hook will be removed.</p>
     */
    public void play() {
        logger.debug("Playing {} starting at {}ms display={}", media, seekTime.toMillis(), display);

        // note: -hide_banner really doesn't mean anything, since we are also using "-v quiet",
        //       but if we try to build the process with an empty string or a space, it causes
        //       problems. Using -hide_banner or -nodisp has the effect of either allowing
        //       ffplay to show a gui, or not showing the gui
        String nodisp = display ? "-hide_banner" : "-nodisp";

        Runnable ffplay = () -> {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    FFPLAY,
                    "-i",
                    media.toString(),
                    nodisp,
                    "-v",
                    "quiet",
                    "-ss",
                    Double.toString(seekTime.toMillis() / 1000.0))
                    .inheritIO();
            try {
                process = processBuilder.start();
                process.waitFor();
            } catch (IOException ioe) {
                logger.warn("Error while trying to play {}", media, ioe);
            } catch (InterruptedException ie) {
                if (!stopCalled) {
                    logger.warn("Interrupted while playing {}", media, ie);
                }
            } finally {
                Runtime.getRuntime().removeShutdownHook(shutdownThread);
            }
        };

        new Thread(ffplay).start();

        shutdownThread = new Thread(new PlayerShutdownHook(this));
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    /**
     * Stop any current playback.
     */
    public void stop() {
        stopCalled = true;
        if (null != process) {
            process.destroy();
        }
    }


    /**
     * Get the media object.
     *
     * @return object representing the media to be played.
     */
    public T getMedia() {
        return media;
    }

    /**
     * Get the parsed metadata for the media.
     *
     * @return metadata that was parsed from the media.
     */
    public MediaMetadata getMediaMetadata() {
        return mediaMetadata;
    }

    /**
     * Get the set seek time, which is where playback will begin.
     *
     * <p>If not set, the default is Duration.ZERO.</p>
     *
     * @return seek time.
     */
    public Duration getSeekTime() {
        return seekTime;
    }

    /**
     * Set the seek time, which is where playback will begin.
     *
     * @param seekTime the time to begin playback.
     */
    public void setSeekTime(Duration seekTime) {
        this.seekTime = seekTime;
    }

    /**
     * Get the value of the display flag.
     *
     * <p>The default value is false.</p>
     *
     * @return true if the display flag is set, false otherwise.
     */
    public boolean isDisplay() {
        return this.display;
    }


    private class PlayerShutdownHook implements Runnable {
        FFPlay<T> player;

        PlayerShutdownHook(FFPlay<T> player) {
            this.player = player;
        }

        public void run() {
            player.stop();
        }
    }
}
