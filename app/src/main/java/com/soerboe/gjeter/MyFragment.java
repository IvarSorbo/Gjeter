package com.soerboe.gjeter;

import android.support.v4.app.Fragment;

/**
 * This is used for the observation fragments to ensure that they implement a toJSON method.
 */
public abstract class MyFragment extends Fragment {
    public abstract String toJSON();
}
