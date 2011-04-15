package net.minecraft.src;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.Random;

public class BlockInformator extends BlockContainer
{
	protected int workingRange;
	protected int textureID;

    protected BlockInformator(int blockID)
    {
        super(blockID, Material.wood);
        blockIndexInTexture = 26;
    }


    public int getBlockTextureFromSide(int i)
    {
        if(i == 1)
        {
            return blockIndexInTexture - 1;
        }
        if(i == 0)
        {
            return blockIndexInTexture - 1;
        }
        if(i == 3)
        {
            return blockIndexInTexture + 1;
        } else
        {
            return blockIndexInTexture;
        }
    }

    public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer)
    {
        if(world.multiplayerWorld)
        {
            return true;
        } else
        {
            TileEntityInformator tileentityinformator = (TileEntityInformator)world.getBlockTileEntity(i, j, k);
			GuiInformator guiInformator = new GuiInformator(entityplayer.inventory, tileentityinformator);
            ModLoader.OpenGUI(entityplayer, guiInformator);
            return true;
        }
    }

    protected TileEntity getBlockEntity()
    {
        return new TileEntityInformator();
		//return null;
    }

    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving)
    {
        int l = MathHelper.floor_double((double)((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
        if(l == 0)
        {
            world.setBlockMetadataWithNotify(i, j, k, 2);
        }
        if(l == 1)
        {
            world.setBlockMetadataWithNotify(i, j, k, 5);
        }
        if(l == 2)
        {
            world.setBlockMetadataWithNotify(i, j, k, 3);
        }
        if(l == 3)
        {
            world.setBlockMetadataWithNotify(i, j, k, 4);
        }
    }

		protected Vec3D scanForBlockNearPoint(World world, int blockId, int x, int y, int z,
			int rx, int ry, int rz) {

		Vec3D entityVec = Vec3D.createVector(x, y, z);

		Vec3D closestVec = null;
		double minDistance = 999999999;

		for (int i = x - rx; i <= x + rx; i++)
			for (int j = y - ry; j <= y + ry; j++)
				for (int k = z - rz; k <= z + rz; k++) {
					if (world.getBlockId(i, j, k) == blockId) {
						Vec3D tempVec = Vec3D.createVector(i, j, k);

						if (closestVec == null
								|| tempVec.distanceTo(entityVec) < minDistance) {
							closestVec = tempVec;
							minDistance = closestVec.distanceTo(entityVec);
						}
					}
				}

		return closestVec;
	}

	protected int findTopGround(World world, int x, int z) {
		int ySolid = world.findTopSolidBlock(x, z);
		int blockId = world.getBlockId(x, ySolid, z);
		while(blockId==Block.leaves.blockID ||
				blockId==Block.wood.blockID ||
				blockId==Block.cactus.blockID ||
				blockId==Block.crops.blockID ||
				blockId==Block.fence.blockID ||
				blockId==Block.fire.blockID ||
				blockId==0)
		{
			ySolid--;
			blockId = world.getBlockId(x, ySolid, z);
		}
		return ySolid;
	}
}
