/*
 * jens - (J)ava (EN)umerable (S)ubsets
 * Copyright (C) 2022 andreww1011
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jamw.jens;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * 
 */
public class EnumerablesTest {
    
    private static AllItems ALL_ITEMS_STATIC;
    private static EvenItems EVEN_ITEMS_STATIC;
    
    public EnumerablesTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
        ALL_ITEMS_STATIC = Enumerables.getEnumerable(AllItems.class);
        EVEN_ITEMS_STATIC = Enumerables.getEnumerable(EvenItems.class);
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
