package net.minecraft.src;

public class RalphPathfinder {

    public RalphPathfinder(IBlockAccess iblockaccess)
    {
        path = new RalphPath();
        pointMap = new MCHashTable();
        pathOptions = new PathPoint[32];
        worldMap = iblockaccess;
    }

    public RalphPathEntity createEntityPathTo(Entity entity, Entity entity1, float f)
    {
        return createEntityPathTo(entity, entity1.posX, entity1.boundingBox.minY, entity1.posZ, f);
    }

    public RalphPathEntity createEntityPathTo(Entity entity, int i, int j, int k, float f)
    {
        return createEntityPathTo(entity, (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, f);
    }

    private RalphPathEntity createEntityPathTo(Entity entity, double d, double d1, double d2, 
            float f)
    {
        path.clearPath();
        pointMap.clearMap();
        PathPoint pathpoint = openPoint(MathHelper.floor_double(entity.boundingBox.minX), MathHelper.floor_double(entity.boundingBox.minY), MathHelper.floor_double(entity.boundingBox.minZ));
        PathPoint pathpoint1 = openPoint(MathHelper.floor_double(d - (double)(entity.width / 2.0F)), MathHelper.floor_double(d1), MathHelper.floor_double(d2 - (double)(entity.width / 2.0F)));
        PathPoint pathpoint2 = new PathPoint(MathHelper.floor_float(entity.width + 1.0F), MathHelper.floor_float(entity.height + 1.0F), MathHelper.floor_float(entity.width + 1.0F));
        System.out.println("tyring to a* path from " + pathpoint + " to " + pathpoint1);
        RalphPathEntity pathentity = addToPath(entity, pathpoint, pathpoint1, pathpoint2, f);
        return pathentity;
    }

    private RalphPathEntity addToPath(Entity entity, PathPoint startPoint, PathPoint destPoint, PathPoint pathWidth, float f)
    {
    	startPoint.totalPathDistance = 0.0F;
    	startPoint.distanceToNext = startPoint.distanceTo(destPoint);
    	startPoint.distanceToTarget = startPoint.distanceToNext;
        path.clearPath();
        path.addPoint(startPoint);
        PathPoint pathpoint3 = startPoint;
        while(!path.isPathEmpty()) 
        {
            PathPoint pathpoint4 = path.dequeue();
        	//System.out.println("after dqueue\n" + path.toString() + "\n");
           // System.out.println("looking for neighbors of " + pathpoint4 + " and trying to reach " + destPoint);
            if(pathpoint4.equals(destPoint)  ||  pathpoint4.distanceTo(destPoint)<2)
            {
                return createEntityPath(startPoint,  pathpoint4);
            }
            if(pathpoint4.distanceTo(destPoint) < pathpoint3.distanceTo(destPoint))
            {
                pathpoint3 = pathpoint4;
            }
            pathpoint4.isFirst = true;
            int i = findPathOptions(entity, pathpoint4, pathWidth, destPoint, f);
            int j = 0;
            while(j < i) 
            {
                PathPoint pathpoint5 = pathOptions[j];
                float f1 = pathpoint4.totalPathDistance + pathpoint4.distanceTo(pathpoint5);
                if(!pathpoint5.isAssigned() || f1 < pathpoint5.totalPathDistance)
                {
                    pathpoint5.previous = pathpoint4;
                    pathpoint5.totalPathDistance = f1;  // total to get to that point
                    pathpoint5.distanceToNext = pathpoint5.distanceTo(destPoint); //estimate for rest of trip
                    if(pathpoint5.isAssigned())
                    {
                        path.changeDistance(pathpoint5, pathpoint5.totalPathDistance + pathpoint5.distanceToNext);
                    } else
                    {
                        pathpoint5.distanceToTarget = pathpoint5.totalPathDistance + pathpoint5.distanceToNext;  //estimate of distance via this point? (and what we sort on)
                        //System.out.println("adding neighbor for consideration " + pathpoint5 + " dist est via this pt(s)=" + pathpoint5.distanceToTarget + " dist to dest " + pathpoint5.distanceToNext + " dist to get to this point=" + pathpoint5.totalPathDistance);
                        path.addPoint(pathpoint5);
                    }
                }
                j++;
            }
           //System.out.println(path.toString());
        }
        if(pathpoint3 == startPoint)
        {
            return null;
        } else
        {
            return createEntityPath(startPoint, pathpoint3);
        }
    }

    private int findPathOptions(Entity entity, PathPoint pathpoint, PathPoint pathpoint1, PathPoint pathpoint2, float f)
    {
        int i = 0;
        int j = 0;
        if(getVerticalOffset(entity, pathpoint.xCoord, pathpoint.yCoord + 1, pathpoint.zCoord, pathpoint1) > 0)
        {
            j = 1;
        }
        PathPoint pathpoint3 = getSafePoint(entity, pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord + 1, pathpoint1, j);
        PathPoint pathpoint4 = getSafePoint(entity, pathpoint.xCoord - 1, pathpoint.yCoord, pathpoint.zCoord, pathpoint1, j);
        PathPoint pathpoint5 = getSafePoint(entity, pathpoint.xCoord + 1, pathpoint.yCoord, pathpoint.zCoord, pathpoint1, j);
        PathPoint pathpoint6 = getSafePoint(entity, pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord - 1, pathpoint1, j);
        if(pathpoint3 != null && !pathpoint3.isFirst && pathpoint3.distanceTo(pathpoint2) < f)
        {
            pathOptions[i++] = pathpoint3;
        }
        if(pathpoint4 != null && !pathpoint4.isFirst && pathpoint4.distanceTo(pathpoint2) < f)
        {
            pathOptions[i++] = pathpoint4;
        }
        if(pathpoint5 != null && !pathpoint5.isFirst && pathpoint5.distanceTo(pathpoint2) < f)
        {
            pathOptions[i++] = pathpoint5;
        }
        if(pathpoint6 != null && !pathpoint6.isFirst && pathpoint6.distanceTo(pathpoint2) < f)
        {
            pathOptions[i++] = pathpoint6;
        }
        return i;
    }

    private PathPoint getSafePoint(Entity entity, int i, int j, int k, PathPoint pathpoint, int l)
    {
        PathPoint pathpoint1 = null;
        if(getVerticalOffset(entity, i, j, k, pathpoint) > 0)
        {
            pathpoint1 = openPoint(i, j, k);
        }
        if(pathpoint1 == null && l > 0 && getVerticalOffset(entity, i, j + l, k, pathpoint) > 0)
        {
            pathpoint1 = openPoint(i, j + l, k);
            j += l;
        }
        if(pathpoint1 != null)
        {
            int i1 = 0;
            for(int j1 = 0; j > 0 && (j1 = getVerticalOffset(entity, i, j - 1, k, pathpoint)) > 0; j--)
            {
                if(j1 < 0)
                {
                    return null;
                }
                if(++i1 >= 4)
                {
                    return null;
                }
            }

            if(j > 0)
            {
                pathpoint1 = openPoint(i, j, k);
            }
        }
        return pathpoint1;
    }

    private final PathPoint openPoint(int i, int j, int k)
    {
        int l = PathPoint.func_22329_a(i, j, k);
        PathPoint pathpoint = (PathPoint)pointMap.lookup(l);
        if(pathpoint == null)
        {
            pathpoint = new PathPoint(i, j, k);
            pointMap.addKey(l, pathpoint);
        }
        return pathpoint;
    }

    private int getVerticalOffset(Entity entity, int i, int j, int k, PathPoint pathpoint)
    {
        for(int l = i; l < i + pathpoint.xCoord; l++)
        {
            for(int i1 = j; i1 < j + pathpoint.yCoord; i1++)
            {
                for(int j1 = k; j1 < k + pathpoint.zCoord; j1++)
                {
                    Material material = worldMap.getBlockMaterial(l, i1, j1);
                    if(material.getIsSolid())
                    {
                        return 0;
                    }
                    if(material == Material.water || material == Material.lava)
                    {
                        return -1;
                    }
                }

            }

        }

        return 1;
    }

    private RalphPathEntity createEntityPath(PathPoint pathpoint, PathPoint pathpoint1)
    {
        int i = 1;
        for(PathPoint pathpoint2 = pathpoint1; pathpoint2.previous != null; pathpoint2 = pathpoint2.previous)
        {
            i++;
        }

        PathPoint apathpoint[] = new PathPoint[i];
        PathPoint pathpoint3 = pathpoint1;
        for(apathpoint[--i] = pathpoint3; pathpoint3.previous != null; apathpoint[--i] = pathpoint3)
        {
            pathpoint3 = pathpoint3.previous;
        }

        return new RalphPathEntity(apathpoint);
    }

    private IBlockAccess worldMap;
    private RalphPath path;
    private MCHashTable pointMap;
    private PathPoint pathOptions[];
}
