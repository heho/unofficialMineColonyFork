package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class GuiHut extends GuiChest
{

    public GuiHut(IInventory iinventory, IInventory iinventory1)
    {
        super(iinventory, iinventory1);
    }

	public void initGui()
    {
        controlList.clear();
		controlList.add(new GuiButton(1, width / 2 - 80, 0, 70, 19, "Inventory"));
        controlList.add(new GuiButton(2, width / 2 + 5, 0, 70, 19, "Hut"));
        if(mc.session == null)
        {
            ((GuiButton)controlList.get(1)).enabled = false;
        }
    }
}
