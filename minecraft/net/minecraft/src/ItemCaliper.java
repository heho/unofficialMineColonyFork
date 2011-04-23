package net.minecraft.src;


/**
 *
 * @author heho
 */
public class ItemCaliper extends Item
{
	protected ItemCaliper(int i)
    {
		super(i);
    }

		public boolean onItemUseLeftClick(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l)
    {
        return onItemUse(itemstack, entityplayer, world, i, j, k, l);
    }

	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l)
    {
		if(startPositionSet)
		{
			startPositionX = i;
			startPositionY = j;
			startPositionZ = k;
			startPositionSet = !startPositionSet;
			return false;
		}
		else
		{
			startPositionSet = !startPositionSet;
			//double distance = Vec3D.createVector(i, j, k).distanceTo(startPosition);
			//ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(String.format("The Distance is %d", (int)distance));
			if(startPositionX == i && startPositionY == j && startPositionZ == k)
			{
				ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(String.format("it is the same block"));
				return false;
			}
			if(startPositionX == i)
			{
				if(startPositionY == j)
				{
					int distance = java.lang.Math.abs(k - startPositionZ) + 1;
					ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(String.format("It is a %d blocks long line", distance));
					return false;
				}
				if(startPositionZ == k)
				{
					int distance = java.lang.Math.abs(j - startPositionY) + 1;
					ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(String.format("It is a %d blocks long line", distance));
					return false;
				}
				int distance1 = java.lang.Math.abs(j - startPositionY) + 1;
				int distance2 = java.lang.Math.abs(k - startPositionZ) + 1;

				ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(String.format("It is a %d by %d square", distance1, distance2));
				return false;
			}
			if(startPositionY == j)
			{
				if(startPositionX == j)
				{
					int distance = java.lang.Math.abs(k - startPositionZ) + 1;
					ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(String.format("It is a %d blocks long line", distance));
					return false;
				}
				if(startPositionZ == k)
				{
					int distance = java.lang.Math.abs(i - startPositionX) + 1;
					ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(String.format("It is a %d blocks long line", distance));
					return false;
				}
				int distance1 = java.lang.Math.abs(i - startPositionX) + 1;
				int distance2 = java.lang.Math.abs(k - startPositionZ) + 1;

				ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(String.format("It is a %d by %d square", distance1, distance2));
				return false;
			}
			if(startPositionZ == k)
			{
				if(startPositionX == i)
				{
					int distance = java.lang.Math.abs(j - startPositionY) + 1;
					ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(String.format("It is a %d blocks long line", distance));
					return false;
				}
				if(startPositionY == j)
				{
					int distance = java.lang.Math.abs(i - startPositionX) + 1;
					ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(String.format("It is a %d blocks long line", distance));
					return false;
				}
				int distance1 = java.lang.Math.abs(i - startPositionX) + 1;
				int distance2 = java.lang.Math.abs(j - startPositionY) + 1;

				ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(String.format("It is a %d by %d square", distance1, distance2));
				return false;
			}

			int distance1 = java.lang.Math.abs(i - startPositionX) + 1;
			int distance2 = java.lang.Math.abs(j - startPositionY) + 1;
			int distance3 = java.lang.Math.abs(k - startPositionZ) + 1;

			ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(String.format("It is a %d by %d by %d cube", distance1, distance2, distance3));
			return false;
		}
    }

	static boolean startPositionSet;
	static int startPositionX;
	static int startPositionY;
	static int startPositionZ;
	static{
		startPositionSet = true;
	}
}
