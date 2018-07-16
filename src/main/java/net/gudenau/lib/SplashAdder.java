package net.gudenau.lib;

import net.gudenau.lib.api.client.event.SplashesLoadedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gudenau on 1/11/2017.
 * <p>
 * GudLib
 */
public class SplashAdder {
    private static final List<String> splashes = new ArrayList<>();

    @SubscribeEvent
    public void onSplashesLoaded(SplashesLoadedEvent event){
        if(splashes.isEmpty()){
            IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();

            try(BufferedReader reader = new BufferedReader(new InputStreamReader(resourceManager.getResource(new ResourceLocation("gud_lib", "splashes.txt")).getInputStream()))){
                for(String line = reader.readLine(); line != null; line = reader.readLine()){
                    if(!line.isEmpty()) {
                        splashes.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        event.addSplashes(splashes);
    }
}
