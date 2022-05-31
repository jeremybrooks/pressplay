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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * An object to encapsulate the metadata parsed from a media file.
 */
public class MediaMetadata {

    /**
     * Get the track number as an int.
     *
     * @return track number, or zero if the track number isn't available.
     */
    public int getTrackNumber() {
        return parseTrackOrDiscData(getTrack()).get(0);
    }

    /**
     * Get the total number of tracks as an int.
     *
     * @return total number of tracks, or zero if the total number of tracks isn't available.
     */
    public int getTotalTracks() {
        return parseTrackOrDiscData(getTrack()).get(1);
    }

    /**
     * Get the disk number as an int.
     *
     * @return disc number, or zero if the disc number isn't available.
     */
    public int getDiscNumber() {
        return parseTrackOrDiscData(getDisc()).get(0);
    }

    /**
     * Get the total number of discs as an int.
     *
     * @return total number of discs, or zero if the total number of discs isn't available.
     */
    public int getTotalDiscs() {
        return parseTrackOrDiscData(getDisc()).get(1);
    }

    /**
     * Get the duration of the media file represented as a Duration object.
     *
     * <p>The resolution of this duration is milliseconds.</p>
     *
     * @return duration of the media file, or Duration.ZERO if the duration isn't known.
     */
    public Duration getDuration() {
        try {
            float seconds = Float.parseFloat(format.duration);
            return Duration.ofMillis((long) (seconds * 1000));
        } catch (Exception e) {
            return Duration.ZERO;
        }
    }

    /**
     * Get the duration of the media file represented as a String.
     *
     * @return duration of the media file.
     */
    public String getDurationAsString() {
        return format.duration == null ? "" : format.duration;
    }

    /**
     * Get the filename of the media.
     *
     * @return filename if available, empty String otherwise.
     */
    public String getFilename() {
        return format.filename == null ? "" : format.filename;
    }

    /**
     * Get the number of streams parsed from the media.
     *
     * @return number of streams if available, zero otherwise.
     */
    public int getNumberStreams() {
        return format.nb_streams;
    }

    /**
     * Get the number of programs parsed from the media.
     *
     * @return number of programs if available, zero otherwise.
     */
    public int getNumberPrograms() {
        return format.nb_programs;
    }

    /**
     * Get the format name of the media.
     *
     * @return format name if available, empty String otherwise.
     */
    public String getFormatName() {
        return format.format_name == null ? "" : format.format_name;
    }

    /**
     * Get the format long name of the media.
     *
     * @return format long name if available, empty String otherwise.
     */
    public String getFormatLongName() {
        return format.format_long_name == null ? "" : format.format_long_name;
    }

    /**
     * Get the size of the media.
     *
     * @return size if available, empty String otherwise.
     */
    public String getSize() {
        return format.size == null ? "" : format.size;
    }

    /**
     * Get the bit rate of the media.
     *
     * @return bit rate if available, empty String otherwise.
     */
    public String getBitRate() {
        return format.bit_rate == null ? "" : format.bit_rate;
    }

    /**
     * Get the disc number of the media file.
     *
     * @return disc number if available, empty String otherwise.
     */
    public String getDisc() {
        // sometimes the disc info is in the tag "TPA", so check it if "disc" is null
        if (format.tags.disc == null) {
            return format.tags.TPA == null ? "" : format.tags.TPA;
        } else {
            return format.tags.disc;
        }
    }

    /**
     * Get the title parsed from the media.
     *
     * @return title if available, empty String otherwise.
     */
    public String getTitle() {
        return format.tags.title == null ? "" : format.tags.title;
    }

    /**
     * Get the artist parsed from the media.
     *
     * @return artist if available, empty String otherwise.
     */
    public String getArtist() {
        return format.tags.artist == null ? "" : format.tags.artist;
    }

    /**
     * Get the album parsed from the media.
     *
     * @return album if available, empty String otherwise.
     */
    public String getAlbum() {
        return format.tags.album == null ? "" : format.tags.album;
    }

    /**
     * Get the genre parsed from the media.
     *
     * @return genre if available, empty String otherwise.
     */
    public String getGenre() {
        return format.tags.genre == null ? "" : format.tags.genre;
    }

    /**
     * Get the track parsed from the media.
     *
     * @return track if available, empty String otherwise.
     */
    public String getTrack() {
        return format.tags.track == null ? "" : format.tags.track;
    }

    /**
     * Get the date parsed from the media.
     *
     * @return date if available, empty String otherwise.
     */
    public String getDate() {
        return format.tags.date == null ? "" : format.tags.date;
    }

    /**
     * Get compilation flag parsed from the media.
     *
     * @return true if media is part of a compilation, false otherwise.
     */
    public boolean isCompilation() {
        return null != format.tags.compilation && format.tags.compilation.equals("1");
    }

    private Format format;

    private class Format {
        private String filename;
        private int nb_streams;
        private int nb_programs;
        private String format_name;
        private String format_long_name;
        private String size;
        private String bit_rate;
        private String duration;
        private Tags tags;

        private class Tags {
            private String disc;
            private String TPA;
            private String title;
            private String artist;
            private String album;
            private String genre;
            private String track;
            private String date;
            private String compilation;
        }
    }

    /**
     * Return a JSON String representation of this object.
     *
     * @return a String in JSON format representing this object.
     */
    public String toString() {
        return new Gson().toJson(this);
    }

    private List<Integer> parseTrackOrDiscData(String data) {
        List<Integer> result = new ArrayList<>();
        if (data == null || data.trim().length() == 0) {
            result.add(0);
            result.add(0);
        } else {
            data = data.trim();
            int firstNumber = 0;
            int secondNumber = 0;
            try {
                int index = data.indexOf('/');
                if (index == -1) {
                    firstNumber = Integer.parseInt(data);
                } else {
                    firstNumber = Integer.parseInt(data.substring(0, index));
                    secondNumber = Integer.parseInt(data.substring(index + 1));
                }
            } catch (Exception e) {
                // ignore number parse errors
            } finally {
                result.add(firstNumber);
                result.add(secondNumber);
            }
        }
        return result;
    }
}
