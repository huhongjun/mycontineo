package org.contineo.web.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.contineo.core.document.Document;
import org.contineo.core.document.dao.DocumentDAO;
import org.contineo.core.security.ExtMenu;
import org.contineo.core.security.Menu;
import org.contineo.core.security.dao.MenuDAO;

import org.contineo.util.Context;

import org.contineo.web.SessionManagement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;


/**
 * Handles documents access by keywords
 *
 * @author Marco Meschieri
 * @version $Id: KeywordsBean.java,v 1.2 2007/08/22 14:12:20 marco Exp $
 * @since ###release###
 */
public class KeywordsBean {
    protected static Log log = LogFactory.getLog(KeywordsBean.class);
    private Collection<Letter> letters = new ArrayList<Letter>();
    private Collection<Keyword> keywords = new ArrayList<Keyword>();
    private Collection<ExtMenu> documents = new ArrayList<ExtMenu>();
    private String selectedWord;

    public KeywordsBean() {
        String str = "abcdefghijklmnopqrstuvwxyz";

        for (int i = 0; i < str.length(); i++) {
            letters.add(new Letter(str.charAt(i)));
        }
    }

    public Collection<Letter> getLetters() {
        return letters;
    }

    public Collection<Keyword> getKeywords() {
        return keywords;
    }

    public void reset() {
        keywords.clear();
    }

    public int getKeywordsCount() {
        return keywords.size();
    }

    public int getDocumentsCount() {
        return documents.size();
    }

    public Collection<ExtMenu> getDocuments() {
        return documents;
    }

    public String getSelectedWord() {
        return selectedWord;
    }

    /**
     * Representation of a letter
     */
    public class Letter {
        char letter;

        public Letter(char letter) {
            super();
            this.letter = letter;
        }

        public String getLetter() {
            return new String(new char[] { letter }).toUpperCase();
        }

        /**
         * Handles the selection of this letter
         */
        public String select() {
            if (SessionManagement.isValid()) {
                try {
                    String lett = new String(new char[] { letter }).toLowerCase();

                    String username = SessionManagement.getUsername();
                    DocumentDAO ddao = (DocumentDAO) Context.getInstance()
                                                            .getBean(DocumentDAO.class);
                    Collection<String> coll = ddao.findKeywords(lett, username);
                    Iterator iter = coll.iterator();
                    Hashtable<String, Integer> table = new Hashtable<String, Integer>(coll.size());

                    while (iter.hasNext()) {
                        String keyword = (String) iter.next();
                        int count = 1;

                        if (table.containsKey(keyword)) {
                            Integer i = (Integer) table.get(keyword);
                            table.remove(keyword);
                            count = i.intValue();
                            count++;
                        }

                        table.put(keyword, new Integer(count));
                    }

                    keywords.clear();
                    documents.clear();

                    Enumeration enum1 = table.keys();

                    while (enum1.hasMoreElements()) {
                        Keyword keyword = new Keyword();
                        String key = (String) enum1.nextElement();
                        Integer value = (Integer) table.get(key);
                        keyword.setWord(key);
                        keyword.setCount(value.intValue());
                        keywords.add(keyword);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

                return null;
            } else {
                return "login";
            }
        }
    }

    /**
     * Representation of a keyword
     */
    public class Keyword {
        private String word;
        private int count;

        public Keyword() {
            word = "";
            count = 0;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        /**
         * Handles the selection of this keyword
         */
        public String select() {
            if (SessionManagement.isValid()) {
                try {
                    selectedWord = word;

                    String username = SessionManagement.getUsername();
                    MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
                    
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
            		DocumentDAO documentDAO = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
            		
                    Collection<Menu> coll = menuDao.findByUserNameAndKeyword(username, word);
                    documents.clear();
                    keywords.clear();

                    for (Menu menu : coll) {
                        ExtMenu xmenu = new ExtMenu(menu);

                        // calculate size of menu
                        int size = 0;

                        if (menu.getMenuType() == Menu.MENUTYPE_FILE) {
                            int tmpSize = (int) menu.getMenuSize();
                            // we show the size in KBytes (on DB it's recorded in bytes)
                            size = tmpSize / 1024;
                            
                            // Just to show the date of the document not the one of the menu
                            Document doc = documentDAO.findByMenuId(menu.getMenuId());
            				if (doc != null) {
            					try {
            						Date date = dateFormat.parse(doc.getDocDate());
            						xmenu.setDate(date);
            					} catch (Exception e) {
            						xmenu.setDate(new Date());
            					}
            				}
                        } else {
                            size = menuDao.findByUserName(username, menu.getMenuId()).size();
                        }

                        xmenu.setSize(size);

                        // check if menu is writable
                        boolean writable = false;

                        if (menuDao.isWriteEnable(menu.getMenuId(), username)) {
                            writable = true;
                        }

                        xmenu.setWritable(writable);

                        documents.add(xmenu);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

                return null;
            } else {
                return "login";
            }
        }
    }
}
