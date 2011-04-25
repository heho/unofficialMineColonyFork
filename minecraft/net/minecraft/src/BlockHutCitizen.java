package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockHutCitizen extends BlockHut
{

	public BlockHutCitizen(int blockID, int _textureID)
	{
		super(blockID);
		textureID = _textureID;
		hutWidth = 5;
		hutHeight = 3;
		clearingRange = 4;
		halfWidth = hutWidth/2;
		workingRange = 10;
		// Sets the recipe be two planks horizontal to each other
		// CraftingManager.getInstance().addRecipe(new ItemStack(blockID, 1,0),
		// new Object[] { "##", Character.valueOf('#'), Block.dirt,});
	}


	public boolean canPlaceBlockAt(World world, int i, int j, int k)
	{
		// check if there are other chests nearby
		Vec3D chestPos = scanForBlockNearPoint(world, mod_MineColony.hutCitizen.blockID, i,j,k, workingRange, 10, workingRange);
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
			return super.blockActivated(world, i, j, k, entityplayer);

		return true;

	}

	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);

		world.setBlockWithNotify(i, j, k, mod_MineColony.hutCitizen.blockID);
		TileEntityCitizen tileentitycitizen = (TileEntityCitizen) world
		.getBlockTileEntity(i, j, k);
		
		tileentitycitizen.spawnInhabitant();
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

	protected TileEntity getBlockEntity()
    {
        return new TileEntityCitizen();
    }

}
