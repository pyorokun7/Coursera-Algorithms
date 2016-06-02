/*----------------------------------------------------------------
 *  First Program Assignment - Java Algorithms Part I
 *  @Author David Anez
 *  @Date   15/02/2016
 *
 *  Specification of the assignment:
 *    - http://coursera.cs.princeton.edu/algs4/assignments/percolation.html
 *    - http://coursera.cs.princeton.edu/algs4/checklists/percolation.html
 *    - http://algs4.cs.princeton.edu/windows/
 *
 *  Compilation: javac Percolation.java
 *  Execution:   java Percolation
 *  Stdlib.jar and algs4.jar needed for execution
 *
 *  Modelation of a percolation system, using a grid NxN and WeightedQuickUnionUF
 *
 *----------------------------------------------------------------*/

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private static final int OPEN_SITE = 0x01;
    private static final int CONNECTED_TOP = 0x02;
    private static final int CONNECTED_BOTTOM = 0x04;

    // To represent our problem, we are going to consider a NxN grid.
    private WeightedQuickUnionUF grid1D;
    private int gridSize;
    private byte[] sitesInfo;
    private boolean sistemPercolated;

    // create N-by-N grid, with all sites blocked
    public Percolation(int N) {
        if (N <= 0) throw new IllegalArgumentException("N must be > 0");
        gridSize = N;
        grid1D = new WeightedQuickUnionUF(N*N);
        sitesInfo = new byte[N*N];
        sistemPercolated = false;
    }

    // open site (row i, column j) if it is not open already
    public void open(int i, int j) {
        int topCoord1D = -1;
        int bottomCoord1D = -1;
        int leftCoordID = -1;
        int rightCoordID = -1;
        int comp0;

        int newCoord1D = from2Dto1D(i, j);

        // Is the site already opened? Do nothing in that case.
        // Otherwise check if the sites up (i-1, j), down(i+1, j), left(i, j-1),
        // right(i, j+1) are open, and union the components if so.
        if ((sitesInfo[newCoord1D] & OPEN_SITE) != OPEN_SITE) {
            sitesInfo[newCoord1D] = OPEN_SITE;

            // If i == 1, then by definition is connected to the top, which is i == 0
            if (i == 1) sitesInfo[newCoord1D] |= CONNECTED_TOP;

            // If i == gridSize, then by definition is connected to the bottom
            if (i == gridSize) sitesInfo[newCoord1D] |= CONNECTED_BOTTOM;

            // We calculate the neighbor's coordinates, if possible
            if (i > 1) topCoord1D = from2Dto1D(i - 1, j);
            if (i < gridSize) bottomCoord1D = from2Dto1D(i + 1, j);
            if (j > 1) leftCoordID = from2Dto1D(i, j - 1);
            if (j < gridSize) rightCoordID = from2Dto1D(i, j + 1);

            // Then we check every neighbor's root, to see if they are connected
            // top or bottom, and mark this new node accordingly, and then
            // union with the neighbor
            if (topCoord1D != -1 &&
                (sitesInfo[topCoord1D] & OPEN_SITE) == OPEN_SITE) {
                comp0 = grid1D.find(topCoord1D);
                if ((sitesInfo[comp0] & CONNECTED_TOP) == CONNECTED_TOP)
                    sitesInfo[newCoord1D] |= CONNECTED_TOP;
                if ((sitesInfo[comp0] & CONNECTED_BOTTOM) == CONNECTED_BOTTOM)
                    sitesInfo[newCoord1D] |= CONNECTED_BOTTOM;
                grid1D.union(newCoord1D, topCoord1D);
            }
            if (bottomCoord1D != -1 &&
                (sitesInfo[bottomCoord1D] & OPEN_SITE) == OPEN_SITE) {
                comp0 = grid1D.find(bottomCoord1D);
                if ((sitesInfo[comp0] & CONNECTED_TOP) == CONNECTED_TOP)
                    sitesInfo[newCoord1D] |= CONNECTED_TOP;
                if ((sitesInfo[comp0] & CONNECTED_BOTTOM) == CONNECTED_BOTTOM)
                    sitesInfo[newCoord1D] |= CONNECTED_BOTTOM;
                grid1D.union(newCoord1D, bottomCoord1D);
            }
            if (leftCoordID != -1 &&
                (sitesInfo[leftCoordID] & OPEN_SITE) == OPEN_SITE) {
                comp0 = grid1D.find(leftCoordID);
                if ((sitesInfo[comp0] & CONNECTED_TOP) == CONNECTED_TOP)
                    sitesInfo[newCoord1D] |= CONNECTED_TOP;
                if ((sitesInfo[comp0] & CONNECTED_BOTTOM) == CONNECTED_BOTTOM)
                    sitesInfo[newCoord1D] |= CONNECTED_BOTTOM;
                grid1D.union(leftCoordID, newCoord1D);
            }
            if (rightCoordID != -1 &&
                (sitesInfo[rightCoordID] & OPEN_SITE) == OPEN_SITE) {
                comp0 = grid1D.find(rightCoordID);
                if ((sitesInfo[comp0] & CONNECTED_TOP) == CONNECTED_TOP)
                    sitesInfo[newCoord1D] |= CONNECTED_TOP;
                if ((sitesInfo[comp0] & CONNECTED_BOTTOM) == CONNECTED_BOTTOM)
                    sitesInfo[newCoord1D] |= CONNECTED_BOTTOM;
                grid1D.union(rightCoordID, newCoord1D);
            }

            // Finally, we get the root of the component where the new site is
            // located after all the unions, and if any neighbor was connected
            // to top or bottom, we update the root as well.
            comp0 = grid1D.find(newCoord1D);
            if ((sitesInfo[newCoord1D] & CONNECTED_TOP) == CONNECTED_TOP)
                sitesInfo[comp0] |= CONNECTED_TOP;
            if ((sitesInfo[newCoord1D] & CONNECTED_BOTTOM) == CONNECTED_BOTTOM)
                sitesInfo[comp0] |= CONNECTED_BOTTOM;

            // If root is connected top and bottom, the system has percolated
            if (!sistemPercolated &&
                (sitesInfo[comp0] & CONNECTED_TOP) == CONNECTED_TOP &&
                (sitesInfo[comp0] & CONNECTED_BOTTOM) == CONNECTED_BOTTOM)
                sistemPercolated = true;
        }
    }

    // is site (row i, column j) open?
    public boolean isOpen(int i, int j) {
        int newCoord1D = from2Dto1D(i, j);
        return ((sitesInfo[newCoord1D] & OPEN_SITE) == OPEN_SITE);
    }

    // is site (row i, column j) full?
    public boolean isFull(int i, int j) {
        int newCoord1D = from2Dto1D(i, j);

        // If it is closed, return false directly. Otherwise, check if the
        // root of its component is connected to the top
        if ((sitesInfo[newCoord1D] & OPEN_SITE) != OPEN_SITE) {
            return false;
        } else {
            int comp0 = grid1D.find(newCoord1D);
            return ((sitesInfo[comp0] & CONNECTED_TOP) == CONNECTED_TOP);
        }
    }

    // does the system percolate?
    public boolean percolates() {
        return sistemPercolated;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        Percolation test = new Percolation(3);
        test.open(1, 1);
        test.open(2, 1);
        test.open(2, 3);
        test.open(3, 3);
        if (test.percolates())
            System.out.println("Percolated");
        else
            System.out.println("Not percolated");
        test.open(2, 2);
        if (test.percolates())
            System.out.println("Percolated");
        else
            System.out.println("Not percolated");
    }

    /**
     *   Maps a set of 2D coordinates to 1D, to allow to
     *   make use of WeightedQuickUnionUF's zero based index
     *
     *   Throws a NullPointerException if a is null.
     */
    private int from2Dto1D(int i, int j) {
        // We need to validate the 2D coordinates
        if (i <= 0 || i > gridSize)
            throw new IndexOutOfBoundsException("Row index i out of range");
        if (j <= 0 || j > gridSize)
            throw new IndexOutOfBoundsException("Column index j out of range");

        return ((i - 1) * gridSize + (j - 1));
    }
}
