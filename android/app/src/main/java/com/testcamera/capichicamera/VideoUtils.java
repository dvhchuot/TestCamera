package com.testcamera.capichicamera;

import android.os.Environment;
import android.util.Log;

import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class VideoUtils {
    private static final String VIDEO_DIRECTORY_NAME = "Capitri";

    public static final int LIMIT_RECORD_TIME = 15;

    public static final String CAMERA_BACK_ID = "0";

    public static final String CAMERA_FRONT_ID = "1";

    public static VideoConfiguration defaultConfig;

    public static VideoConfiguration VDIEO_720P = new VideoConfiguration();

    static {
        VDIEO_720P.video_bitrate = 2000000;
        VDIEO_720P.audio_bitrate = 128000;
        VDIEO_720P.width = 1280;
        VDIEO_720P.height = 720;
        defaultConfig = VDIEO_720P;
    }

    public static String mergeVideo(List<File> videos, boolean deleteOld) {
        String outputFileName = getOutputMediaFile().getAbsolutePath();
        try {
            Movie[] inMovies = new Movie[videos.size()];
            Log.d("MoviePaths",videos.size()+"");
            for (int i = 0; i <videos.size(); i++) {
                Log.d("FilePath",videos.get(i)+"");
                inMovies[i] = MovieCreator.build(videos.get(i).getAbsolutePath());
            }

            List<Track> videoTracks = new LinkedList<>();
            List<Track> audioTracks = new LinkedList<>();
            for (Movie m : inMovies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                }
            }

            Movie result = new Movie();

            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks
                        .toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }

            BasicContainer out = (BasicContainer) new DefaultMp4Builder()
                    .build(result);

            @SuppressWarnings("resource")
            FileChannel fc = new RandomAccessFile(outputFileName, "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.e("FileNotFoundException",e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("IOException",e.toString());
            e.printStackTrace();
        }

        if (deleteOld) {
            for (int i = 0; i <videos.size(); i++) {
                Log.d("Delete file",videos.get(i)+"");
                if (videos.get(i).exists()) {
                    videos.get(i).delete();
                }
            }
        }
        return outputFileName;
    }

    /**
     * Create directory and return file
     * returning video file
     */
    public static File getOutputMediaFile() {

        // External sdcard file location
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),
                VIDEO_DIRECTORY_NAME);
        // Create storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "VID_" + timeStamp + ".mp4");
        return mediaFile;
    }
}
