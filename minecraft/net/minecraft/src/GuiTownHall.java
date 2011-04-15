package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class GuiTownHall extends GuiInformator
{

    public GuiTownHall(InventoryPlayer inventoryplayer, TileEntityTownHall tileentitytownhall)
    {
        super(inventoryplayer, tileentitytownhall);
		xSize = 174;
        ySize = 246;
		townhall = tileentitytownhall;
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
        fontRenderer.drawString("TownHall", 60, 6, 0x011f36);
		if(page == 0)
		{
			fontRenderer.drawString("City Name:", 6, 18, 0x011f36);
			fontRenderer.drawString(" - Citizens -", 6, 32, 0x011f36);
			fontRenderer.drawString("Settlers:", 14, 42, 0x011f36);
			fontRenderer.drawString("Burgesses:", 14, 52, 0x011f36);
			fontRenderer.drawString("Esquires:", 14, 62, 0x011f36);
			fontRenderer.drawString("Nobles:", 14, 72, 0x011f36);
			fontRenderer.drawString("Clerics:", 14, 82, 0x011f36);
			fontRenderer.drawString("Gains:", 6, 94, 0x011f36);
			fontRenderer.drawString("Expenses:", 6, 104, 0x011f36);
			fontRenderer.drawString("Balance:", 6, 114, 0x011f36);
			fontRenderer.drawString(" - Tax Height -", 6, 126, 0x011f36);
			fontRenderer.drawString("Settlers:", 14, 136, 0x011f36);
			fontRenderer.drawString("Burgesses:", 14, 146, 0x011f36);
			fontRenderer.drawString("Esquires:", 14, 156, 0x011f36);
			fontRenderer.drawString("Nobles:", 14, 166, 0x011f36);
			fontRenderer.drawString("Clerics:", 14,176, 0x011f36);

			fontRenderer.drawString(townhall.cityName, 80, 18, 0x3f5464);
			fontRenderer.drawString((new StringBuilder().append(townhall.numberOfSettlers).toString()), 120, 42, 0x3f5464);
			fontRenderer.drawString((new StringBuilder().append(townhall.numberOfBurgesses).toString()), 120, 52, 0x3f5464);
			fontRenderer.drawString((new StringBuilder().append(townhall.numberOfEsquires).toString()), 120, 62, 0x3f5464);
			fontRenderer.drawString((new StringBuilder().append(townhall.numberOfNobles).toString()), 120, 72, 0x3f5464);
			fontRenderer.drawString((new StringBuilder().append(townhall.numberOfClerics).toString()), 120, 82, 0x3f5464);
			fontRenderer.drawString((new StringBuilder().append(townhall.gains).toString()), 120, 94, 0x3f5464);
			fontRenderer.drawString((new StringBuilder().append(townhall.expanses).toString()), 120, 104, 0x3f5464);
			fontRenderer.drawString((new StringBuilder().append(townhall.gains - townhall.expanses).toString()), 120, 114, 0x3f5464);
			fontRenderer.drawString((new StringBuilder().append(townhall.taxHeightforSettlers).toString()), 120, 136, 0x3f5464);
			fontRenderer.drawString((new StringBuilder().append(townhall.taxHeightforBurgesses).toString()), 120, 146, 0x3f5464);
			fontRenderer.drawString((new StringBuilder().append(townhall.taxHeightforEsquires).toString()), 120, 156, 0x3f5464);
			fontRenderer.drawString((new StringBuilder().append(townhall.taxHeightforNobles).toString()), 120, 166, 0x3f5464);
			fontRenderer.drawString((new StringBuilder().append(townhall.taxHeightforClerics).toString()), 120,176, 0x3f5464);
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

	/*protected void setCityName(String name)
	{
		this.cityName = name;
	}

	protected boolean setValue(int i, int j)
	{
		try
		{
			this.value[i] = j;
			return true;
		}
		catch(ArrayIndexOutOfBoundsException error)
		{
			return false;
		}
	}*/

	private int page = 0;
	//private String cityName = "<default>";
	private int[] value = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private TileEntityTownHall townhall;
}
