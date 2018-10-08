package com.soerboe.gjeter;

import android.support.v4.app.Fragment;

/**
 * This is used for the observation fragments to force a toJSON method.
 */
public abstract class MyFragment extends Fragment {
    public abstract String toJSON();
}
