package journeymap.common.asm.transformer;

import journeymap.common.asm.JourneymapPlugin;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public final class JourneymapTransformer implements IClassTransformer
{

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (basicClass == null) return null;
        if ("net.minecraft.client.gui.GuiSelectWorld".equals(transformedName))
        {
            return transformGuiSelectWorld(basicClass);
        }
        return basicClass;
    }

    private byte[] transformGuiSelectWorld(byte[] basicClass)
    {
        final ClassReader cr = new ClassReader(basicClass);
        final ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        boolean changed = false;
        boolean changedTwo = false;
        boolean changedThree = false;

        final boolean obf = JourneymapPlugin.isObf();
        final String CLICK_METHOD_NAME = obf ? "a" : "confirmClicked";
        final String CLICK_METHOD_DESC = "(ZI)V";

        final String PROMPT_METHOD_NAME = obf ? "a" : "func_152129_a";
        final String PROMPT_METHOD_DESC = obf ? "(Lbcv;Ljava/lang/String;I)Lbcw;" : "(Lnet/minecraft/client/gui/GuiYesNoCallback;Ljava/lang/String;I)Lnet/minecraft/client/gui/GuiYesNo;";

        for (MethodNode mn : cn.methods)
        {
            if (CLICK_METHOD_NAME.equals(mn.name) && CLICK_METHOD_DESC.equals(mn.desc))
            {

                final String DEL_METHOD_OWNER = obf ? "aze" : "net/minecraft/world/storage/ISaveFormat";
                final String DEL_METHOD_NAME = obf ? "e" : "deleteWorldDirectory";
                final String DEL_METHOD_DESC = "(Ljava/lang/String;)Z";

                for (AbstractInsnNode node : mn.instructions.toArray())
                {
                    if (node.getOpcode() == Opcodes.INVOKEINTERFACE && node instanceof MethodInsnNode)
                    {
                        final MethodInsnNode mNode = (MethodInsnNode) node;
                        if (DEL_METHOD_OWNER.equals(mNode.owner) && DEL_METHOD_NAME.equals(mNode.name) && DEL_METHOD_DESC.equals(mNode.desc))
                        {
                            mn.instructions.insertBefore(node,
                                    new MethodInsnNode(
                                            Opcodes.INVOKESTATIC,
                                            "journeymap/common/asm/hook/GuiSelectWorldHook",
                                            "beforeWorldDeletion",
                                            "(Ljava/lang/String;)Ljava/lang/String;",
                                            false));
                            changed = true;
                            break;
                        }
                    }
                }
            }

            if (PROMPT_METHOD_NAME.equals(mn.name) && PROMPT_METHOD_DESC.equals(mn.desc))
            {
                final String OLD_GUI_YN = obf ? "bcw" : "net/minecraft/client/gui/GuiYesNo";
                final String NEW_GUI_YN = "journeymap/client/ui/dialog/GuiYesNoJMPrompt";

                for (AbstractInsnNode node : mn.instructions.toArray())
                {
                    if (node instanceof MethodInsnNode)
                    {
                        final MethodInsnNode mNode = (MethodInsnNode) node;
                        if (OLD_GUI_YN.equals(mNode.owner))
                        {
                            mNode.owner = NEW_GUI_YN;
                            changedTwo = true;
                        }
                    }
                    else if (node instanceof TypeInsnNode)
                    {
                        final TypeInsnNode tNode = (TypeInsnNode) node;
                        if (OLD_GUI_YN.equals(tNode.desc))
                        {
                            tNode.desc = NEW_GUI_YN;
                            changedThree = true;
                        }
                    }
                }
            }
        }

        if (changed && changedTwo && changedThree)
        {
            final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            return cw.toByteArray();
        }

        return basicClass;
    }
}
