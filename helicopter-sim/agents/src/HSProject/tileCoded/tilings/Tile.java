package HSProject;

import marl.environments.State;

/**
 * A Tile is very simple implementation of a State
 * Representation where it is given an unsigned int
 * value on construction and then simply give that
 * value as the hash value required by the State
 * Representation interface.
 */
public class Tile
	implements State<Tile>
{
	protected int value_;
	
	public Tile()
	{
		this(0);
	}
	public Tile(int value)
	{
		value_ = value;
	}

	@Override
	public int hashCode()
	{
		return value_;
	}
	
    public void set(Tile s)
    {
        value_ = s.value_;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(obj instanceof Tile){
    		Tile t = (Tile) obj;
    		return this.value_ == t.value_;
    	}
		return false;
    }
	
}