package com.example.greyson.test1.net;

import com.example.greyson.test1.entity.SafePlaceRes;

import java.util.List;
import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by greyson on 1/4/17.
 */

public interface WSNetService {
    /**
     * SafePlace Request
     */
    @GET("?format=json&suburb=Malvern+East")
    Observable<List<SafePlaceRes>> getSafePlaceData(@QueryMap Map<String, String> params);
}
