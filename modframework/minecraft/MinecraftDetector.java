package com.modframework.minecraft;

public class MinecraftDetector {
    
    public enum Environment {
        FABRIC, FORGE, STANDALONE, NOT_MINECRAFT
    }
    
    public static Environment detect() {
        // Check for Fabric
        try {
            Class.forName("net.fabricmc.api.ModInitializer");
            return Environment.FABRIC;
        } catch (ClassNotFoundException ignored) {}
        
        // Check for Forge
        try {
            Class.forName("net.minecraftforge.fml.common.Mod");
            return Environment.FORGE;
        } catch (ClassNotFoundException ignored) {}
        
        // Check for vanilla Minecraft
        try {
            Class.forName("net.minecraft.client.Minecraft");
            return Environment.STANDALONE;
        } catch (ClassNotFoundException ignored) {}
        
        return Environment.NOT_MINECRAFT;
    }
}