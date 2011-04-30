package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;



public class GuiHut extends GuiContainer
{

    public GuiHut(IInventory iinventory, IInventory iinventory1)
    {
        super(new ContainerHut(iinventory, iinventory1));
        inventoryRows = 0;
        upperChestInventory = iinventory;
        lowerChestInventory = iinventory1;
        field_948_f = false;
        char c = '\336';
        int i = c - 108;
        inventoryRows = iinventory1.getSizeInventory() / 9;
        ySize = i + inventoryRows * 18;
		page = 0;
    }

    protected void drawGuiContainerForegroundLayer()
    {
        fontRenderer.drawString(lowerChestInventory.getInvName(), 8, 6, 0x404040);
		if(page == 0)
		{
			fontRenderer.drawString(upperChestInventory.getInvName(), 8, (ySize - 96) + 2, 0x404040);
			drawRect(99, 5, 166, 15, 0x66000000);
			drawRect(99, 5, 165, 14, 0xffa3a3a3);
			fontRenderer.drawString("[Information]", 100, 6, 0x404040);
		}
		else if(page == 1)
		{
			drawRect(99, 5, 159, 15, 0x66000000);
			drawRect(99, 5, 158, 14, 0xffc4b4a2);
			fontRenderer.drawString("[Inventory]", 100, 6, 0x404040);
		}
	}

	protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
		if(i > ((width - xSize) / 2) + 95 && j > ((height - ySize) / 2) + 5 && i < ((width - xSize) / 2) + 170 && j < ((height - ySize) / 2) + 15)
		{
			if(page == 0)
			{
				page = 1;
			}
			else if(page == 1)
			{
				page = 0;
			}
		}
    }

	public void drawScreen(int i, int j, float f)
    {
		if(page == 0)
		{
			super.drawScreen(i, j, f);
		}
		if(page == 1)
		{
			drawDefaultBackground();
			int k = (width - xSize) / 2;
			int l = (height - ySize) / 2;
			drawGuiContainerBackgroundLayer(f);
			GL11.glPushMatrix();
			GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
			RenderHelper.enableStandardItemLighting();
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glTranslatef(k, l, 0.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);

			GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(2896 /*GL_LIGHTING*/);
			GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
			drawGuiContainerForegroundLayer();

			GL11.glPopMatrix();

			for(int h = 2; h < controlList.size(); h++)
			{
				GuiButton guibutton = (GuiButton)controlList.get(h);
				guibutton.drawButton(mc, i, j);
			}



			GL11.glEnable(2896 /*GL_LIGHTING*/);
			GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
		}
    }

    protected void drawGuiContainerBackgroundLayer(float f)
    {
		if(page == 0)
		{
			int i = mc.renderEngine.getTexture("/gui/container.png");
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.renderEngine.bindTexture(i);
			int j = (width - xSize) / 2;
			int k = (height - ySize) / 2;
			drawTexturedModalRect(j, k, 0, 0, xSize, inventoryRows * 18 + 17);
			drawTexturedModalRect(j, k + inventoryRows * 18 + 17, 0, 126, xSize, 96);
		}
		else if(page == 1)
		{
			int i = mc.renderEngine.getTexture("/gui/informator.png");
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.renderEngine.bindTexture(i);
			int j = (width - xSize) / 2;
			int k = (height - ySize-10) / 2;
			drawTexturedModalRect(j, k, 0, 0, xSize, ySize + 20);
		}
    }

    private IInventory upperChestInventory;
    private IInventory lowerChestInventory;
    private int inventoryRows;
	byte page;
}
