public class P
{
	public int x, y;
	
	public P(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	@Override
    public boolean equals(Object b){
    	P bb = (P)b;
        return x == bb.x && y == bb.y;
    }
	
	@Override
    public int hashCode(){
        return x*10000000 + y * 10000;
    }
}