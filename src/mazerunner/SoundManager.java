package mazerunner;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    public static final String BACKGROUND_MUSIC = "background";
    public static final String BULLET_PICKUP = "bullet_pickup";
    public static final String BULLET_SHOOT = "bullet_shoot";
    public static final String BOMB_EXPLODE = "bomb_explode";
    public static final String MONSTER_KILL = "monster_kill";
    public static final String PORTAL_ENTER = "portal_enter";
    public static final String DOOR_OPEN = "door_open";
    public static final String PLAYER_CRASH = "player_crash";
    public static final String GAME_WIN = "game_win";
    public static final String GAME_OVER = "game_over";
    
    private static SoundManager instance;
    private Map<String, Clip> soundClips;
    private FloatControl bgMusicVolumeControl;
    private boolean soundEnabled = true;
    private float normalVolume = 0.0f;
    private float reducedVolume = -10.0f;
    
    private SoundManager() {
        soundClips = new HashMap<>();
        loadSounds();
    }
    
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    private void loadSounds() {
        loadSound(BACKGROUND_MUSIC, "/sounds/background.wav");
        loadSound(BULLET_PICKUP, "/sounds/bullet_pickup.wav");
        loadSound(BULLET_SHOOT, "/sounds/bullet_shoot.wav");
        loadSound(BOMB_EXPLODE, "/sounds/bomb_explode.wav");
        loadSound(MONSTER_KILL, "/sounds/monster_kill.wav");
        loadSound(PORTAL_ENTER, "/sounds/portal_enter.wav");
        loadSound(DOOR_OPEN, "/sounds/door_open.wav");
        loadSound(PLAYER_CRASH, "/sounds/player_crash.wav");
        loadSound(GAME_WIN, "/sounds/game_win.wav");
        loadSound(GAME_OVER, "/sounds/game_over.wav");
    }
    
    private void loadSound(String soundName, String path) {
        try {
            URL soundURL = getClass().getResource(path);
            
            if (soundURL == null) {
                File soundFile = new File("resources" + path);
                if (soundFile.exists()) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    soundClips.put(soundName, clip);
                    
                    if (soundName.equals(BACKGROUND_MUSIC)) {
                        bgMusicVolumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    }
                } else {
                    System.out.println("Sound file not found: " + path);
                }
            } else {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                soundClips.put(soundName, clip);
                
                if (soundName.equals(BACKGROUND_MUSIC)) {
                    bgMusicVolumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                }
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error loading sound " + path + ": " + e.getMessage());
        }
    }
    
    public void playSound(String soundName) {
        if (!soundEnabled) return;
        
        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    public void loopSound(String soundName) {
        if (!soundEnabled) return;
        
        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            
            if (soundName.equals(BACKGROUND_MUSIC) && bgMusicVolumeControl == null) {
                bgMusicVolumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }
        }
    }
    
    public void stopSound(String soundName) {
        Clip clip = soundClips.get(soundName);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
    
    public void stopAllSounds() {
        for (Clip clip : soundClips.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
    }
    
    public void toggleSound(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopAllSounds();
        }
    }
    
    public void setBackgroundMusicVolume(float volume) {
        if (bgMusicVolumeControl != null) {
            bgMusicVolumeControl.setValue(volume);
        }
    }
    
    public void reduceBackgroundMusic() {
        setBackgroundMusicVolume(reducedVolume);
    }
    
    public void restoreBackgroundMusic() {
        setBackgroundMusicVolume(normalVolume);
    }
}