package manager;

import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {

    // Atribut
    private Clip    bgmClip;     // musik background (loop)
    private float   bgmVolume  = 0.8f;
    private float   sfxVolume  = 1.0f;
    private boolean isMuted    = false;

    private static final String SOUND_PATH = "assets/sounds/";

    // Singleton
    private static SoundManager instance;
    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    // Play BGM (loop)
    public void playBGM(String fileName) {
        try {
            stopBGM(); // stop BGM sebelumnya
            File file = new File(SOUND_PATH + fileName + ".wav");
            if (!file.exists()) {
                System.out.println("BGM tidak ditemukan: " + file.getAbsolutePath());
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(ais);
            setVolume(bgmClip, bgmVolume);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();
        } catch (Exception e) {
            System.out.println("Error play BGM: " + e.getMessage());
        }
    }

    // Stop BGM
    public void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
    }

    // Play SFX (sekali putar)
    public void playSFX(String fileName) {
        if (isMuted) return;
        try {
            File file = new File(SOUND_PATH + fileName + ".wav");
            if (!file.exists()) {
                System.out.println("SFX tidak ditemukan: " + file.getAbsolutePath());
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            setVolume(clip, sfxVolume);
            clip.start();
            // Auto close setelah selesai
            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
        } catch (Exception e) {
            System.out.println("Error play SFX: " + e.getMessage());
        }
    }

    // Set volume
    private void setVolume(Clip clip, float volume) {
        try {
            FloatControl fc = (FloatControl) clip.getControl(
                    FloatControl.Type.MASTER_GAIN);
            float dB = (float)(Math.log(volume) / Math.log(10.0) * 20.0);
            fc.setValue(Math.max(fc.getMinimum(), Math.min(dB, fc.getMaximum())));
        } catch (Exception e) {
            System.out.println("Volume control tidak tersedia");
        }
    }

    // Mute / Unmute
    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            stopBGM();
        } else {
            // BGM akan diplay lagi dari GamePanel
        }
        System.out.println("Mute: " + isMuted);
    }

    public boolean isMuted() { return isMuted; }
}
