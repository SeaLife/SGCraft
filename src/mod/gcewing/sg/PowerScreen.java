//------------------------------------------------------------------------------------------------
//
//   SG Craft - Power unit gui screen
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import static org.lwjgl.opengl.GL11.*;

import net.minecraft.entity.player.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.text.DecimalFormat;

public class PowerScreen extends BaseGui.Screen {

    final static int guiWidth = 148;
    final static int guiHeight = 64;
    final static DecimalFormat dFormat = new DecimalFormat("###,###,###");

    PowerTE te;
    
    public static PowerScreen create(EntityPlayer player, World world, BlockPos pos) {
        PowerContainer container = PowerContainer.create(player, world, pos);
        if (container != null)
            return new PowerScreen(container);
        else
            return null;
    }
    
    public PowerScreen(PowerContainer container) {
        super(container, guiWidth, guiHeight);
        this.te = container.te;
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void drawBackgroundLayer() {
        bindTexture(SGCraft.getInstance().resourceLocation("textures/gui/power_gui.png"), 148, 64);
        drawTexturedRect(0, 0, guiWidth, guiHeight, 0, 0);
        int cx = xSize / 2;
        drawCenteredString(te.getScreenTitle(), cx, 8);
        drawRightAlignedString(te.getUnitName()+":", 90, 23);
        drawRightAlignedString(dFormat.format(Math.min(Math.max(te.energyBuffer, 0), te.energyMax)), 141, 23);
        drawRightAlignedString("SGPU:", 90, 34);
        drawRightAlignedString(dFormat.format(Math.min(Math.max(te.energyBuffer/te.energyPerSGEnergyUnit, 0), te.energyMax)), 141, 34);
        drawRightAlignedString("Max:", 90, 45);
        drawRightAlignedString(dFormat.format(te.energyMax), 141, 45);
         drawPowerGauge();
    }
    
    void drawPowerGauge() {
        gSave();
        glPushAttrib(GL_ENABLE_BIT | GL_CURRENT_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        setColor(1, 0, 0);
        drawRect(23, 28, 29 * te.energyBuffer / te.energyMax, 10);
        glBlendFunc(GL_ONE, GL_ZERO);
        glPopAttrib();
        gRestore();
    }

}
