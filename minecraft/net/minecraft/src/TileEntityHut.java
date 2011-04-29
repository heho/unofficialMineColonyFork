package net.minecraft.src;

/**
 *
 * @author heho
 */
public class TileEntityHut extends TileEntityChest
{
    public String getInvName()
    {
        return "Hut";
    }

	public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
    }

    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
    }

	/**
	 * scan for block type in the specified range
	 * 
	 * @param world
	 * @param blockId
	 * @param x
	 * @param y
	 * @param z
	 * @param rx 
	 * @param ry
	 * @param rz
	 * @return
	 */
	protected Vec3D scanForBlockNearPoint(World world, int blockId, int x, int y, int z,
			int rx, int ry, int rz)
	{

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

	public void setWorker(EntityWorker worker)
	{
		this.worker = worker;
	}

	protected EntityWorker worker = null;
}
