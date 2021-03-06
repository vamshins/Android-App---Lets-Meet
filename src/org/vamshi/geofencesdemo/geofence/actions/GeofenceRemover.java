package org.vamshi.geofencesdemo.geofence.actions;

import java.util.Arrays;
import java.util.List;

import org.vamshi.geofencesdemo.R;
import org.vamshi.geofencesdemo.geofence.callbacks.GeofenceCallbacks;
import org.vamshi.geofencesdemo.geofence.util.GeofenceUtils;
import org.vamshi.geofencesdemo.geofence.util.GeofenceUtils.AddType;
import org.vamshi.geofencesdemo.ui.activities.MainActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;

public class GeofenceRemover implements ConnectionCallbacks, OnConnectionFailedListener {

    private final String LOG_TAG = GeofenceRemover.class.getSimpleName();

    // Storage for a context from the calling client
    private Context mContext;
    private GeofenceCallbacks mListener;

    private String mPlaceId;

    // Stores the current list of geofences
    private List<String> mCurrentGeofenceIds;

    // Stores the current instantiation of the location client
    private GoogleApiClient mLocationClient;

    // The PendingIntent sent in removeGeofencesByIntent
    private PendingIntent mCurrentIntent;

    private AddType mAddType;

    /*
     * Record the type of removal. This allows continueRemoveGeofences to call
     * the appropriate removal request method.
     */
    private GeofenceUtils.RemoveType mRequestType;

    /*
     * Flag that indicates whether an add or remove request is underway. Check
     * this flag before attempting to start a new request.
     */
    private boolean mInProgress;

    /**
     * Default constructor
     * 
     * @param context
     */
    public GeofenceRemover(final Context context, final GeofenceCallbacks listener) {
        mContext = context;
        mListener = listener;
    }

    /**
     * Set the "in progress" flag from a caller. This allows callers to re-set a
     * request that failed but was later fixed.
     * 
     * @param flag
     *            Turn the in progress flag on or off.
     */
    public void setInProgressFlag(final boolean flag) {
        mInProgress = flag;
    }

    /**
     * Get the current in progress status.
     * 
     * @return The current value of the in progress flag.
     */
    public boolean getInProgressFlag() {
        return mInProgress;
    }

    /**
     * Request a connection to Location Services. This call returns immediately,
     * but the request is not complete until onConnected() or
     * onConnectionFailure() is called.
     */
    private void requestConnection() {
        getLocationClient().connect();
    }

    /**
     * Get the current location client, or create a new one if necessary.
     * 
     * @return A LocationClient object
     */
    private GoogleApiClient getLocationClient() {
        if (mLocationClient == null) {
            mLocationClient = new GoogleApiClient.Builder(mContext)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();
        }
        return mLocationClient;
    }

    /**
     * Remove the geofences associated with a PendIntent. The PendingIntent is
     * the one used in the request to add the geofences; all geofences in that
     * request are removed. To remove a subset of those geofences, call
     * removeGeofencesById().
     * 
     * @param requestIntent
     *            The PendingIntent used to request the geofences
     */
    public void removeGeofencesByIntent(final PendingIntent requestIntent) {

        // If a removal request is not in progress, continue
        if (!mInProgress) {
            // Set the request type, store the List, and request a location
            // client connection.
            mRequestType = GeofenceUtils.RemoveType.INTENT;
            mCurrentIntent = requestIntent;
            requestConnection();

            // If a removal request is in progress, throw an exception
        } else {

            throw new UnsupportedOperationException();
        }
    }

    /**
     * When the request to remove geofences by PendingIntent returns, handle the
     * result.
     * 
     * @param statusCode
     *            the code returned by Location Services
     * @param requestIntent
     *            The Intent used to request the removal.
     */
   // @Override
    public void onRemoveGeofencesByPendingIntentResult(final int statusCode, final PendingIntent requestIntent) {

       /* // If removing the geofences was successful
        if (statusCode == LocationStatusCodes.SUCCESS) {
            Log.d(LOG_TAG, "Remove geofences by pending intent");

            mListener.removeGeofenceListener(mPlaceId, mAddType);
        } else {

            Log.e(LOG_TAG, "Removing geofences failed");

            mListener.errorGeofenceListener(mPlaceId,
                    mContext.getString(R.string.geofences_removed_fails, statusCode));
        }

        // Disconnect the location client
        requestDisconnection();*/
    }

