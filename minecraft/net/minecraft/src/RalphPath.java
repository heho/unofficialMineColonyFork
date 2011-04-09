package net.minecraft.src;

public class RalphPath {

    public RalphPath()
    {
        pathPoints = new PathPoint[1024];
        count = 0;
    }

    public PathPoint addPoint(PathPoint pathpoint)
    {
        if(pathpoint.index >= 0)
        {
            throw new IllegalStateException("OW KNOWS!");
        }
        if(count == pathPoints.length)
        {
            PathPoint apathpoint[] = new PathPoint[count << 1];
            System.arraycopy(pathPoints, 0, apathpoint, 0, count);
            pathPoints = apathpoint;
        }
        pathPoints[count] = pathpoint;
        pathpoint.index = count;
        sortBack1(count++);
        return pathpoint;
    }

    public void clearPath()
    {
        count = 0;
    }

    public PathPoint dequeue()
    {
        PathPoint pathpoint = pathPoints[0];
        for (int i=0; i<count-1; i++)
        {
        	pathPoints[i]=pathPoints[i+1];
        	pathPoints[i].index=i;
        }
        
        pathPoints[count] = null;
        count--;
        pathpoint.index = -1;
        return pathpoint;
    }

    public void changeDistance(PathPoint pathpoint, float f)
    {
        float f1 = pathpoint.distanceToTarget;
        pathpoint.distanceToTarget = f;
        if(f < f1)
        {
            sortBack1(pathpoint.index);
        } else
        {
            sortForward(pathpoint.index);
        }
    }

    private void sortBack(int i)
    {
    	// can't just guess at the correct spot, should look entry by entry
    	
        PathPoint pathpoint = pathPoints[i];
        float f = pathpoint.distanceToTarget;
        do 
        {
            if(i <= 0)
            {
                break;
            }
            int j = i - 1 >> 1;
            PathPoint pathpoint1 = pathPoints[j];
            if(f >= pathpoint1.distanceToTarget)
            {
                break;
            }
            pathPoints[i] = pathpoint1;
            pathpoint1.index = i;
            i = j;
        } while(true);
        pathPoints[i] = pathpoint;
        pathpoint.index = i;
    }

    private void sortBack1(int i)  // checks each and every spot
    {
    	// can't just guess at the correct spot, should look entry by entry
    	//System.out.println("before sort\n" + toString() + "\n");
    	
        while (i>0 && pathPoints[i].distanceToTarget<pathPoints[i-1].distanceToTarget)
        {
        	PathPoint t =pathPoints[i-1];
        	pathPoints[i-1]=pathPoints[i];
        	pathPoints[i]=t;
        	pathPoints[i].index=i;
        	pathPoints[i-1].index=i-1;
        	i--;
        	//System.out.println("after switch\n" + toString() + "\n");
        }
    }
    
    
    private void sortForward(int i)
    {
        PathPoint pathpoint = pathPoints[i];
        float f = pathpoint.distanceToTarget;
        do
        {
            int j = 1 + (i << 1);
            int k = j + 1;
            if(j >= count)
            {
                break;
            }
            PathPoint pathpoint1 = pathPoints[j];
            float f1 = pathpoint1.distanceToTarget;
            PathPoint pathpoint2;
            float f2;
            if(k >= count)
            {
                pathpoint2 = null;
                f2 = (1.0F / 0.0F);
            } else
            {
                pathpoint2 = pathPoints[k];
                f2 = pathpoint2.distanceToTarget;
            }
            if(f1 < f2)
            {
                if(f1 >= f)
                {
                    break;
                }
                pathPoints[i] = pathpoint1;
                pathpoint1.index = i;
                i = j;
                continue;
            }
            if(f2 >= f)
            {
                break;
            }
            pathPoints[i] = pathpoint2;
            pathpoint2.index = i;
            i = k;
        } while(true);
        pathPoints[i] = pathpoint;
        pathpoint.index = i;
    }

    public boolean isPathEmpty()
    {
        return count == 0;
    }
 
    public String toString()
    {
    	String s="";
    	for (int i=0; i<count; i++)
    		s += i + ") " + pathPoints[i].toString() + " estimate total trip " + pathPoints[i].distanceToTarget + " est to go" + pathPoints[i].distanceToNext + "dist so far" + pathPoints[i].totalPathDistance + "\n";
    	
    	return s;
    }
    
    private PathPoint pathPoints[];
    private int count;
}
