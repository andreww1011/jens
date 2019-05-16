/*
 * Copyright (c) 2018 to present, Andrew Wagner. All rights reserved.
 */
package com.jamw.jens;

import java.util.List;

/**
 *
 * @author Andrew
 */
public interface Enumerable<E extends Enumerable<E>> {
    
    List<Item<E>> items(); 
    
    int size();
}
