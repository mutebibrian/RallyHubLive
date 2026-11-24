package com.waltsrally.rallyhublive.data.api

import com.waltsrally.rallyhublive.data.models.*
import retrofit2.http.*

interface APIService {

    // ── Auth ──────────────────────────────────────────────────────────────────
    @POST("rally_hub_live.php")
    @Headers("Content-Type: application/json")
    suspend fun login(
        @Query("api") api: String = "login",
        @Body body: LoginRequest
    ): LoginResponse

    // ── Sections ──────────────────────────────────────────────────────────────
    @GET("rally_hub_live.php")
    suspend fun getSections(
        @Query("api") api: String = "sections"
    ): SectionsResponse

    // ── Cars ──────────────────────────────────────────────────────────────────
    @GET("rally_hub_live.php")
    suspend fun getAllCars(
        @Query("api") api: String = "get_all_cars",
        @Query("section_id") sectionId: Int
    ): CarsResponse

    // ── Start marshal ─────────────────────────────────────────────────────────
    @POST("rally_hub_live.php")
    @Headers("Content-Type: application/json")
    suspend fun recordStart(
        @Query("api") api: String = "record_start",
        @Body body: RecordStartRequest
    ): RecordStartResponse

    // ── Admin: set start time ──────────────────────────────────────────────────
    @POST("rally_hub_live.php")
    @Headers("Content-Type: application/json")
    suspend fun setAdminStartTime(
        @Query("api") api: String = "set_admin_start_time",
        @Body body: SetAdminStartRequest
    ): RecordStartResponse

    // ── Time entry (finish) ───────────────────────────────────────────────────
    @POST("rally_hub_live.php")
    @Headers("Content-Type: application/json")
    suspend fun recordTime(
        @Query("api") api: String = "record_ff",
        @Body body: RecordTimeRequest
    ): RecordTimeResponse

    // ── DNF ───────────────────────────────────────────────────────────────────
    @POST("rally_hub_live.php")
    @Headers("Content-Type: application/json")
    suspend fun recordDnf(
        @Query("api") api: String = "record_ff",
        @Body body: RecordDNFRequest
    ): RecordDNFResponse

    // ── Results ───────────────────────────────────────────────────────────────
    @GET("rally_hub_live.php")
    suspend fun getResults(
        @Query("api") api: String = "recent_times",
        @Query("section_id") sectionId: Int
    ): ResultsResponse

    @GET("rally_hub_live.php")
    suspend fun getBoard(
        @Query("api") api: String = "get_board",
        @Query("section_id") sectionId: Int
    ): BoardResponse

    @GET("rally_hub_live.php")
    suspend fun driverResults(
        @Query("api") api: String = "driver_results"
    ): ResultsResponse
}
