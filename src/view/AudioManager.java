/**
 * File: AudioManager.java
 *
 * Purpose:
 *      Centralized audio controller for background music playback.
 *
 *      This class manages music playback, playlists, crossfading, looping,
 *      volume control, and trainer-specific background music sets.
 *
 *      Implemented as a Singleton so that only one MediaPlayer instance
 *      controls audio across the entire application.
 */

package view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import models.TrainerRole;

/**
 * Singleton manager responsible for all background music behavior.
 * 
 * Handles looping tracks, shuffled playlists, crossfade transitions,
 * and global volume management.
 */
public class AudioManager {

	/** Singleton instance of AudioManager. */
	private static AudioManager instance;

	/** Active MediaPlayer instance. */
    private MediaPlayer mediaPlayer;

    /** Currently playing music resource path. */
    private String currentMusicPath;
    
    /** Playlist of music tracks for trainer-specific themes. */
    private List<String> playlist;

    /** Index of the currently queued song in the playlist. */
    private int playlistIndex = 0;
    
    /** Global master music volume (0.0 – 1.0). */
    private double masterVolume = 0.5;
    
    /**
     * Retrieves the singleton instance of the AudioManager.
     *
     * @return the shared AudioManager instance
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    /**
     * Core internal music playback handler.
     *
     * Handles:
     * - Resource loading
     * - Looping or playlist playback
     * - Crossfade transitions
     *
     * @param resourcePath path to the audio file
     * @param offsetSeconds starting offset in seconds
     * @param fadeDuration fade duration in milliseconds
     * @param loop whether the track should loop indefinitely
     */
    private void playInternal(String resourcePath, Double offsetSeconds, Double fadeDuration, boolean loop) {
        if (loop && resourcePath.equals(currentMusicPath)) {
            return; 
        }

        MediaPlayer oldPlayer = mediaPlayer;

        URL resource = getClass().getResource(resourcePath);
        if (resource == null) {
            System.out.println("Music not found: " + resourcePath);
            if (!loop && playlist != null) playNextSong();
            return;
        }
        
        Media sound = new Media(resource.toExternalForm());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setStartTime(Duration.seconds(offsetSeconds));
        
        if (loop) {
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        } else {
            mediaPlayer.setCycleCount(1);
            mediaPlayer.setOnEndOfMedia(this::playNextSong);
        }
        
        mediaPlayer.setVolume(0);
        mediaPlayer.play();
        
        this.currentMusicPath = resourcePath;
        
        Timeline crossFade = new Timeline();

        crossFade.getKeyFrames().add(
            new KeyFrame(Duration.millis(fadeDuration),
            new KeyValue(mediaPlayer.volumeProperty(), masterVolume))
        );

        if (oldPlayer != null) {
            crossFade.getKeyFrames().add(
                new KeyFrame(Duration.millis(fadeDuration), 
                new KeyValue(oldPlayer.volumeProperty(), 0))
            );
            
            crossFade.setOnFinished(e -> {
                oldPlayer.stop();
                oldPlayer.dispose();
            });
        }

        crossFade.play();
    }
    
    /**
     * Initializes and plays a shuffled playlist for a specific trainer role.
     *
     * @param tr the selected TrainerRole
     */
    public void setPlaylist(TrainerRole tr) {
    	String folderPath = "/view/assets/audio/BackgroundMusic/" + tr.getName() + "/";
    	
    	playlist = new ArrayList<>();
    	
    	for (int i = 1; i <= tr.getSongCount(); i++) {
    		playlist.add(folderPath + tr.getName() + "_" + Integer.toString(i) + ".mp3");
    	}
        
        if (!playlist.isEmpty()) {
            Collections.shuffle(playlist);
            playlistIndex = 0;
            System.out.println("Starting shuffled playlist with " + playlist.size() + " songs.");
            playNextSong();
        } else {
            System.out.println("No .mp3 files found in playlist folder.");
        }
    }
    
    /**
     * Advances to and plays the next song in the active playlist.
     */
    private void playNextSong() {
        if (playlist == null || playlist.isEmpty()) return;

        String nextSong = playlist.get(playlistIndex);
        
        playlistIndex = (playlistIndex + 1) % playlist.size();
        
        playInternal(nextSong, 0.0, 500.0, false);
    }
    
    /**
     * Plays a looping background track and clears any active playlist.
     *
     * @param resourcePath path to music file
     */
    public void playMusic(String resourcePath) {
    	this.playlist = null; 
    	playInternal(resourcePath, 0.0, 500.0, true);
    }
    
    /**
     * Plays a looping background track with a custom start offset and fade duration.
     *
     * @param resourcePath path to music file
     * @param offsetSeconds starting offset in seconds
     * @param fadeDuration fade duration in milliseconds
     */
    public void playMusic(String resourcePath, Double offsetSeconds, Double fadeDuration) {
        this.playlist = null; 
        playInternal(resourcePath, offsetSeconds, fadeDuration, true);
    }
    
    /**
     * Stops music playback with a fade-out effect.
     *
     * @param fade fade duration in milliseconds
     */
    public void stopMusic(double fade) {
    	if (mediaPlayer != null) {
            Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.millis(fade), 
                new KeyValue(mediaPlayer.volumeProperty(), 0))
            );
            fadeOut.setOnFinished(e -> {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
                currentMusicPath = null;
            });
            fadeOut.play();
        }
    	this.playlist = null;
    }
    
    /**
     * Stops music playback using the default fade duration.
     */
    public void stopMusic() { stopMusic(500.0); }
    
    /**
     * Sets the global master music volume.
     *
     * @param volume value between 0.0 and 1.0
     */
    public void setMasterVolume(double volume) {
        this.masterVolume = Math.max(0.0, Math.min(1.0, volume));
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(this.masterVolume);
        }
    }
    
    /**
     * @return current master volume value
     */
    public double getMasterVolume() {
        return masterVolume;
    }
    
    /**
     * @return the active MediaPlayer instance
     */
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
