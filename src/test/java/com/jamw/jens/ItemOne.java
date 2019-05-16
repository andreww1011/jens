/*
 * Copyright (c) 2018 to present, Andrew Wagner. All rights reserved.
 */
package com.jamw.jens;

/**
 *
 * @author Andrew
 */
@EnumeratedItem(description = "Item #1")
public interface ItemOne 
<E extends Enumerable<E>>
{
    
    public Item<E> item1();
    
}
