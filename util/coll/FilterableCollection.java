package util.coll;

import java.util.Iterator;

public class FilterableCollection {

    public static Iterator filterableIterator(Iterator iter, IteratorFilter filter){
        return new FilterableIterator(iter,filter);
    }
	
}
