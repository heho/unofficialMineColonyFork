package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class GuiChanger extends GuiContainer
{

    public GuiChanger(InventoryPlayer inventoryplayer, TileEntityChanger tileentitychanger)
    {
        super(new CraftingInventoryChangerCB(inventoryplayer, tileentitychanger));
        changerInventory = tileentitychanger;
    }

    protected void drawGuiContainerForegroundLayer()
    {
        fontRenderer.drawString("Changing", 60, 6, 0x404040);
        fontRenderer.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float f)
    {
		
        int i = mc.renderEngine.getTexture("/gui/banking.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(i);
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2;
        drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
    }

    private TileEntityChanger changerInventory;
}