    /**
     * Remove the geofences in a list of geofence IDs. To remove all current
     * geofences associated with a request, you can also call
     * removeGeofencesByIntent.
     * <p>
     * <b>Note: The List must contain at least one ID, otherwise an Exception is
     * thrown</b>
     * 
     * @param geofenceIds
     *            A List of geofence IDs
     */
    public void removeGeofencesById(final List<String> geofenceIds, final AddType addType, final String placeId)
            throws IllegalArgumentException, UnsupportedOperationException {

        // If the List is empty or null, throw an error immediately
        if ((geofenceIds == null) || (geofenceIds.size() == 0)) {
            throw new IllegalArgumentException();
            // Set the request type, store the List, and request a location
            // client connection.
        } else {
            // If a removal request is not already in progress, continue
            if (!mInProgress) {
                mPlaceId = placeId;
                mAddType = addType;
                mRequestType = GeofenceUtils.RemoveType.LIST;
                mCurrentGeofenceIds = geofenceIds;
                requestConnection();

                // If a removal request is in progress, throw an exception
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * When the request to remove geofences by IDs returns, handle the result.
     * 
     * @param statusCode
     *            The code returned by Location Services
     * @param geofenceRequestIds
     *            The IDs removed
     */
    //@Override
    public void onRemoveGeofencesByRequestIdsResult(final int statusCode, final String[] geofenceRequestIds) {

        // If removing the geocodes was successful
        if (LocationStatusCodes.SUCCESS == statusCode) {

            // Create a message containing all the geofence IDs removed.
            Log.d(LOG_TAG, "Removed the following ids: " + Arrays.toString(geofenceRequestIds));

            mListener.removeGeofenceListener(mPlaceId, mAddType);
        } else {
            Log.e(LOG_TAG,
                    "We had a problem (code): " + statusCode + "\nIds: " + Arrays.toString(geofenceRequestIds));
            mListener.errorGeofenceListener(mPlaceId, mContext.getString(R.string.geofences_removed_fails));
        }
        // Disconnect the location client
        requestDisconnection();
    }

    /**
     * Get a location client and disconnect from Location Services
     */
    private void requestDisconnection() {

        // A request is no longer in progress
        mInProgress = false;

        getLocationClient().disconnect();
        /*
         * If the request was done by PendingIntent, cancel the Intent. This
         * prevents problems if the client gets disconnected before the
         * disconnection request finishes; the location updates will still be
         * cancelled.
         */
        if (mRequestType == GeofenceUtils.RemoveType.INTENT) {
            mCurrentIntent.cancel();
        }

    }

    /*
     * Implementation of OnConnectionFailedListener.onConnectionFailed If a
     * connection or disconnection request fails, report the error
     * connectionResult is passed in from Location Services
     */
    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {

        // A request is no longer in progress
        mInProgress = false;

        /*
         * Google Play services can resolve some errors it detects. If the error
         * has a resolution, try sending an Intent to start a Google Play
         * services activity that can resolve error.
         */
        if (connectionResult.hasResolution()) {

            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult((MainActivity) mContext,
                        GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }

            /*
             * If no resolution is available, put the error code in an error
             * Intent and broadcast it back to the main Activity. The Activity
             * then displays an error dialog. is out of date.
             */
        } else {
            mListener.errorGeofenceListener(mPlaceId, mContext.getString(R.string.geofences_removed_fails));
        }
    }

    /*
     * Called by Location Services once the location client is connected.
     * 
     * Continue by removing the requested geofences.
     */
    @Override
    public void onConnected(final Bundle arg0) {

        Log.d(LOG_TAG, "Connected");
        // Continue the request to remove the geofences
        continueRemoveGeofences();
    }

    /**
     * Once the connection is available, send a request to remove the Geofences.
     * The method signature used depends on which type of remove request was
     * originally received.
     */
    private void continueRemoveGeofences() {
        switch (mRequestType) {

        // If removeGeofencesByIntent was called
            case INTENT:
                LocationServices.GeofencingApi.removeGeofences(mLocationClient,mCurrentIntent);
                mListener.removeGeofenceListener(mPlaceId, mAddType);
                break;

            // If removeGeofencesById was called
            case LIST:
            	LocationServices.GeofencingApi.removeGeofences(mLocationClient,mCurrentGeofenceIds);
            	mListener.removeGeofenceListener(mPlaceId, mAddType);
                break;
        }
    }

   /* 
     * Called by Location Services if the connection is lost.
     
    @Override
    public void onDisconnected() {

       
    }*/

	@Override
	public void onConnectionSuspended(int arg0) {
		 // A request is no longer in progress
        mInProgress = false;

        // In debug mode, log the disconnection
        Log.d(LOG_TAG, "Disconnected");

        // Destroy the current location client
        mLocationClient = null;
		
	}

}
