# jens
Java library for defining and using subsets.  Simulate `enum` with common constants.

## Usage

1. Define each element of a set in its own interface annotated with `@EnumeratedItem`, parameterized with `<E extends Enumerable<E>>`, and that has a member that returns the type `Item<E>` 

```java
@EnumeratedItem(description="translation in x")
interface Dx<E extends Enumerable<E>> {
    Item<E> dx();
}

@EnumeratedItem(description="translation in y")
interface Dy<E extends Enumerable<E>> {
    Item<E> dy();
}

@EnumeratedItem(description="translation in z")
interface Dz<E extends Enumerable<E>> {
    Item<E> dz();
}
```

2. Define a set in its own interface that extends `Enumerable` and each element of the set.

```java
interface Cartesian3d extends 
        Enumerable<Cartesian3d>,
        Dx<Cartesian3d>,
        Dy<Cartesian3d>,
        Dz<Cartesian3d> { }

interface Cartesian2d extends
        Enumerable<Cartesian2d>,
        Dx<Cartesian2d>,
        Dy<Cartesian2d> { }
```

3. Access the enumerable using `Enumerables.getEnumerable()`.  A concrete implementation is generated dynamically at runtime.

```java
Cartesian3d c3 = Enumerables.getEnumerable(Cartesian3d.class);
Cartesian2d c2 = Enumerables.getEnumerable(Cartesian2d.class);
```

4. The elements of the set are ordered by the implementation.  Reference each element by name.

```java 
//3d position vector (x,y,z) = (1.5,-2.0,1.7)
double[] arr3d = new double[c3.size()];
arr3d[c3.dx().ordinal()] =  1.5;
arr3d[c3.dy().ordinal()] = -2.0;
arr3d[c3.dz().ordinal()] =  1.7;

//2d position vector (x,y) = (-1.0,4.3)
double[] arr2d = new double[c2.size()];
arr2d[c2.dx().ordinal()] = -1.0;
arr2d[c2.dy().ordinal()] =  4.3;

double[] res2d = new double[c2.size()];
addXY(c3,arr3d,c2,arr2d,c2,res2d); //res2d = [0.5,2.3]

<E1 extends Enumerable<E1> & Dx<E1> & Dy<E1>,
 E2 extends Enumerable<E2> & Dx<E2> & Dy<E2>,
 E3 extends Enumerable<E3> & Dx<E3> & Dy<E3>> 
void addXY(E1 e1, double[] a1, E2 e2, double[] a2, E3 e3, double[] a3) {
    a3[e3.dx().ordinal()] = a1[e1.dx().ordinal()] + a2[e2.dx().ordinal()];
    a3[e3.dy().ordinal()] = a1[e1.dy().ordinal()] + a2[e2.dy().ordinal()];
}
```

## Features
- Type-safe comparison across different subsets.
- Internal ordering is managed by runtime.  Access elements using meaningful method names, not indicies.

