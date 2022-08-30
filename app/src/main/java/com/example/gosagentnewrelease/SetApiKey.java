package com.example.gosagentnewrelease;

import com.yandex.mapkit.MapKitFactory;

class SetApiKey {
    private static boolean isActivate = false;

    public static void Activate() {
        if (isActivate)
            return;

        MapKitFactory.setApiKey(PluginData.MAPKIT_API_KEY);
        isActivate = true;
    }

}