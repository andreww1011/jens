/*
 * Copyright (c) 2018 to present, Andrew Wagner. All rights reserved.
 */
package com.jamw.jens;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Andrew
 */
public class EnumerablesTest {
    
    private static AllItems ALL_ITEMS_STATIC;
    private static EvenItems EVEN_ITEMS_STATIC;
    
    public EnumerablesTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        ALL_ITEMS_STATIC = Enumerables.getEnumerable(AllItems.class);
        EVEN_ITEMS_STATIC = Enumerables.getEnumerable(EvenItems.class);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void Enumerables_AllItems_Test() {
        //test instance
        AllItems allItemsInstance = Enumerables.getEnumerable(AllItems.class);
        checkEnumerable(ALL_ITEMS_STATIC, allItemsInstance);
        
        //test interface instance
        Enumerable<AllItems> allItemsEnumerableInstance = Enumerables.getEnumerable(AllItems.class);
        checkEnumerable(ALL_ITEMS_STATIC, allItemsEnumerableInstance);
        
        //test toString()
        String tmps = ALL_ITEMS_STATIC.toString();
        assertEquals("com.jamw.jens.AllItems:<item0,item1,item2,item3,item4>",
                     tmps);
        
        //test items
        int ordinal = 0;
        for (Item<AllItems> i : ALL_ITEMS_STATIC.items()) {
            checkItem(ALL_ITEMS_STATIC.items(), i, ordinal, "AllItems:item" + ordinal);
            ordinal++;
        }
    }
    
    private static 
    <E extends Enumerable<E>> 
    void checkEnumerable(Enumerable<E> e1, Enumerable<E> e2) {
        assertEquals(e1, e2);
        assertSame(e1, e2);
    }
    
    private static
    <E extends Enumerable<E>>
    void checkItem(List<Item<E>> items, Item<E> item, int ordinal, String toString) {
        assertEquals(ordinal,item.ordinal());
        assertEquals(toString,item.toString());
        assertEquals(item,items.get(ordinal));
        assertSame(item,items.get(ordinal));
    }
    
    @Test
    public void Enumerables_EvenItems_Test() {
        //test instance
        EvenItems evenItemsInstance = Enumerables.getEnumerable(EvenItems.class);
        checkEnumerable(EVEN_ITEMS_STATIC, evenItemsInstance);
        
        //test interface instance
        Enumerable<EvenItems> evenItemsEnumerableInstance = Enumerables.getEnumerable(EvenItems.class);
        checkEnumerable(EVEN_ITEMS_STATIC, evenItemsEnumerableInstance);
        
        //test toString()
        String tmps = EVEN_ITEMS_STATIC.toString();
        assertEquals("com.jamw.jens.EvenItems:<item0,item2,item4>",
                     tmps);
        
        //test items
        int ordinal = 0;
        for (Item<EvenItems> i : EVEN_ITEMS_STATIC.items()) {
            checkItem(EVEN_ITEMS_STATIC.items(), i, ordinal, "EvenItems:item" + ordinal*2);
            ordinal++;
        }
    }
    
    @Test
    public void EnumerableNotSameTest() {
        assertNotSame(ALL_ITEMS_STATIC,EVEN_ITEMS_STATIC);
        assertNotSame(ALL_ITEMS_STATIC.item0(),EVEN_ITEMS_STATIC.item0());
    }
    
    @Test
    public void ItemDescriptionsTest() {
        assertEquals(ALL_ITEMS_STATIC.item1().description(),"Item #1");
        assertEquals(ALL_ITEMS_STATIC.item0().description(),"");
    }
    
}
