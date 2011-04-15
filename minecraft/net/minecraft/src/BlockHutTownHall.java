package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockHutTownHall extends BlockInformator
{

	public BlockHutTownHall(int blockID, int _textureID) {
		super(blockID);
		textureID = _textureID;
		workingRange = 200;
	}

	protected TileEntity getBlockEntity()
    {
        return new TileEntityTownHall();
		//return null;
    }

	public boolean canPlaceBlockAt(World world, int i, int j, int k)
	{
		// check if there are other chests nearby
		Vec3D chestPos = scanForBlockNearPoint(world, mod_MineColony.hutTownHall.blockID, i,j,k, workingRange, 100, workingRange);
		if (chestPos != null)
			return false;

		return super.canPlaceBlockAt(world, i, j, k);
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer)
	{
		// build house when clicked with stick
		ItemStack is = entityplayer.getCurrentEquippedItem();

		 if(is !=null && (is.getItem().shiftedIndex == mod_MineColony.scepterSteel.shiftedIndex))
		{
			// fence
			Vec3D chestPos = Vec3D.createVector(i, j, k);
			for(int dx = i-workingRange; dx<=i+workingRange;dx++)
				for(int dz=k-workingRange; dz<=k+workingRange;dz++)
				{
					int dy = findTopGround(world, dx, dz);
					int groundBlockId = world.getBlockId(dx, dy, dz);
					if(groundBlockId == Block.dirt.blockID ||
							groundBlockId == Block.grass.blockID ||
							groundBlockId == Block.gravel.blockID ||
							groundBlockId == Block.sand.blockID ||
							groundBlockId == Block.stone.blockID)
					{
						if(dx == i-workingRange || dx == i+workingRange ||
								dz == k-workingRange || dz == k+workingRange)
							world.setBlockWithNotify(dx,dy+1,dz, Block.fence.blockID);
					}
				}
		}
		else
		{
			if(world.multiplayerWorld)
			{
				return true;
			} else
			{
				TileEntityTownHall tileentitytownhall = (TileEntityTownHall)world.getBlockTileEntity(i, j, k);
				GuiTownHall guiTownHall = new GuiTownHall(entityplayer.inventory, tileentitytownhall);
				ModLoader.OpenGUI(entityplayer, guiTownHall);
				return true;
			}
		}

		return true;

	}

	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);

		world.setBlockWithNotify(i, j, k, mod_MineColony.hutTownHall.blockID);
		TileEntityTownHall tileentitytownhall = (TileEntityTownHall) world
		.getBlockTileEntity(i, j, k);
	}

	public void updateTick(World world, int i, int j, int k, Random random)
	{
		super.updateTick(world, i, j, k, random);
	}

	public void onBlockRemoval(World world, int i, int j, int k)
	{
		/*inhabitants = getBuilderAround(world, i,j,k);
		if(inhabitants!=null)
		{
			inhabitants.isDead=true;
		}*/
	}

	public int getBlockTextureFromSide(int side)
	{
		if(side==1)
		{
			// Workshop top
			return textureID;
		}
		return blockIndexInTexture;
	}

	public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l)
	{
		if(l == 1)
		{
			return textureID;
		}
		else
			return super.getBlockTexture(iblockaccess, i, j, k, l);
	}

}
