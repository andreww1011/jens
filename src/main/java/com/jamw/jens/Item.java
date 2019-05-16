/*
 * Copyright (c) 2018 to present, Andrew Wagner. All rights reserved.
 */
package com.jamw.jens;

/**
 *
 * @author Andrew
 */

public abstract class Item
<E extends Enumerable<E>> 
{   
    
    Item() {}
    
    public abstract int ordinal();
    
    public abstract String name();
    
    public abstract String description();
        
    @Override
    public final boolean equals(Object o) {
        return o == this;
    }
    
    @Override
    public final int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public abstract String toString();
}
