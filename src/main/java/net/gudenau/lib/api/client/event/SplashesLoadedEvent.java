package net.gudenau.lib.api.client.event;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by gudenau on 1/11/2017.
 * <p>
 * GudLib
 */
@Event.HasResult
public class SplashesLoadedEvent extends Event {
    private final List<String> splashes;

    public SplashesLoadedEvent(List<String> splashes) {
        this.splashes = splashes;
    }

    public void addSplash(String splash){
        splashes.add(splash);
    }

    public void addSplashes(String[] splashes){
        Collections.addAll(this.splashes, splashes);
    }

    public void addSplashes(Collection<String> splashes){
        this.splashes.addAll(splashes);
    }

    public List<String> getSplashes(){
        return splashes;
    }
}
