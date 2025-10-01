package com.newgame.teamtilt.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class LevelProgress {
    private static final String PREF_NAME = "teamtilt.progress";
    private static final String KEY_COMPLETED = "completed";

    private static final Set<String> completed = new HashSet<>();
    private static boolean loaded = false;

    private LevelProgress() {}

    public static synchronized void markCompleted(int worldIndex, int levelIndex) {
        ensureLoaded();
        if (completed.add(key(worldIndex, levelIndex))) {
            persist();
        }
    }

    public static synchronized boolean isCompleted(int worldIndex, int levelIndex) {
        ensureLoaded();
        return completed.contains(key(worldIndex, levelIndex));
    }

    private static void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        try {
            Preferences prefs = Gdx.app.getPreferences(PREF_NAME);
            String saved = prefs.getString(KEY_COMPLETED, "");
            if (saved != null && !saved.isEmpty()) {
                completed.clear();
                completed.addAll(Arrays.stream(saved.split(";"))
                        .filter(s -> s != null && !s.isEmpty())
                        .collect(Collectors.toSet()));
            }
        } catch (Exception ignored) {
            // If preferences fail, keep in-memory only
        }
    }

    private static void persist() {
        try {
            Preferences prefs = Gdx.app.getPreferences(PREF_NAME);
            String joined = String.join(";", completed);
            prefs.putString(KEY_COMPLETED, joined);
            prefs.flush();
        } catch (Exception ignored) {
            // Ignore persistence errors to avoid crashing the game
        }
    }

    private static String key(int w, int l) {
        return w + ":" + l;
    }
}


