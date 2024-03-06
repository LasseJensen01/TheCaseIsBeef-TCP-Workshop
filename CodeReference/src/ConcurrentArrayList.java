import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConcurrentArrayList implements Iterable<Player>
{
	private List<Player> liste = new ArrayList<Player>(); 
	
	public void add(Player p) {
		liste.add(p);	
	}

	public void remove(Player p) {
		liste.remove(p);	
	}
	
	public void clear() {
		liste.clear();
	}
	
	public int size() {
		return liste.size();
	}

	@Override
	public Iterator<Player> iterator() {
		return liste.iterator();
	}
	/*public class ConcurrentArrayListIterator<Player> implements Iterator<Player> {

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public Player next() {
			return null;
		}
	
}*/
}