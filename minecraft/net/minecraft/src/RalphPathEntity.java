package net.minecraft.src;

public class RalphPathEntity {

    public RalphPathEntity(PathPoint apathpoint[])
    {
        points = apathpoint;
        pathLength = apathpoint.length;
    }

    public void incrementPathIndex()
    {
        pathIndex++;
    }

    public boolean isFinished()
    {
        return pathIndex >= points.length;
    }

    public PathPoint func_22328_c()
    {
        if(pathLength > 0)
        {
            return points[pathLength - 1];
        } else
        {
            return null;
        }
    }

    public Vec3D getPosition(Entity entity)
    {
        double d = (double)points[pathIndex].xCoord + (double)(int)(entity.width + 1.0F) * 0.5D;
        double d1 = points[pathIndex].yCoord;
        double d2 = (double)points[pathIndex].zCoord + (double)(int)(entity.width + 1.0F) * 0.5D;
        return Vec3D.createVector(d, d1, d2);
    }

    public int indexOf(int x, int y, int z)
    {
    for (int i=0; i<points.length; i++)
    	if (points[i].xCoord == x && points[i].yCoord == y && points[i].zCoord == z )
    		return i;
    	
    return -1;	
    }
    
    public final PathPoint points[];
    public final int pathLength;
    private int pathIndex;
}
