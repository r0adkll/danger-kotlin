package com.r0adkll.danger.util;

import com.intellij.openapi.extensions.PluginId;

/**
 * PluginId was refactored to Kotlin from Java in ver. 251 --> 252.
 * So this breaks kotlin -> kotlin calls when building against 252 and running in 251
 * TODO: Remove when minBuild is 252+
 */
public class PluginIdHelper {

    public static PluginId getId(String idString) {
        return PluginId.getId(idString);
    }
}
