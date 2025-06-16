package services;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SoundService {
    private static SoundService instance;
    private Map<String, Clip> soundClips;
    private boolean soundEnabled;

    private SoundService() {
        soundClips = new HashMap<>();
        soundEnabled = true;
    }

    public static SoundService getInstance() {
        if (instance == null) {
            instance = new SoundService();
        }
        return instance;
    }

    public boolean loadSound(String name, String filePath) {
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                return false;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            soundClips.put(name, clip);
            return true;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean playSound(String name) {
        if (!soundEnabled) return false;

        Clip clip = soundClips.get(name);
        if (clip != null) {
            try {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
                clip.start();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean loopSound(String name) {
        if (!soundEnabled) return false;

        Clip clip = soundClips.get(name);
        if (clip != null) {
            try {
                if (!clip.isRunning()) {
                    clip.setFramePosition(0);
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean stopSound(String name) {
        Clip clip = soundClips.get(name);
        if (clip != null && clip.isRunning()) {
            try {
                clip.stop();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void stopAllSounds() {
        for (Clip clip : soundClips.values()) {
            if (clip != null && clip.isRunning()) {
                try {
                    clip.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isSoundPlaying(String name) {
        Clip clip = soundClips.get(name);
        return clip != null && clip.isRunning();
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopAllSounds();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void unloadSound(String name) {
        Clip clip = soundClips.remove(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close();
        }
    }

    public void dispose() {
        for (Clip clip : soundClips.values()) {
            if (clip != null) {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.close();
            }
        }
        soundClips.clear();
    }

    public int getLoadedSoundCount() {
        return soundClips.size();
    }
}