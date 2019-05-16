/*
 * Copyright (c) 2018 to present, Andrew Wagner. All rights reserved.
 */
package com.jamw.jens;

/**
 *
 * @author Andrew
 */
public interface AllItems 
extends Enumerable<AllItems>,
        ItemZero<AllItems>,
        ItemOne<AllItems>,
        ItemTwo<AllItems>,
        ItemThree<AllItems>,
        ItemFour<AllItems>
{
    
}
