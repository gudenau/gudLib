package net.gudenau.lib.api.helper;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.lang.reflect.Constructor;

/**
 * Created by gudenau on 3/14/2017.
 * <p>
 * GudLib
 *
 * @since 1.2.0.0
 *
 * @deprecated Forge changed the registry system, again.
 */
@Deprecated
public class RegistryHelper {
    public static ItemBlock register(Block block, ResourceLocation name){
        return register(block, null, ItemBlock.class, null, name);
    }

    public static ItemBlock register(Block block, Class<? extends TileEntity> tileClass, ResourceLocation name){
        return register(block, null, ItemBlock.class, null, name);
    }

    public static ItemBlock register(Block block, CreativeTabs creativeTab, ResourceLocation name){
        return register(block, null, ItemBlock.class, creativeTab, name);
    }

    public static ItemBlock register(Block block, Class<? extends TileEntity> tileClass, CreativeTabs creativeTab, ResourceLocation name){
        return register(block, null, ItemBlock.class, creativeTab, name);
    }

    public static <T extends ItemBlock> T register(Block block, Class<? extends TileEntity> tileClass, Class<T> itemClass, ResourceLocation name) {
        return register(block, tileClass, itemClass, null, name);
    }

    public static <T extends ItemBlock> T register(Block block, Class<? extends TileEntity> tileClass, Class<T> itemClass, CreativeTabs creativeTabs, ResourceLocation name) {
        try {
            T item = null;

            if(itemClass != null) {
                Constructor<T> itemConstructor = itemClass.getConstructor(Block.class);
                item = itemConstructor.newInstance(block);
                register(item, name);
            }

            if(creativeTabs != null){
                block.setCreativeTab(creativeTabs);
            }
            block.setUnlocalizedName(name.getResourcePath());
            block.setRegistryName(name);
            ForgeRegistries.BLOCKS.register(block);

            if(tileClass != null){
                GameRegistry.registerTileEntity(tileClass, name);
            }

            return item;
        }catch (ReflectiveOperationException e){
            throw new RuntimeException("Could not create block: " + name);
        }
    }

    public static Item register(Item item, CreativeTabs creativeTab, ResourceLocation name){
        if(creativeTab != null) {
            item.setCreativeTab(creativeTab);
        }
        item.setUnlocalizedName(name.getResourcePath());
        item.setRegistryName(name);
        ForgeRegistries.ITEMS.register(item);
        return item;
    }

    public static Item register(Item item, ResourceLocation name){
        return register(item, null, name);
    }
}
