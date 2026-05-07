package journeymap.client.ui.dialog;

import cpw.mods.fml.client.config.GuiCheckBox;
import journeymap.client.Constants;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;

public class GuiYesNoJMPrompt extends GuiYesNo {
    private boolean deleteJMData = true;
    private static final int CHECKBOX_ID = 90;

    public GuiYesNoJMPrompt(GuiYesNoCallback callback, String delQuestion, String delWarn, String confirmButton, String CancelButton, int worldIndex) {
        super(callback, delQuestion, delWarn, confirmButton, CancelButton, worldIndex);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiCheckBox(CHECKBOX_ID, this.width / 2 - 80, 110, " " + Constants.getString("jm.common.deleteworld_text"), this.deleteJMData ));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof GuiCheckBox && button.id == CHECKBOX_ID) {
            this.deleteJMData = ((GuiCheckBox) button).isChecked();
            return;
        }
        super.actionPerformed(button);
    }

    public boolean getJMDataChoice() {
        return this.deleteJMData;
    }
}
