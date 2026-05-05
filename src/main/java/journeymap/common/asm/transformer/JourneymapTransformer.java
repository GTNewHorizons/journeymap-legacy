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

        final boolean obf = JourneymapPlugin.isObf();
        final String CLICK_METHOD_NAME = obf ? "a" : "confirmClicked";
        final String CLICK_METHOD_DESC = "(ZI)V";

        outerloop:
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
                            break outerloop;
                        }
                    }
                }
            }
        }

        if (changed)
        {
            final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            return cw.toByteArray();
        }

        return basicClass;
    }
}
