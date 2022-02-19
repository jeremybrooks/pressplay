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

/**
 * This class sets up the paths to the ffmpeg binaries that are used by PressPlay.
 * <p>
 * The default location for these tools is {@code /usr/local/bin}. If your ffmpeg
 * tools are installed there, you don't need to do anything.
 * <p>
 * If the ffmpeg tools are installed in a different location, you will need to
 * set the system property @{code pressplay.ffmpeg.path} to the correct location of the binaries.
 * <p>
 * For example, if you have ffmpeg tools installed at {@code /opt/ffmpeg/bin/}, set the property as
 * follows:
 * <p>
 * {@code System.setProperty("pressplay.ffmpeg.path", "/opt/ffmpeg/bin/");}
 * <p>
 * or, as a command line option: @{code -Dpressplay.ffmpeg.path=/opt/ffmpeg/bin/}
 * <p>
 * Note the trailing slash - this is important. The binary names will be appended to
 * the value of the system property.
 */
public class PressPlay {

    public static final String FFPROBE;
    public static final String FFPLAY;

    private static final String FFMPEG_PATH_PROPERTY = "pressplay.ffmpeg.path";

    static {
        String path = System.getProperty(FFMPEG_PATH_PROPERTY);
        if (path == null || path.trim().length() == 0) {
            path = "/usr/local/bin/";
        }
        FFPROBE = path + "ffprobe";
        FFPLAY = path + "ffplay";
    }
}
