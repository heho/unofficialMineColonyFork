package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;

public class GuiHut extends GuiContainer
{

    public GuiHut(IInventory pInventory, IInventory inventory)
	{
        super(new CraftingInventoryHutCB(pInventory, inventory));
        inventoryRows = 0;
        playerInventory = pInventory;
        chestInventory = inventory;
        field_948_f = false;
        char c = '\336';
        int i = c - 108;
        inventoryRows = 3;
        ySize = i + (inventoryRows + 1) * 18;
		page = 0;
		controlList = new ArrayList();
    }

	public void drawScreen(int i, int j, float f)
    {
		if(page == 0)
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
			Slot slot = null;
			for(int i1 = 0; i1 < inventorySlots.slots.size(); i1++)
			{
				Slot slot1 = (Slot)inventorySlots.slots.get(i1);
				drawSlotInventory(slot1);
				if(getIsMouseOverSlot(slot1, i, j))
				{
					slot = slot1;
					GL11.glDisable(2896 /*GL_LIGHTING*/);
					GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
					int j1 = slot1.xDisplayPosition;
					int l1 = slot1.yDisplayPosition;
					drawGradientRect(j1, l1, j1 + 16, l1 + 16, 0x80ffffff, 0x80ffffff);
					GL11.glEnable(2896 /*GL_LIGHTING*/);
					GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
				}
			}

			InventoryPlayer inventoryplayer = mc.thePlayer.inventory;
			if(inventoryplayer.getItemStack() != null)
			{
				GL11.glTranslatef(0.0F, 0.0F, 32F);
				itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, inventoryplayer.getItemStack(), i - k - 8, j - l - 8);
				itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, inventoryplayer.getItemStack(), i - k - 8, j - l - 8);
			}
			GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(2896 /*GL_LIGHTING*/);
			GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
			drawGuiContainerForegroundLayer();
			if(inventoryplayer.getItemStack() == null && slot != null && slot.getHasStack())
			{
				String s = (new StringBuilder()).append("").append(StringTranslate.getInstance().translateNamedKey(slot.getStack().func_20109_f())).toString().trim();
				if(s.length() > 0)
				{
					int k1 = (i - k) + 12;
					int i2 = j - l - 12;
					int j2 = fontRenderer.getStringWidth(s);
					drawGradientRect(k1 - 3, i2 - 3, k1 + j2 + 3, i2 + 8 + 3, 0xc0000000, 0xc0000000);
					fontRenderer.drawStringWithShadow(s, k1, i2, -1);
				}
			}
			GL11.glPopMatrix();

			for(int h = 0; h < 2; h++)
			{
				GuiButton guibutton = (GuiButton)controlList.get(h);
				guibutton.drawButton(mc, i, j);
			}

			GL11.glEnable(2896 /*GL_LIGHTING*/);
			GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
			return;
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

	private void drawSlotInventory(Slot slot)
    {
        int i = slot.xDisplayPosition;
        int j = slot.yDisplayPosition;
        ItemStack itemstack = slot.getStack();
        if(itemstack == null)
        {
            int k = slot.getBackgroundIconIndex();
            if(k >= 0)
            {
                GL11.glDisable(2896 /*GL_LIGHTING*/);
                mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/gui/items.png"));
                drawTexturedModalRect(i, j, (k % 16) * 16, (k / 16) * 16, 16, 16);
                GL11.glEnable(2896 /*GL_LIGHTING*/);
                return;
            }
        }
        itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, itemstack, i, j);
        itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, itemstack, i, j);
    }

	private boolean getIsMouseOverSlot(Slot slot, int i, int j)
    {
		if(page == 1)
		{
			return false;
		}
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        i -= k;
        j -= l;
        return i >= slot.xDisplayPosition - 1 && i < slot.xDisplayPosition + 16 + 1 && j >= slot.yDisplayPosition - 1 && j < slot.yDisplayPosition + 16 + 1;
    }

	public void initGui()
    {
        controlList.clear();
		controlList.add(new GuiButton(1, width / 2 - 80, height / 4 + 42, 70, 19, "Inventory"));
        controlList.add(new GuiButton(2, width / 2 + 5, height / 4 + 42, 70, 19, "Hut"));
		controlList.add(new GuiButton(3, width / 2 - 80, height / 4 + 150, 70, 19, "Inventory"));
        controlList.add(new GuiButton(4, width / 2 + 5, height / 4 + 150, 70, 19, "Hut"));
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
		if(guibutton.id == 3)
        {
            page = 0;
        }
		if(guibutton.id == 4)
        {
            page = 1;
        }
		drawGuiContainerForegroundLayer();
    }


    protected void drawGuiContainerForegroundLayer()
    {
		if(page == 0)
		{
			fontRenderer.drawString(chestInventory.getInvName(), 8, 6, 0x404040);
			fontRenderer.drawString(playerInventory.getInvName(), 8, (ySize - 96) + 7, 0x404040);
		}
		else if(page == 1)
		{
			fontRenderer.drawString("Hut", 75, 6, 0x011f36);
		}
    }

    protected void drawGuiContainerBackgroundLayer(float f)
    {
		if(page == 0)
		{
			int i = mc.renderEngine.getTexture("/gui/hut.png");
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.renderEngine.bindTexture(i);
			int j = (width - xSize) / 2;
			int k = (height - ySize) / 2;
			drawTexturedModalRect(j, k, 0, 0, xSize, (inventoryRows + 1) * 18 + 24);
			drawTexturedModalRect(j, k + (inventoryRows + 1) * 18 + 24, 0, 126, xSize, 96);
			return;
		}
		else if(page == 1)
		{
			int i = mc.renderEngine.getTexture("/gui/informator.png");
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.renderEngine.bindTexture(i);
			int j = (width - xSize) / 2;
			int k = (height - ySize - 10) / 2;
			drawTexturedModalRect(j, k, 0, 0, xSize, ySize + 20);
		}
    }

	private Slot getSlotAtPosition(int i, int j)
    {
		if(page == 1)
		{
			return null;
		}
        for(int k = 0; k < inventorySlots.slots.size(); k++)
        {
            Slot slot = (Slot)inventorySlots.slots.get(k);
            if(getIsMouseOverSlot(slot, i, j))
            {
                return slot;
            }
        }

        return null;
    }

	protected void mouseClicked(int i, int j, int k)
    {
        if(k == 0)
        {
            for(int l = 0; l < controlList.size(); l++)
            {
                GuiButton guibutton = (GuiButton)controlList.get(l);
                if(guibutton.mousePressed(mc, i, j))
                {
                    selectedButton = guibutton;
                    mc.sndManager.func_337_a("random.click", 1.0F, 1.0F);
                    actionPerformed(guibutton);
                }
            }
        }

		if(page == 0)
		{
			return;
		}

        if(k == 0 || k == 1)
        {
            Slot slot = getSlotAtPosition(i, j);
            int l = (width - xSize) / 2;
            int i1 = (height - ySize) / 2;
            boolean flag = i < l || j < i1 || i >= l + xSize || j >= i1 + ySize;
            int j1 = -1;
            if(slot != null)
            {
                j1 = slot.slotNumber;
            }
            if(flag)
            {
                j1 = -999;
            }
            if(j1 != -1)
            {
                mc.playerController.func_20085_a(inventorySlots.windowId, j1, k, mc.thePlayer);
            }
        }
    }


	private int page = 0;
    private IInventory playerInventory;
    private IInventory chestInventory;
    private int inventoryRows;
	private static RenderItem itemRenderer = new RenderItem();
	private GuiButton selectedButton;
}
