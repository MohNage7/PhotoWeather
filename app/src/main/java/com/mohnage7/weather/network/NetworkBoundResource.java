package com.mohnage7.weather.network;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.mohnage7.weather.network.ApiResponse;
import com.mohnage7.weather.base.DataWrapper;
import com.mohnage7.weather.db.AppExecutors;

// ResultType: Type for the DataWrapper data. (database cache)
// RequestType: Type for the API response. (network request)
public abstract class NetworkBoundResource<ResultType, RequestType> {

    private AppExecutors appExecutors;
    private MediatorLiveData<DataWrapper<ResultType>> results = new MediatorLiveData<>();

    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        init();
    }

    private void init() {

        // update LiveData for loading status
        results.setValue(DataWrapper.loading(null));

        // observe LiveData source from local db
        final LiveData<ResultType> dbSource = loadFromDb();

        results.addSource(dbSource, ResultType -> {

            results.removeSource(dbSource);

            if (shouldFetch(ResultType)) {
                // get data from the network
                fetchFromNetwork(dbSource);
            } else {
                results.addSource(dbSource, ResultType1 -> setValue(DataWrapper.success(ResultType1)));
            }
        });
    }

    /**
     * 1) observe local db
     * 2) if <condition/> query the network
     * 3) stop observing the local db
     * 4) insert new data into local db
     * 5) begin observing local db again to see the refreshed data from network
     *
     * @param dbSource
     */
    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        results.addSource(dbSource, ResultType -> setValue(DataWrapper.loading(ResultType)));

        final LiveData<ApiResponse<RequestType>> apiResponse = createCall();

        results.addSource(apiResponse, RequestTypeApiResponse -> {
            results.removeSource(dbSource);
            results.removeSource(apiResponse);

            if (RequestTypeApiResponse instanceof ApiResponse.ApiSuccessResponse) {
                // success
                appExecutors.diskIO().execute(() -> {
                    // save the response to the local db
                    saveCallResult((RequestType) processResponse((ApiResponse.ApiSuccessResponse) RequestTypeApiResponse));
                    appExecutors.mainThread().execute(() -> results.addSource(loadFromDb(), ResultType -> setValue(DataWrapper.success(ResultType))));
                });
            } else if (RequestTypeApiResponse instanceof ApiResponse.ApiEmptyResponse) {
                // empty response
                appExecutors.mainThread().execute(() -> results.addSource(loadFromDb(), ResultType -> setValue(DataWrapper.success(ResultType))));
            } else if (RequestTypeApiResponse instanceof ApiResponse.ApiErrorResponse) {
                // api error
                results.addSource(dbSource, ResultType -> setValue(
                        DataWrapper.error(
                                ((ApiResponse.ApiErrorResponse) RequestTypeApiResponse).getErrorMessage(),
                                ResultType
                        )
                ));
            }
        });
    }

    private ResultType processResponse(ApiResponse.ApiSuccessResponse response) {
        return (ResultType) response.getBody();
    }

    private void setValue(DataWrapper<ResultType> newValue) {
        if (results.getValue() != newValue) {
            results.setValue(newValue);
        }
    }

    // Called to save the result of the API response into the database.
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    // Called to get the cached data from the database.
    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    // Called to create the API call.
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    // Returns a LiveData object that represents the DataWrapper that's implemented
    // in the base class.
    public final LiveData<DataWrapper<ResultType>> getAsLiveData() {
        return results;
    }
}




