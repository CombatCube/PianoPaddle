package com.combatcube.pianopaddle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrew on 2016-10-28.
 */

public class Constants {
    public static final Map<String, String> myMap;
    static {
        Map<String, String> LEADERBOARD_IDS = new HashMap<String, String>();
        LEADERBOARD_IDS.put("Alla_Turca.mid", "CgkIoYKMtJsREAIQBQ");
        LEADERBOARD_IDS.put("BWV_934.mid", "CgkIoYKMtJsREAIQBg");
        LEADERBOARD_IDS.put("Flight_of_the_Bumblebee.mid", "CgkIoYKMtJsREAIQBw");
        LEADERBOARD_IDS.put("Für_Elise.mid", "CgkIoYKMtJsREAIQCA");
        LEADERBOARD_IDS.put("Gymnopédie_1.mid", "CgkIoYKMtJsREAIQCQ");
        LEADERBOARD_IDS.put("Maple_Leaf_Rag.mid", "CgkIoYKMtJsREAIQCg");
        LEADERBOARD_IDS.put("Moonlight_Sonata.mid", "CgkIoYKMtJsREAIQCw");
        LEADERBOARD_IDS.put("Nocturne.mid", "CgkIoYKMtJsREAIQDA");
        LEADERBOARD_IDS.put("The_Entertainer.mid", "CgkIoYKMtJsREAIQDQ");
        myMap = Collections.unmodifiableMap(LEADERBOARD_IDS);
    }
}
