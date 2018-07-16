package net.gudenau.lib.core;

import net.gudenau.lib.api.core.ObfuscationHelper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.F_APPEND;
import static org.objectweb.asm.Opcodes.IF_ACMPEQ;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

/**
 * Created by gudenau on 12/26/2016.
 * <p>
 * lib
 */
@SuppressWarnings("unused")
public class ClientTransformer implements IClassTransformer {
    private static final String obfuscatedTileEntityItemStackRenderer;
    private static final String obfuscatedRenderByItemName;
    private static final String obfuscatedRenderByItemDescription;
    private static final String obfuscatedItem;
    private static final String obfuscatedItemStack;
    private static final String obfuscatedGuiMainMenu;

    static{
        obfuscatedTileEntityItemStackRenderer = ObfuscationHelper.getObfuscatedClassNameForASM("net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer");
        obfuscatedItem = ObfuscationHelper.getObfuscatedClassNameForASM("net.minecraft.item.Item");
        obfuscatedItemStack = ObfuscationHelper.getObfuscatedClassNameForASM("net.minecraft.item.ItemStack");
        obfuscatedGuiMainMenu = ObfuscationHelper.getObfuscatedClassNameForASM("net.minecraft.client.gui.GuiMainMenu");

        obfuscatedRenderByItemName = ObfuscationHelper.getObfuscatedMethodNameForASM("net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer.renderByItem", "(Lnet.minecraft.item.ItemStack;)V");
        obfuscatedRenderByItemDescription = ObfuscationHelper.getObfuscatedMethodDescriptionForASM("net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer.renderByItem", "(Lnet.minecraft.item.ItemStack;)V");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if("net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer".equals(name)){
            return transformTileEntityItemStackRenderer(basicClass, false);
        }else if(obfuscatedTileEntityItemStackRenderer.equals(name)){
            return transformTileEntityItemStackRenderer(basicClass, true);
        }else if("net.minecraft.client.gui.GuiMainMenu".equals(name) || obfuscatedGuiMainMenu.equals(name)){
            return transformMainMenu(basicClass);
        }

        return basicClass;
    }

    private byte[] transformMainMenu(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        for(MethodNode method : classNode.methods){
            if("<init>".equals(method.name) && "()V".equals(method.desc)){
                InsnList instructions = method.instructions;
                for(int i = 0; i < instructions.size(); i++){
                    AbstractInsnNode abstractInsnNode = instructions.get(i);
                    if(abstractInsnNode instanceof FrameNode){
                        FrameNode frameNode = (FrameNode) abstractInsnNode;
                        if(frameNode.type == F_APPEND &&
                                frameNode.local != null &&
                                frameNode.local.size() == 1 &&
                                "java/lang/String".equals(frameNode.local.get(0))){
                            InsnList newInstructions = new InsnList();
                            newInstructions.add(new VarInsnNode(ALOAD, 2));
                            newInstructions.add(new MethodInsnNode(INVOKESTATIC, "net/gudenau/lib/core/ClientHooks", "onSplashTextLoaded", "(Ljava/util/List;)V", false));
                            instructions.insert(frameNode, newInstructions);
                            break;
                        }
                    }
                }
            }
        }

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private byte[] transformTileEntityItemStackRenderer(byte[] basicClass, boolean obfuscated) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        String methodName = obfuscated ? obfuscatedRenderByItemName : "renderByItem";
        String methodDescription = obfuscated ? obfuscatedRenderByItemDescription : "(Lnet/minecraft/item/ItemStack;)V";
        String itemName = obfuscated ? obfuscatedItem : "net/minecraft/item/Item";
        String itemStackName = obfuscated ? obfuscatedItemStack : "net/minecraft/item/ItemStack";

        for(MethodNode method : classNode.methods){
            if(methodName.equals(method.name) &&
                    methodDescription.equals(method.desc)){
                AbstractInsnNode startNode = null;
                AbstractInsnNode endNode = null;

                InsnList instructions = method.instructions;
                for(int i = 0; i < instructions.size(); i++){
                    AbstractInsnNode abstractInsnNode = instructions.get(i);
                    if(abstractInsnNode instanceof MethodInsnNode){
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                        if("net/minecraftforge/client/ForgeHooksClient".equals(methodInsnNode.owner) &&
                                "renderTileItem".equals(methodInsnNode.name) &&
                                ("(L" + itemName + ";I)V").equals(methodInsnNode.desc) &&
                                INVOKESTATIC == methodInsnNode.getOpcode()){
                            endNode = abstractInsnNode;
                            break;
                        }
                    }
                }

                if(endNode == null){
                    //noinspection RedundantStringFormatCall
                    System.err.printf("Unable to transform TileEntityItemStackRenderer, endNode is null!\n");
                    return null;
                }

                for(int i = instructions.indexOf(endNode); i > 0; i--){
                    AbstractInsnNode abstractInsnNode = instructions.get(i);
                    if(abstractInsnNode instanceof JumpInsnNode){
                        JumpInsnNode jumpInsnNode = (JumpInsnNode) abstractInsnNode;
                        if(IF_ACMPEQ == jumpInsnNode.getOpcode()){
                            startNode = abstractInsnNode;
                            break;
                        }
                    }
                }

                if(startNode == null){
                    //noinspection RedundantStringFormatCall
                    System.err.printf("Unable to transform TileEntityItemStackRenderer, startNode is null!\n");
                    return null;
                }

                InsnList preInstructions = new InsnList();
                InsnList postInstructions = new InsnList();

                preInstructions.add(new VarInsnNode(ALOAD, 1));
                preInstructions.add(new MethodInsnNode(INVOKESTATIC, "net/gudenau/lib/core/ClientHooks", "preItemTileRender", "(L" + itemStackName + ";)V", false));

                postInstructions.add(new VarInsnNode(ALOAD, 1));
                postInstructions.add(new MethodInsnNode(INVOKESTATIC, "net/gudenau/lib/core/ClientHooks", "postItemTileRender", "(L" + itemStackName + ";)V", false));

                instructions.insert(startNode, preInstructions);
                instructions.insert(endNode, postInstructions);

                break;
            }
        }

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
