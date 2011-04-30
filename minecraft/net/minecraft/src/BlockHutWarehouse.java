package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockHutWarehouse extends BlockHut {
	int xCoord, yCoord, zCoord;

	public BlockHutWarehouse(int blockID, int _textureID) {
		super(blockID);
		textureID = _textureID;
		hutWidth = 5;
		hutHeight = 4;
		clearingRange = 4;
		halfWidth = hutWidth/2;
		workingRange = 60;
	}

	protected EntityWorker createEntity(World world)
	{
		return (EntityDeliveryMan) EntityList.createEntityInWorld("DeliveryMan", world);
	}

	public boolean canPlaceBlockAt(World world, int i, int j, int k)
	{
		// check if there are other chests nearby
		//Vec3D chestPos = scanForBlockNearPoint(world, mod_MineColony.hutWarehouse.blockID, i,j,k, workingRange, 20, workingRange);
		//if (chestPos != null)
		//	return false;

		return super.canPlaceBlockAt(world, i, j, k);
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer)
	{
		// build house when clicked with stick
		ItemStack is = entityplayer.getCurrentEquippedItem();
		if(is !=null && is.getItem()!=null && (is.getItem().shiftedIndex == mod_MineColony.scepterGold.shiftedIndex))
		{
			// Build Warehouse

			// clean area around house
			for (int x = i - clearingRange; x <= i + clearingRange; x++)
				for (int z = k - clearingRange; z <= k + clearingRange; z++)
					for (int y = j; y < j + hutHeight; y++) {
						if(x!=i || y!=j || z!=k)
							world.setBlockWithNotify(x, y, z, 0);
					}

			// create empty box with stone
			for (int x = i - halfWidth; x <= i + halfWidth; x++)
				for (int z = k - halfWidth; z <= k + halfWidth; z++)
					for (int y = j - 1; y < j + hutHeight; y++) {

						if (x == i - halfWidth || x == i + halfWidth
								|| z == k - halfWidth || z == k + halfWidth) {
							// stone on sides
							world.setBlockWithNotify(x, y, z, Block.planks.blockID);
						} else if(x!=i || y!=j || z!=k){
							// air inside
							world.setBlockWithNotify(x, y, z, 0);
						}
					}

			// floor
			for (int x = i - halfWidth-1; x <= i + halfWidth+1; x++)
				for (int z = k - halfWidth-1; z <= k + halfWidth+1; z++) {
					world.setBlockWithNotify(x, j - 1, z, Block.cobblestone.blockID);
				}

			// roof
			for (int x = i - halfWidth; x <= i + halfWidth; x++)
				for (int z = k - halfWidth; z <= k + halfWidth; z++) {
					world.setBlockWithNotify(x, j + hutHeight-1, z, Block.planks.blockID);
				}

			// Door
			world.setBlockWithNotify(i+1, j, k - halfWidth, 0);
			world.setBlockWithNotify(i+1, j + 1, k - halfWidth,0);
			world.setBlockWithNotify(i+1, j + 2, k - halfWidth,Block.planks.blockID);
			world.setBlockWithNotify(i, j, k - halfWidth, 0);
			world.setBlockWithNotify(i, j + 1, k - halfWidth,0);
			world.setBlockWithNotify(i, j + 2, k - halfWidth,Block.planks.blockID);
			world.setBlockWithNotify(i-1, j, k - halfWidth, 0);
			world.setBlockWithNotify(i-1, j + 1, k - halfWidth,0);
			world.setBlockWithNotify(i-1, j + 2, k - halfWidth,Block.planks.blockID);

			world.setBlockWithNotify(i-2, j + 2, k - halfWidth,Block.planks.blockID);
			world.setBlockWithNotify(i+2, j + 2, k - halfWidth,Block.planks.blockID);


			// Windows
			world.setBlockWithNotify(i - halfWidth, j + 1, k-1, 0);
			world.setBlockWithNotify(i - halfWidth, j, k-1, 0);
			world.setBlockWithNotify(i - halfWidth, j + 1, k, 0);
			world.setBlockWithNotify(i - halfWidth, j, k, 0);
			world.setBlockWithNotify(i - halfWidth, j + 1, k+1, 0);
			world.setBlockWithNotify(i - halfWidth, j, k+1, 0);

			world.setBlockWithNotify(i + halfWidth, j + 1, k-1, 0);
			world.setBlockWithNotify(i + halfWidth, j, k-1, 0);
			world.setBlockWithNotify(i + halfWidth, j + 1, k, 0);
			world.setBlockWithNotify(i + halfWidth, j, k, 0);
			world.setBlockWithNotify(i + halfWidth, j + 1, k+1, 0);
			world.setBlockWithNotify(i + halfWidth, j, k+1, 0);
		}
		else
			return super.blockActivated(world, i, j, k, entityplayer);

		return true;

	}

	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		//world.setWorldTime(0);
		world.setBlockWithNotify(i, j, k, mod_MineColony.hutWarehouse.blockID);
		TileEntityWarehouse tew= (TileEntityWarehouse) world.getBlockTileEntity(i, j, k);

		tew.setInventorySlotContents(0, new ItemStack(Item.sign, 10));

        xCoord=i;
        yCoord=j;
        zCoord=k;

	}

	public void updateTick(World world, int i, int j, int k, Random random)
	{
		super.updateTick(world, i, j, k, random);

	}




	public void onBlockRemoval(World world, int i, int j, int k)
	{
		if ( world.getBlockTileEntity(i, j, k) instanceof TileEntityWarehouse)
		{
		TileEntityWarehouse tew= (TileEntityWarehouse) world.getBlockTileEntity(i, j, k);
		tew.removeWorker();
		}
	}
    protected TileEntity getBlockEntity()
    {
        return new TileEntityWarehouse();
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

