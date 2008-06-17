package org.contineo.core.document.dao;

import java.util.Collection;
import java.util.Iterator;

import org.contineo.core.AbstractCoreTestCase;
import org.contineo.core.document.History;
import org.contineo.core.document.dao.HistoryDAO;

/**
 * Test case for <code>HibernateHistoryDAO</code>
 * 
 * @author Alessandro Gasparini
 * @version $Id:$
 * @since 3.0
 */
public class HibernateHistoryDAOTest extends AbstractCoreTestCase {

	// Instance under test
	private HistoryDAO dao;

	public HibernateHistoryDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateHistoryDAO
		dao = (HistoryDAO) context.getBean("HistoryDAO");
	}


    @SuppressWarnings("unchecked")
    public void testDelete() {
        Collection<History> histories = (Collection<History>)dao.findByUsername("author");
        assertNotNull(histories);
        assertEquals(2, histories.size());
        
        for (History history : histories) {
            boolean result = dao.delete(history.getHistoryId());
            assertTrue(result);
        }
        
        histories = (Collection<History>)dao.findByUsername("author");
        assertNotNull(histories);
        assertEquals(0, histories.size());        
    }    
    

	public void testFindByDocId() {
		Collection histories = dao.findByDocId(1);
		assertNotNull(histories);
		assertEquals(2, histories.size());

		// Try with unexisting docId
        histories = dao.findByDocId(99);
		assertNotNull(histories);
		assertEquals(0, histories.size());        
	}
    
    public void testFindByUsername() {
        Collection histories = dao.findByUsername("author");
        assertNotNull(histories);
        assertEquals(2, histories.size());

        // Try with unexisting username
        histories = dao.findByUsername("sss");
        assertNotNull(histories);
        assertEquals(0, histories.size());        
    }    
    
	@SuppressWarnings("unchecked")
    public void testStore() {
        History history = new History();
        history.setDocId(1);
        history.setDate("2006-12-20");
        history.setUsername("sebastian");
        history.setEvent("test History store");

        assertTrue(dao.store(history));        

		// Test the stored history
        Collection<History> histories = (Collection<History>)dao.findByUsername("sebastian");
		assertNotNull(histories);
        assertFalse(histories.isEmpty());
        
        Iterator<History> itHist = histories.iterator();
        History hStored = itHist.next();
        assertTrue(hStored.equals(history));
        assertEquals(hStored.getDocId(), 1);
        assertEquals(hStored.getDate(), "2006-12-20");
        assertEquals(hStored.getUsername(), "sebastian");
        assertEquals(hStored.getEvent(), "test History store");
	}
}