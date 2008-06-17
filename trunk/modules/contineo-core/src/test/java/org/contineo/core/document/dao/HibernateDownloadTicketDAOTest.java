package org.contineo.core.document.dao;

import org.contineo.core.AbstractCoreTestCase;
import org.contineo.core.document.DownloadTicket;
import org.contineo.core.document.dao.DownloadTicketDAO;

/**
 * Test case for <code>HibernateDownloadTicketDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class HibernateDownloadTicketDAOTest extends AbstractCoreTestCase {

	// Instance under test
	private DownloadTicketDAO dao;

	public HibernateDownloadTicketDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateDownaloadTicketoDAO
		dao = (DownloadTicketDAO) context.getBean("DownloadTicketDAO");
	}

	public void testDelete() {
		DownloadTicket ticket = dao.findByPrimaryKey("1");
		assertNotNull(ticket);
		assertTrue(dao.delete("1"));
		ticket = dao.findByPrimaryKey("1");
		assertNull(ticket);
	}

	public void testDeleteByMenuId() {
		DownloadTicket ticket = dao.findByPrimaryKey("1");
		assertNotNull(ticket);
		assertEquals(1, ticket.getMenuId());
		ticket = dao.findByPrimaryKey("3");
		assertNotNull(ticket);
		assertEquals(1, ticket.getMenuId());
		
		assertTrue(dao.deleteByMenuId(1));
		ticket = dao.findByPrimaryKey("1");
		assertNull(ticket);
		ticket = dao.findByPrimaryKey("3");
		assertNull(ticket);
	}
	
	public void testFindByPrimaryKey() {
		DownloadTicket ticket = dao.findByPrimaryKey("1");
		assertNotNull(ticket);
		assertEquals("admin", ticket.getUsername());
		assertEquals(1, ticket.getMenuId());

		ticket = dao.findByPrimaryKey("99");
		assertNull(ticket);
	}

	public void testStore() {
		DownloadTicket ticket = new DownloadTicket();
		ticket.setMenuId(1);
		ticket.setUsername("sebastian");
		ticket.setTicketId("5");
		dao.store(ticket);

		DownloadTicket storedTicket = dao.findByPrimaryKey("5");
		assertNotNull(storedTicket);
		assertEquals(ticket, storedTicket);
	}
}