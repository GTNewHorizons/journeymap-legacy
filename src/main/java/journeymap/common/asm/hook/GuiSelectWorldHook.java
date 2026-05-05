package journeymap.common.asm.hook;

import journeymap.client.Constants;
import journeymap.common.Journeymap;
import net.minecraft.client.Minecraft;
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
