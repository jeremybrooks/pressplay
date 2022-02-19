package net.jeremybrooks.pressplay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Duration;

import static net.jeremybrooks.pressplay.PressPlay.FFPLAY;

/**
 * Wrapper around the ffplay command.
 * <p>
 * To play a media asset, create a new instance of FFPlay using the fluent builder.
 * The media must be specified, the other options are, well, optional.
 * <p>
 * You can use any object as the media, as long as the {@code toString()} method of the
 * object results in a string that ffplay can make sense of. This means you can use a
 * Path, File, URL, or a plain old String to specify where to find the media to play.
 * For example, to play a media file located at a Path:
 * <pre>
 * {@code
 *   Path media = Paths.get("/path/to/the/file.mp3");
 *         FFPlay<Path> player = new FFPlay.Builder<Path>()
 *                 .media(media)
 *                 .build();
 *         player.play();
 * }
 * </pre>
 */
public class FFPlay<T> {
    private static final Logger logger = LogManager.getLogger();

    private T media;
    private Duration seekTime;
    private boolean display;

    private Process process;
    private boolean stopCalled;

    /**
     * Builder to create an instance of FFPlay.
     *
     * @param <T> the class that will be used to provide the path to the media to play.
     */
    public static class Builder<T> {
        private T media;
        private Duration seekTime;
        private boolean display = false;

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
         * Sets a flag indicating that ffplay should display a gui during playback.
         *
         * @return builder for chaining.
         */
        public Builder<T> display() {
            this.display = true;
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
            return new FFPlay<>(media, seekTime, display);
        }
    }

    private FFPlay() {
    }

    private FFPlay(T media, Duration seekTime, boolean display) {
        this.media = media;
        this.display = display;
        try {
            Duration duration = FFProbe.getDuration(media.toString());
            if (duration == null) {
                this.seekTime = Duration.ZERO;
            } else if (seekTime.toMillis() > duration.toMillis()) {
                this.seekTime = Duration.ZERO;
            } else {
                this.seekTime = seekTime;
            }
        } catch (Exception e) {
            logger.warn("Error parsing duration, using seek time of ZERO", e);
            this.seekTime = Duration.ZERO;
        }
    }

    public void play() {
        logger.debug("Playing {} starting at {}ms display={}", media, seekTime.toMillis(), display);

        // note: -hide_banner really doesn't mean anything, since we are also using "-v quiet",
        //       but if we try to build the process with an empty string or a space, it causes
        //       problems. Using -hide_banner or -nodisp has the effect of either allowing
        //       ffplay to show a gui, or not showing the gui
        String nodisp = display ? "-hide_banner" : "-nodisp";

        new Thread(() -> {
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
            }
        }).start();
    }

    public void stop() {
        stopCalled = true;
        process.destroy();
    }


    public T getMedia() {
        return media;
    }

    public Duration getSeekTime() {
        return seekTime;
    }
}
