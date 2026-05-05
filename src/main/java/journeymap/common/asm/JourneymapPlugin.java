package journeymap.common.asm;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.Name("JourneymapASM")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions("journeymap.common.asm.transformer")
public final class JourneymapPlugin implements IFMLLoadingPlugin
{

    private static Boolean isObf;

    @Override
    public String[] getASMTransformerClass()
    {
        if (FMLLaunchHandler.side().isClient())
        {
            return new String[]{"journeymap.common.asm.transformer.JourneymapTransformer"};
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        isObf = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }

    public static boolean isObf()
    {
        if (isObf == null)
        {
            throw new IllegalStateException("Obfuscation state has been accessed too early!");
        }
        return isObf;
    }
}
