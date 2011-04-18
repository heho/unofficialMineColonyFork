package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class GuiMarket extends GuiInformator
{

    public GuiMarket(InventoryPlayer inventoryplayer, TileEntityMarket tileentitymarket)
    {
        super(inventoryplayer, tileentitymarket);
		xSize = 174;
        ySize = 246;
		market = tileentitymarket;
		page = 0;
    }


    public void initGui()
    {
        controlList.clear();
		controlList.add(new GuiButton(1, width / 2 - 80, height / 4 + 160, 70, 20, "Informations"));
        controlList.add(new GuiButton(2, width / 2 + 5, height / 4 + 160, 70, 20, "Actions"));
        if(mc.session == null)
        {
            ((GuiButton)controlList.get(1)).enabled = false;
        }
    }

	protected void actionPerformed(GuiButton guibutton)
    {
        if(guibutton.id != 0);
        if(guibutton.id == 1)
        {
            page = 0;
        }
		if(guibutton.id == 2)
        {
            page = 1;
        }
		drawGuiContainerForegroundLayer();
    }

    protected void drawGuiContainerForegroundLayer()
    {
        fontRenderer.drawString("Market", 60, 6, 0x011f36);
		if(page == 0)
		{

		}
    }

    protected void drawGuiContainerBackgroundLayer(float f)
    {
		
        int i = mc.renderEngine.getTexture("/gui/informator.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(i);
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2;
        drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
    }

	private int page = 0;
	//private String cityName = "<default>";
	private int[] value = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private TileEntityMarket market;
}
