package net.gudenau.lib.api.helper;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;

/**
 * Created by gudenau on 1/10/2017.
 * <p>
 * GudLib
 *
 * Use @{link net.gudenau.lib.api.helper.RegistryHelper RegistryHelper} instead!
 */
@Deprecated
public class RegisterHelper {
    @Deprecated
    public static ItemBlock register(Block block, ResourceLocation name){
        return RegistryHelper.register(block, name);
    }

    @Deprecated
    public static ItemBlock register(Block block, Class<? extends TileEntity> tileClass, ResourceLocation name){
        return RegistryHelper.register(block, tileClass, name);
    }

    @Deprecated
    public static <T extends ItemBlock> T register(Block block, Class<? extends TileEntity> tileClass, Class<T> itemClass, ResourceLocation name) {
        return RegistryHelper.register(block, tileClass, itemClass, null, name);
    }

    @Deprecated
    public static Item register(Item item, ResourceLocation name){
        return RegistryHelper.register(item, name);
    }

    @Deprecated
    public static <T extends ItemBlock> T register(Block block, Class<? extends TileEntity> tileClass, Class<T> itemClass, CreativeTabs creativeTabs, ResourceLocation name) {
        return RegistryHelper.register(block, tileClass, itemClass, creativeTabs, name);
    }
}
