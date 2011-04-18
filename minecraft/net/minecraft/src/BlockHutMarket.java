package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockHutMarket extends BlockInformator
{
	public BlockHutMarket(int blockID, int _textureID) {
		super(blockID);
		textureID = _textureID;
		workingRange = 10;
	}

	protected TileEntity getBlockEntity()
    {
        return new TileEntityMarket();
		//return null;
    }


	public boolean canPlaceBlockAt(World world, int i, int j, int k)
	{
		// check if there are other chests nearby
		Vec3D chestPos = scanForBlockNearPoint(world, mod_MineColony.hutMarket.blockID, i,j,k, workingRange, 15, workingRange);
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

		world.setBlockWithNotify(i, j, k, mod_MineColony.hutMarket.blockID);
		TileEntityMarket tileEntityMarket = (TileEntityMarket) world
		.getBlockTileEntity(i, j, k);

		tileEntityMarket.setTownHall(i, j, k, world);
	}

	public void getNextTownHall(World world, int i, int j, int k, TileEntityMarket tileEntity)
	{

	}

	public void updateTick(World world, int i, int j, int k, Random random)
	{
		super.updateTick(world, i, j, k, random);
	}


	public void onBlockRemoval(World world, int i, int j, int k)
	{
		TileEntityMarket market = (TileEntityMarket) (world.getBlockTileEntity(i, j, k));
		if(market.townHallPosition != null)
		{
			System.out.println(market.townHallX);
			System.out.println(market.townHallY);
			System.out.println(market.townHallZ);
			TileEntityTownHall townhall = (TileEntityTownHall) (world.getBlockTileEntity((int)market.townHallX, (int)market.townHallY, (int)market.townHallZ));
			if(townhall == null)
			{
				return;
			}
			townhall.deleteMarket(i, j, k);
		}
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
