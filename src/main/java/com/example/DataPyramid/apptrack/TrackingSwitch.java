package com.example.DataPyramid.apptrack;

/**
 * Class containing a globally tracked boolean to allow the program to continue populating parts of its
 * interface and database.
 */
public class TrackingSwitch {
    /** True if the program is able to continue populating the database and portions of the interface with the user's
     * screen time and applications.  */
    public static volatile boolean continuePopulating = true;
}
