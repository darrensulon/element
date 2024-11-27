package util.coll;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FilterableIterator implements Iterator{

        private IteratorFilter m_filter;
        private Iterator m_iter;
        private Object obj;
        private boolean hasNext;

        protected FilterableIterator(Iterator iterator,IteratorFilter filter) {
            m_filter = filter;
            m_iter = iterator;
            filterNext();
        }
        public boolean hasNext() {
            return hasNext;
        }
        public Object next() {
            if (obj == null)
                throw new NoSuchElementException();
            Object tmpObj = obj;
            filterNext();
            return tmpObj;
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
        // helper method
        private void filterNext() {
            while (m_iter.hasNext()) {
                obj = m_iter.next();
                if (m_filter.matches(obj)) {
                    hasNext = true;
                    return;
                }
            }
            hasNext = false;
        }
}
