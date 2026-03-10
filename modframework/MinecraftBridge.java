package com.modframework.minecraft;

import com.modframework.core.ModFramework;

public class MinecraftBridge {

    private static MinecraftDetector.Environment env;

    public static void init() {
        env = MinecraftDetector.detect();
        
        switch (env) {
            case FABRIC:
                System.out.println("[OrangeML] Fabric detected - hooking into Fabric");
                initFabric();
                break;
            case FORGE:
                System.out.println("[OrangeML] Forge detected - hooking into Forge");
                initForge();
                break;
            case STANDALONE:
                System.out.println("[OrangeML] Vanilla Minecraft - running standalone");
                initStandalone();
                break;
            default:
                System.out.println("[OrangeML] Not a Minecraft environment");
                break;
        }
    }

    private static void initFabric() {
        ModFramework.getInstance().fireEvent("minecraft:fabric_init", null);
    }

    private static void initForge() {
        ModFramework.getInstance().fireEvent("minecraft:forge_init", null);
    }

    private static void initStandalone() {
        ModFramework.getInstance().fireEvent("minecraft:standalone_init", null);
    }

    public static MinecraftDetector.Environment getEnvironment() {
        return env;
    }
}