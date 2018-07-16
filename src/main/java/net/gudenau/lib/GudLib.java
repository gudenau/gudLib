package net.gudenau.lib;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;

/**
 * Created by gudenau on 12/26/2016.
 * <p>
 * lib
 */
public class GudLib extends DummyModContainer {
    public GudLib(){
        super(new ModMetadata());

        ModMetadata modMetadata = getMetadata();
        modMetadata.authorList = Collections.singletonList("gudenau");
        modMetadata.description = "A library mod by gudenau";
        modMetadata.modId = "gud_lib";
        modMetadata.name = "GudLib";
        modMetadata.version = "1.2.0.0";
    }

    private File source;

    @Override
    public File getSource(){
        if(source != null){
            return source;
        }

        URL url = getClass().getProtectionDomain().getCodeSource().getLocation();

        String path = url.getPath();
        try {
            path = URLDecoder.decode(path, "UTF-8").replaceAll("!*[/\\\\]net[/\\\\]gudenau[/\\\\]lib[/\\\\]GudLib.class", "").replaceAll("file:[/\\\\]", "");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error while finding gudLib location!", e);
        }

        return source = new File(path);
    }

    @Override
    public Disableable canBeDisabled(){
        return Disableable.NEVER;
    }

    @Override
    public Class<?> getCustomResourcePackClass() {
        return getSource().isDirectory() ? FMLFolderResourcePack.class : FMLFileResourcePack.class;
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller){
        bus.register(this);
        return true;
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent event){
        MinecraftForge.EVENT_BUS.register(new SplashAdder());
    }
}
