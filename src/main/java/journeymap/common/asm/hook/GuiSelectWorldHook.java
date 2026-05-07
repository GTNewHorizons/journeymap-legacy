package journeymap.common.asm.hook;

import journeymap.client.Constants;
import journeymap.client.ui.dialog.GuiYesNoJMPrompt;
import journeymap.common.Journeymap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GuiSelectWorldHook
{

    public static String beforeWorldDeletion(String original)
    {
        try
        {
            GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
            if (currentScreen instanceof GuiYesNoJMPrompt)
            {
                GuiYesNoJMPrompt prompt = (GuiYesNoJMPrompt) currentScreen;
                Journeymap.getLogger().debug("JM Data deletion choice: {}", prompt.getJMDataChoice());
                if (!prompt.getJMDataChoice()) {
                    return original;
                }
            }

            Path jmWorldDataFullPath = Minecraft.getMinecraft().mcDataDir.toPath().resolve(Constants.SP_DATA_DIR).resolve(original);
            if (Files.isDirectory(jmWorldDataFullPath)) {
                try {
                    FileUtils.deleteDirectory(jmWorldDataFullPath.toFile());
                } catch (IOException e) {
                    Journeymap.getLogger().warn("Failed to delete VP world data found at {}", original);
                }
            }
        } catch (Throwable ignored)
        {
        }
        return original;
    }

}
